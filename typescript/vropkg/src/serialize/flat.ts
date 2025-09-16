/*-
 * #%L
 * vropkg
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
import * as fs from "fs-extra";
import * as glob from "glob";
import * as path from "path";
import getLogger from "../logger";
import * as t from "../types";
import * as xmlbuilder from "xmlbuilder";
import {v5 as uuidv5} from "uuid";
import * as s from "../security";
import * as p from "../packaging"
import type { ArchiverWithDone } from "../packaging";
import { exist, isDirectory } from "../util";
import { getPackageName, serialize, zipbundle, getActionXml, saveOptions, xmlOptions, infoOptions } from "./util"
import * as archiver from "archiver";
import { decode } from "../encoding";
import * as xmlDoc from "xmldoc";
import { DEFAULT_ENCODING, FORM_ITEM_TEMPLATE, VSO_RESOURCE_INF } from "../constants";

const
    DUNES_META_INF = "dunes-meta-inf",
    CERTIFICATES = "certificates",
    ELEMENTS = "elements",
    CATEGORIES = "categories",
    CONTENT_SIGNATURE = "content-signature",
    DATA = "data",
    BUNDLE = "bundle",
    DATA_ALLOWEDOPERATIONS = "attribute_allowedOperations",
    DATA_DESCRIPTION = "attribute_description",
    DATA_ID = "attribute_id",
    DATA_MIMETYPE = "attribute_mimetype",
    DATA_NAME = "attribute_name",
    DATA_VERSION = "attribute_version",
    INFO = "info",
    TAGS = "tags",
    SIGNATURES = "signatures",
    INPUT_FORM = "input_form_",
    INPUT_FORM_ITEM = "input_form_item";

const initializeContext = (target: string) => {
    // Clean up before start
    fs.pathExistsSync(target) && fs.removeSync(target);

    const bundle = async (name: string): Promise<void> => {
        const arch = p.archive(path.join(target, name));
        [CERTIFICATES, ELEMENTS, SIGNATURES].forEach(folder => arch.directory(path.join(target, folder), folder));
        arch.append(fs.createReadStream(path.join(target, DUNES_META_INF)), { name: DUNES_META_INF });
        arch.finalize();
        await (arch as ArchiverWithDone).done;
    }
    const store = serialize(target);

    return {
        target: target,
        dunesMetaInf: store(DUNES_META_INF),
        certificates: (subjectName: string) => store(path.join(CERTIFICATES, subjectName)),
        elements: (elementId: string) => initializeElementContext(path.join(target, ELEMENTS, elementId)),
        signatures: (signature: string) => store(path.join(SIGNATURES, signature)),
        save: bundle
    }
}

const initializeElementContext = (target: string) => {
    const store = serialize(target);
    const storezipped = zipbundle(target);

    return {
        target,
        categories: store(CATEGORIES),
        contentSignature: store(CONTENT_SIGNATURE),
        data: store(DATA),
        bundle: storezipped(BUNDLE),
        dataComplex: serializeFlatElementData(path.join(target, DATA)),
        info: store(INFO),
        tags: store(TAGS),
        form: store(INPUT_FORM),
        formItems: store(INPUT_FORM_ITEM)
    }
}

const serializeFlatElementData = (target: string) => {
    let bundle: ArchiverWithDone;

    const initBundle = () => {
        bundle = p.archive(target) as ArchiverWithDone;
    }
    const append = (name: string) => (data: any) => {
        if (data) {
            bundle.append(Buffer.from(data, 'utf8'), { name: `${VSO_RESOURCE_INF}/${name}` })
        } else {
			getLogger().debug(`Element not available ${VSO_RESOURCE_INF}/${name}`);
        }
    };

    return {
        target: target,
        initBundle: initBundle,
        allowedOperations: append(DATA_ALLOWEDOPERATIONS),
        description: append(DATA_DESCRIPTION),
        id: append(DATA_ID),
        mimetype: append(DATA_MIMETYPE),
        name: append(DATA_NAME),
        version: append(DATA_VERSION),
        data: append(DATA),
        save: async () => { bundle.finalize(); await bundle.done; }
    }
}

const serializeFlatCertificate = (context: any, pkg: t.VroPackageMetadata): Array<Promise<void>> => {
    const future = [];

    pkg.certificate.chain.forEach((pem, subject) => {
        future.push(context.certificates(`${subject}.cer`)(s.pemToDer(pem), { encoding: "binary" }));
    })

    return future;
}

const serializeFlatDunesMetadata = async (context: any, pkg: t.VroPackageMetadata): Promise<void> => {
    let node = xmlbuilder.create("properties", xmlOptions)
    node.dtd("", "http://java.sun.com/dtd/properties.dtd");
    node.ele("comment").text("UTF-16").up()
        // Generates Package ID based on the FQ Package Name
        .ele("entry").att("key", "pkg-id").text(uuidv5('http://' + getPackageName(pkg), uuidv5.URL)).up()
        .ele("entry").att("key", "pkg-name").text([pkg.groupId, pkg.artifactId + "-" + pkg.version].join(".")).up()
        .ele("entry").att("key", "pkg-description").text(pkg.description || "Built by Build Tools for VMware Aria").up()
        .ele("entry").att("key", "pkg-signer").text(pkg.certificate.subject).up()
        .ele("entry").att("key", "pkg-owner").text(pkg.certificate.subject)

    return context.dunesMetaInf(node.end(saveOptions));
}

// Enhanced concurrency limiter with robust defensive mechanisms
const limitConcurrency = (concurrency: number) => {
    let active = 0;
    let completed = 0;
    let failed = 0;
    const queue: Array<() => void> = [];
    const startTime = Date.now();
    
    const next = () => {
        active--;
        const fn = queue.shift();
        if (fn) fn();
    };
    
    // Watchdog to detect if we're completely stuck
    const watchdogInterval = setInterval(() => {
        const runtime = Date.now() - startTime;
        if (runtime > 60000 && active === 0 && queue.length === 0) {
            getLogger().warn(`Concurrency limiter appears idle after ${Math.round(runtime/1000)}s - cleared watchdog`);
            clearInterval(watchdogInterval);
        } else if (runtime > 300000) { // 5 minutes total
            getLogger().error(`Concurrency limiter stuck for 5+ minutes! Active: ${active}, Queue: ${queue.length}, Completed: ${completed}, Failed: ${failed}`);
            // Force clear the queue to prevent indefinite hanging
            while (queue.length > 0) {
                const fn = queue.shift();
                if (fn) {
                    try { fn(); } catch (e) { getLogger().debug('Queue cleanup error:', e); }
                }
            }
            clearInterval(watchdogInterval);
        }
    }, 30000);
    
    return <T>(fn: () => Promise<T>): Promise<T> => new Promise<T>((resolve, reject) => {
        const operationStartTime = Date.now();
        
        const run = () => {
            active++;
            
            // Shorter timeout per operation, but log more aggressively 
            const timeoutId = setTimeout(() => {
                const duration = Date.now() - operationStartTime;
                getLogger().error(`Operation timed out after ${duration}ms (active: ${active}, queue: ${queue.length}, completed: ${completed})`);
                failed++;
                reject(new Error(`Operation timed out after ${duration}ms`));
                next();
            }, 45000); // 45 second timeout per operation
            
            // Wrap the original function with additional error handling
            Promise.resolve()
                .then(() => fn())
                .then((v) => { 
                    clearTimeout(timeoutId);
                    completed++;
                    if (completed % 20 === 0) {
                        getLogger().debug(`Concurrency stats: completed=${completed}, failed=${failed}, active=${active}, queue=${queue.length}`);
                    }
                    resolve(v); 
                    next(); 
                })
                .catch((e) => { 
                    clearTimeout(timeoutId);
                    failed++;
                    const duration = Date.now() - operationStartTime;
                    getLogger().debug(`Operation failed after ${duration}ms (active: ${active}, queue: ${queue.length}):`, e.code || e.message || 'Unknown error');
                    reject(e); 
                    next(); 
                });
        };
        
        if (active < concurrency) {
            run();
        } else {
            queue.push(run);
            if (queue.length % 50 === 0) {
                getLogger().debug(`Queue growing: ${queue.length} operations waiting, ${active} active`);
            }
        }
    });
};

const serializeFlatElements = (context: any, pkg: t.VroPackageMetadata): Array<Promise<void>> => {
    // Use extremely conservative concurrency to prevent EMFILE errors
    const defaultConc = process.platform === 'win32' ? 1 : 1;
    const concurrency = Math.max(1, Number(process.env.VROPKG_IO_CONCURRENCY) || defaultConc);
    const limit = limitConcurrency(concurrency);
    getLogger().info(`Processing ${pkg.elements.length} elements with concurrency: ${concurrency}`);
    
    return pkg.elements.map(() => null).map((_, idx) => limit(async () => {
        const element = pkg.elements[idx];
        const elementStartTime = Date.now();
        getLogger().debug(`Processing element ${idx + 1}/${pkg.elements.length}: ${element.name} (${element.type})`);
        const elementContext = context.elements(element.id)
        
        try {
            // Add per-element timeout protection
            const elementTimeout = new Promise<never>((_, reject) => {
                setTimeout(() => {
                    const elapsed = Date.now() - elementStartTime;
                    reject(new Error(`Element processing timed out after ${elapsed}ms for ${element.name}`));
                }, 120000); // 2 minutes per element
            });
            
            const elementWork = (async () => {
                // Perform element operations sequentially to minimize concurrent file handles
                await serializeFlatElementInfo(elementContext, pkg, element);
                await serializeFlatElementCategory(elementContext, element);
                await serializeFlatElementContent(elementContext, pkg, element);
                await serializeFlatElementBundle(elementContext, element);
                await serializeFlatElementTags(elementContext, element);
                await serializeFlatElementInputForm(elementContext, element);
                await serializeFlatElementInputFormItems(elementContext, element);

                return await exportPackageElementContentSignature(elementContext, pkg);
            })();
            
            const result = await Promise.race([elementWork, elementTimeout]);
            const duration = Date.now() - elementStartTime;
            getLogger().debug(`Completed element ${idx + 1}/${pkg.elements.length}: ${element.name} (${duration}ms)`);
            return result;
        } catch (error) {
            const duration = Date.now() - elementStartTime;
            getLogger().error(`Failed processing element ${element.name} (${element.type}) after ${duration}ms:`, error);
            throw error;
        }
    }));
};

const serializeFlatElementInfo = async (context: any, pkg: t.VroPackageMetadata, element: t.VroNativeElement): Promise<void> => {
    let node = xmlbuilder.create("properties", infoOptions)
    node.dtd("", "http://java.sun.com/dtd/properties.dtd")
    node.ele("comment").text("Exported from PSCoE o11n-convert").up()
        .ele("entry").att("key", "type").text(element.type.toString()).up()
        .ele("entry").att("key", "signature-owner").text(pkg.certificate.subject).up()
        .ele("entry").att("key", "id").text(element.id)

    return context.info(node.end(saveOptions));
}

const serializeFlatElementInputForm = async (context: any, element: t.VroNativeElement): Promise<void> => {
    if (element.form?.data) {
        const buffer = Buffer.from('\ufeff' + JSON.stringify(element.form?.data), "utf16le").swap16();
        return context.form(buffer, DEFAULT_ENCODING);
    }
}

const serializeFlatElementInputFormItems = async (context: any, element: t.VroNativeElement): Promise<void> => {
    if (element.formItems && Array.isArray(element.formItems) && element.formItems.length > 0) {
        getLogger().debug(`Processing ${element.formItems.length} form items for element: ${element.name}`);
        
        // Process form items sequentially to avoid too many concurrent file operations
        for (const formItem of element.formItems) {
            const buffer = Buffer.from('\ufeff' + JSON.stringify(formItem.data), "utf16le").swap16();
            const formName: string = formItem.name || "";
            const fileName = FORM_ITEM_TEMPLATE.replace("{{formName}}", formName);
            await context.formItems(buffer, DEFAULT_ENCODING, fileName);
        }
    }
}

const serializeFlatElementCategory = async (context: any, element: t.VroNativeElement): Promise<void> => {
    const categories = xmlbuilder.begin().ele(CATEGORIES);
    (element.type == t.VroElementType.ScriptModule
        ? [element.categoryPath.join(".")]
        : element.categoryPath
    ).forEach((path) => {
        const realName = path.replace(/\//g, "");
        categories.ele("category").att("name", realName).ele("name").dat(realName)
    });

    return context.categories(Buffer.from('\ufeff' + categories.end(saveOptions), "utf16le").swap16(), DEFAULT_ENCODING);
}

const serializeFlatElementBundle = async (context: any, element: t.VroNativeElement): Promise<void> => {
    if (!element?.action?.bundle) {
        return;
    }
    if (!exist(element.action.bundle.contentPath)) {
        throw new Error(`Source script bundle "${element.action.bundle.contentPath}" does not exist for element `
            + `${element.name}" of type "${element.type}", category "${element.categoryPath}", id: "${element.id}". `
            + `Cannot save script bundle.`);
    }
    let isDir = isDirectory(element.action.bundle.contentPath);

    return context.bundle(element.action.bundle.contentPath, isDir);
}

const serializeFlatElementTags = async (context: any, element: t.VroNativeElement): Promise<void> => {
    if (!element.tags?.length) {
        getLogger().debug(`Element does not have tags ${element.name}`);
        return;
    }
    let node = xmlbuilder.create("tags", xmlOptions);
    element.tags.forEach(name => {
        node.ele("tag").att("name", name).att("global", true).up()
    })
    const contentBuffer = Buffer.from('\ufeff' + node.end(saveOptions), "utf16le").swap16();

    return context.tags(contentBuffer, DEFAULT_ENCODING);
}

const serializeFlatElementContent = async (context: any, pkg: t.VroPackageMetadata, element: t.VroNativeElement): Promise<void> => {
    const pkgVersion = pkg.version.split("-SNAPSHOT")[0];

    let content: string | null = null;
    switch (element.type) {
        case t.VroElementType.ResourceElement: {
            const data = context.dataComplex;
            const value = (name: string) => element.attributes[name.replace('attribute_', '')]
            // Order is important for ZIP checksum
            data.initBundle();
            data.id(value(DATA_ID));
            data.name(value(DATA_NAME));
            data.version(pkgVersion);
            data.description(value(DATA_DESCRIPTION));
            data.mimetype(value(DATA_MIMETYPE));
            data.allowedOperations(value(DATA_ALLOWEDOPERATIONS));
            {
                const fileBuf = await fs.readFile(element.dataFilePath);
                data.data(Buffer.from(fileBuf));
            }

            return data.save();
        }
        case t.VroElementType.ScriptModule: {
            content = getActionXml(element.id, element.name, element.description, element.action);
            break;
        }
        case t.VroElementType.ActionEnvironment: {
            return context.data(await fs.readFile(element.dataFilePath));
        }
        default: {
        content = decode(await fs.readFile(element.dataFilePath));
        }
    }
    // Update vRO elements with package version
    let xmlContent = new xmlDoc.XmlDocument(content);
    xmlContent.attr["version"] = pkgVersion;
    const contentBuffer = Buffer.from('\ufeff' + xmlContent.toString({ compressed: true }), "utf16le").swap16();

    return context.data(contentBuffer, DEFAULT_ENCODING);
}

const exportPackageElementContentSignature = async (context: any, pkg: t.VroPackageMetadata): Promise<void> => {
    let data = await fs.readFile(path.join(context.target, DATA));

    return context.contentSignature(s.sign(data, pkg.certificate));
}

const serializeFlatSignatures = async (context: any, pkg: t.VroPackageMetadata): Promise<void> => {
    const target = context.target;
    const files = glob.sync((target + "/**/*").replace(/[\\/]+/gm, path.posix.sep), { nodir: true });
    // Use very conservative concurrency for Windows due to low file descriptor limits
    const defaultConc = process.platform === 'win32' ? 1 : 1;
    const concurrency = Math.max(1, Number(process.env.VROPKG_IO_CONCURRENCY) || defaultConc);
    const limit = limitConcurrency(concurrency);

    getLogger().info(`Processing ${files.length} files for signatures with concurrency: ${concurrency}`);
    let processedCount = 0;
    let lastProgressTime = Date.now();
    
    const processFile = async (file: string): Promise<void> => {
        const fileStartTime = Date.now();
        try {
            const location = path.normalize(file).replace(target, "");
            getLogger().debug(`Processing signature for: ${location}`);
            const buffer = await fs.readFile(file);
            const data = s.sign(buffer, pkg.certificate);
            await context.signatures(location)(data);
            processedCount++;
            
            const now = Date.now();
            if (processedCount % 10 === 0 || (now - lastProgressTime) > 30000) {
                const duration = now - fileStartTime;
                getLogger().info(`Processed ${processedCount}/${files.length} signatures (last file took ${duration}ms)`);
                lastProgressTime = now;
            }
        } catch (error) {
            const duration = Date.now() - fileStartTime;
            getLogger().error(`Failed to process signature for ${file} after ${duration}ms:`, error);
            throw error;
        }
    };
    
    // Add overall timeout for the entire signatures process
    const signaturesStartTime = Date.now();
    const maxSignaturesTime = files.length * 1000 + 300000; // 1 second per file + 5 minutes buffer
    
    const signaturesPromise = Promise.all(files.map(file => limit(() => processFile(file))));
    const timeoutPromise = new Promise<never>((_, reject) => {
        setTimeout(() => {
            const elapsed = Date.now() - signaturesStartTime;
            reject(new Error(`Signatures processing timed out after ${elapsed}ms. Expected max: ${maxSignaturesTime}ms`));
        }, maxSignaturesTime);
    });
    
    await Promise.race([signaturesPromise, timeoutPromise]);
    getLogger().info(`Completed processing all ${processedCount} signatures`);
}

const serializeFlat = async (pkg: t.VroPackageMetadata, targetPath: string) => {
    const context = initializeContext(targetPath);
    await Promise.all([
        ...serializeFlatCertificate(context, pkg),
        serializeFlatDunesMetadata(context, pkg),
        ...serializeFlatElements(context, pkg)])

    await serializeFlatSignatures(context, pkg);
    return context.save(getPackageName(pkg));
}

export { serializeFlat };

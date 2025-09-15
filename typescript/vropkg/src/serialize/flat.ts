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
import { exist, isDirectory } from "../util";
import { getPackageName, serialize, zipbundle, getActionXml, saveOptions, xmlOptions, infoOptions } from "./util"
import * as archiver from "archiver";
import { Buffer } from 'buffer';
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
        const arch = await p.archive(path.join(target, name));
        [CERTIFICATES, ELEMENTS, SIGNATURES].forEach(folder => arch.directory(path.join(target, folder), folder));
        return arch.append(fs.createReadStream(path.join(target, DUNES_META_INF)), { name: DUNES_META_INF }).finalize();
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
    let archiver: p.ManagedArchiver | undefined;

    const createBundle = async () => {
        archiver = await p.archive(target);
        return archiver;
    };

    const append = (name: string) => async (data: any) => {
        if (!archiver) {
            throw new Error('Bundle not initialized. Call initBundle first.');
        }
        
        if (data) {
            const buffer = data instanceof Buffer ? data : Buffer.from(data, 'utf8');
            await archiver.append(buffer, { name: `${VSO_RESOURCE_INF}/${name}` });
        } else {
            getLogger().debug(`Element not available ${VSO_RESOURCE_INF}/${name}`);
        }
    };

    return {
        target: target,
        initBundle: async () => {
            try {
                archiver = await createBundle();
            } catch (error) {
                getLogger().error(`Failed to initialize bundle at ${target}: ${error instanceof Error ? error.message : String(error)}`);
                throw error;
            }
        },
        allowedOperations: append(DATA_ALLOWEDOPERATIONS),
        description: append(DATA_DESCRIPTION),
        id: append(DATA_ID),
        mimetype: append(DATA_MIMETYPE),
        name: append(DATA_NAME),
        version: append(DATA_VERSION),
        data: append(DATA),
        save: async () => {
            if (!archiver) {
                throw new Error('Bundle not initialized. Call initBundle first.');
            }
            return archiver.finalize();
        }
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

const MAX_CONCURRENT_ELEMENTS = 5;

/**
 * Process a batch of elements with proper error handling
 */
const processBatch = async (elements: t.VroNativeElement[], context: any, pkg: t.VroPackageMetadata): Promise<void[]> => {
    try {
        const promises = elements.map(async element => {
            try {
                const elementContext = context.elements(element.id);
                // Process each component sequentially for the element
                await serializeFlatElementInfo(elementContext, pkg, element);
                await serializeFlatElementCategory(elementContext, element);
                await serializeFlatElementContent(elementContext, pkg, element);
                await serializeFlatElementBundle(elementContext, element);
                await serializeFlatElementTags(elementContext, element);
                await serializeFlatElementInputForm(elementContext, element);
                await serializeFlatElementInputFormItems(elementContext, element);
                return exportPackageElementContentSignature(elementContext, pkg);
            } catch (error) {
                getLogger().error(`Error processing element ${element.id}: ${error.message}`);
                throw error;
            }
        });
        
        const results = await Promise.all(promises);
        return results;
    } catch (error) {
        getLogger().error(`Error processing batch of elements: ${error.message}`);
        throw error;
    }
};

/**
 * Process elements in controlled batches to prevent file handle exhaustion
 */
const serializeFlatElements = async (context: any, pkg: t.VroPackageMetadata): Promise<void[]> => {
    const elements = pkg.elements;
    const results: void[] = [];
    
    // Process elements in batches of MAX_CONCURRENT_ELEMENTS
    for (let i = 0; i < elements.length; i += MAX_CONCURRENT_ELEMENTS) {
        const batch = elements.slice(i, i + MAX_CONCURRENT_ELEMENTS);
        getLogger().debug(`Processing batch of ${batch.length} elements (${i + 1} to ${i + batch.length} of ${elements.length})`);
        
        try {
            const batchResults = await processBatch(batch, context, pkg);
            results.push(...batchResults);
        } catch (error) {
            getLogger().error(`Failed to process batch starting at element ${i + 1}: ${error.message}`);
            throw error;
        }
    }
    
    return results;
}

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

const serializeFlatElementInputFormItems = async (context: any, element: t.VroNativeElement): Promise<Promise<void>[]> => {
    const promises: Promise<void>[] = [];
    if (element.formItems && Array.isArray(element.formItems)) {
        element.formItems.forEach((formItem: t.VroNativeFormElement) => {
            const buffer = Buffer.from('\ufeff' + JSON.stringify(formItem.data), "utf16le").swap16();
            const formName: string = formItem.name || "";
            const fileName = FORM_ITEM_TEMPLATE.replace("{{formName}}", formName);
            const promise = context.formItems(buffer, DEFAULT_ENCODING, fileName);
            promises.push(promise);
        });
    }

    return promises;
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
            
            try {
                // Initialize the bundle first
                await data.initBundle();
                
                // Order is important for ZIP checksum
                await data.id(value(DATA_ID));
                await data.name(value(DATA_NAME));
                await data.version(pkgVersion);
                await data.description(value(DATA_DESCRIPTION));
                await data.mimetype(value(DATA_MIMETYPE));
                await data.allowedOperations(value(DATA_ALLOWEDOPERATIONS));
                await data.data(Buffer.from(fs.readFileSync(element.dataFilePath)));

                return data.save();
            } catch (error) {
                getLogger().error(`Failed to process ResourceElement ${element.id}: ${error instanceof Error ? error.message : String(error)}`);
                throw error;
            }
        }
        case t.VroElementType.ScriptModule: {
            content = getActionXml(element.id, element.name, element.description, element.action);
            break;
        }
		case t.VroElementType.ActionEnvironment: {
			return context.data(fs.readFileSync(element.dataFilePath));
		}
        default: {
            content = decode(fs.readFileSync(element.dataFilePath));
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

const serializeFlatSignatures = async (context: any, pkg: t.VroPackageMetadata): Promise<void[]> => {
    const promises = [];
    const target = context.target;

	glob.sync((target + "/**/*").replace(/[\\/]+/gm, path.posix.sep), { nodir: true }).forEach(file => {
        const location = path.normalize(file).replace(target, "");
        const data = s.sign(fs.readFileSync(file), pkg.certificate);
        const signature = context.signatures(location)(data);
        promises.push(signature);
    })

    return promises;
}

const serializeFlat = async (pkg: t.VroPackageMetadata, targetPath: string) => {
    const context = initializeContext(targetPath);
    
    // Process certificates
    const certificatePromises = serializeFlatCertificate(context, pkg);
    
    // Process metadata
    const metadataPromise = serializeFlatDunesMetadata(context, pkg);
    
    // Process elements
    const elementsPromise = serializeFlatElements(context, pkg);
    
    // Wait for all processes to complete
    await Promise.all([
        ...certificatePromises,
        metadataPromise,
        elementsPromise
    ]);

    // Process signatures last as they depend on all other files being written
    await serializeFlatSignatures(context, pkg);
    
    // Save and return the final package
    return context.save(getPackageName(pkg));
}

export { serializeFlat };

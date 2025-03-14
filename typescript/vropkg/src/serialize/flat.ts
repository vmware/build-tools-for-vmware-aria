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
import * as winston from "winston";
import * as t from "../types";
import * as xmlbuilder from "xmlbuilder";
import {v5 as uuidv5} from "uuid";
import * as s from "../security";
import * as p from "../packaging"
import { exist, isDirectory } from "../util";
import { getPackageName, serialize, zipbundle, getActionXml, saveOptions, xmlOptions, infoOptions } from "./util"
import * as archiver from "archiver";
import { decode } from "../encoding";
import * as xmlDoc from "xmldoc";
import { DEFAULT_ENCODING, FORM_ITEM_TEMPLATE, VSO_RESOURCE_INF, WINSTON_CONFIGURATION } from "../constants";

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

    const bundle = (name: string): Promise<void> => {
        const arch = p.archive(path.join(target, name));
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
    let bundle: archiver.Archiver;

    const initBundle = () => {
        bundle = p.archive(target);
    }
    const append = (name: string) => (data: any) => {
        if (data) {
            bundle.append(Buffer.from(data, 'utf8'), { name: `${VSO_RESOURCE_INF}/${name}` })
        } else {
            winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Element not available ${VSO_RESOURCE_INF}/${name}`);
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
        save: () => bundle.finalize()
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

const serializeFlatElements = (context: any, pkg: t.VroPackageMetadata): Array<Promise<void>> => {
    return pkg.elements.map(async element => {
        const elementContext = context.elements(element.id)
        await Promise.all([
            serializeFlatElementInfo(elementContext, pkg, element),
            serializeFlatElementCategory(elementContext, element),
            serializeFlatElementContent(elementContext, pkg, element),
            serializeFlatElementBundle(elementContext, element),
            serializeFlatElementTags(elementContext, element),
            serializeFlatElementInputForm(elementContext, element),
            serializeFlatElementInputFormItems(elementContext, element)
        ]);

        return exportPackageElementContentSignature(elementContext, pkg);
    });
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
        winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).debug(`Element does not have tags ${element.name}`);
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
            data.data(Buffer.from(fs.readFileSync(element.dataFilePath)));

            return data.save();
        }
        case t.VroElementType.ScriptModule: {
            content = getActionXml(element.id, element.name, element.description, element.action);
            break;
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
    await Promise.all([
        ...serializeFlatCertificate(context, pkg),
        serializeFlatDunesMetadata(context, pkg),
        ...serializeFlatElements(context, pkg)])

    await serializeFlatSignatures(context, pkg);
    return context.save(getPackageName(pkg));
}

export { serializeFlat };

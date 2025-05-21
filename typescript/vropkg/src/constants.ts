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
export const FORM_SUFFIX = ".form.json";
export const VRO_FORM_TEMPLATE = `{{elementName}}${FORM_SUFFIX}`;
export const RESOURCE_ELEMENT_DEFAULT_VERSION = "0.0.0";
export const ELEMENT_INFO_FILE = "element_info.xml";
export const LOGGER_PREFIX = "vrbt";
export const JSON_DEFAULT_IDENT = 4;
export const JSON_MINOR_IDENT = 2;
export const ZLIB_COMPRESS_LEVEL = 9;
export const DEFAULT_ENCODING = { encoding: "utf16le" };
export const FORM_ITEM_TEMPLATE = "input_form_{{formName}}";
export const VRO_CUSTOM_FORMS_FILENAME_TEMPLATE = `{{elementName}}_${FORM_ITEM_TEMPLATE}${FORM_SUFFIX}`;
export const WORKFLOW_ITEM_INPUT_TYPE = "input";
export const DEFAULT_FORM_NAME = "input_form";
export const DEFAULT_FORM_FILE_NAME = `${DEFAULT_FORM_NAME}_`;
export const VSO_RESOURCE_INF = "VSO-RESOURCE-INF";

export const SAVE_OPTIONS = {
    prettyPrint: true
};

export const XML_OPTIONS = {
    version: "1.0",
    encoding: "UTF-8",
    standalone: false
};

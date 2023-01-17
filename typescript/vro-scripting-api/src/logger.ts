/*
 * #%L
 * vro-scripting-api
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
namespace vroapi {
    export const logger = {
        info,
        warn,
        error,
        debug,
        stdout,
        log,
    };

    const fs: typeof import("fs-extra") = require("fs-extra");
    const path: typeof import("path") = require("path");
    const LOG_DIR = "logs";
    const LOG_FILE_PATH = path.join(LOG_DIR, "scripting.log");
    const STDOUT_FILE_PATH = path.join(LOG_DIR, "stdout.log");

    if (!fs.existsSync(LOG_DIR)) {
        fs.mkdirSync(LOG_DIR);
    }

    function info(text: string) {
        log(text, "INFO");
    }

    function warn(text: string) {
        log(text, "WARN");
    }

    function error(text: string) {
        log(text, "ERROR");
    }

    function debug(text: string) {
        log(text, "DEBUG");
    }

    function stdout(text: string) {
        fs.appendFileSync(STDOUT_FILE_PATH, `${formatDate(new Date())}}\n${text}\n`);
    }

    function log(text: string, severity: string) {
        fs.appendFileSync(LOG_FILE_PATH, `[${formatDate(new Date())}] [${severity}] ${text}\n`);
    }

    function formatDate(date: Date): string {
        return date.toISOString().replace(/T/, " ").replace(/\..+/, "");
    }
}

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

import * as winston from "winston";

const DEFAULT_WINSTON_CONFIGURATION = {
    logLevel: 'info',
    logPrefix: "vrbt",
    logFiles: {
        error: "vrbt-error.log",
        debug: "vrbt-debug.log",
        default: "vrbt.log"
    }
};
/** Loggers cache (winston.loggers.get/add didn't work) */
const LOGGERS: Record<string, winston.Logger> = {};

/**
 * 
 * @param name - logger name. Default is "vrbt"
 * @param opts - logger options - used to create logger if it doesn't exist. By default:
 * - log level is "info", logged into file "vrbt.log"
 * - ERROR level logs are recorded in file "vrbt-error.log"
 * - DEBUG  level logs are recorded in file "vrbt-debug.log"
 * - logging on console is in simple format
 * 
 * @returns logger with the given name (if it exists) and options (when newly created) as singleton
 */
export default function getLogger(name: string = DEFAULT_WINSTON_CONFIGURATION.logPrefix, opts: winston.LoggerOptions = {
	level: DEFAULT_WINSTON_CONFIGURATION.logLevel,
	format: winston.format.json(),
	// defaultMeta: { service: 'user-service' },
	transports: [
		new winston.transports.File({ filename: DEFAULT_WINSTON_CONFIGURATION.logFiles.error, level: 'error' }),
		new winston.transports.File({ filename: DEFAULT_WINSTON_CONFIGURATION.logFiles.debug, level: 'debug' }),
		new winston.transports.File({ filename: DEFAULT_WINSTON_CONFIGURATION.logFiles.default }),
		new winston.transports.Console({ format: winston.format.simple() })
	]
}) {
	if (!LOGGERS[name]) {
		LOGGERS[name] = winston.createLogger(opts);
	}
	return LOGGERS[name];
}

/*-
 * #%L
 * polyglotpkg
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

import winston, { Logger } from 'winston';
import { Writable } from 'stream';

let logger: Logger;
const streams: Array<Writable> = [];

export default function createLogger(verbose: boolean = false, outputStream?: Writable): Logger {
    if (!logger) {
        logger = winston.createLogger({
            level: verbose ? 'debug' : 'info',
            format: winston.format.json(),
            transports: [
                new winston.transports.Console({
                    format: winston.format.simple()
                }),
                // new winston.transports.File({
                //     format: winston.format.simple(),
                //     filename: 'polyglotpkg.log',
                //     options: { flags: 'w' }
                // })
            ]
        });
    }
    if (outputStream && !streams.includes(outputStream)) {
        logger.add(new winston.transports.Stream({
            stream: outputStream,
            format: winston.format.simple(),
        }));
        streams.push(outputStream);
    }
    return logger;
}

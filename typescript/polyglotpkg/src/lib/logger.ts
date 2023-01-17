
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

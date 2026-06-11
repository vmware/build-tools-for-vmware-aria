import { Logger } from "./logger";

export default function testAction(param: string) {
    const logger = new Logger();
    logger.log("outer");

    if (param === "test") {
        const logger = new Logger();
        logger.log("inner");
    }
}
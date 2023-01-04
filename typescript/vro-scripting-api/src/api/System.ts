namespace vroapi {
    const crypto: typeof import("crypto") = require("crypto");
    let context: ExecutionContext;

    /**
     * vRO System intrinsic class representation
     */
    export class System {
        /**
         * Append a path fragment to another. (i.e. C:/temp and subpath/myfile.txt -> C:/temp/subpath/myfile.txt)
         * @param rootPath String,Path or File object
         * @param toAdd Part to add
         */
        static appendToPath(rootPath: any, toAdd: string): string {
            throw new NotSupportedError();
        }

        /**
         * compare two version number with format "v1.v2.v3 .. vn" and return the index of the change with positive and negative value depending what version is greater
         * @param v1 First version
         * @param v2 Second version
         */
        static compareVersionNumber(v1: string, v2: string): number {
            throw new NotSupportedError();
        }

        /**
         * Creates an empty file in the temporary directory of vRealize Orchestrator (app-server/temp), using the given suffix to generate its name.
         * @param suffix The suffix string to be used in generating the file's name; may be null or omitted, in which case the suffix ".tmp" will be used.
         */
        static createTempFile(suffix: string): File {
            throw new NotSupportedError();
        }

        /**
         * Return an string url usable to generate HTTP custom event.
         * @param eventName Custom event name
         * @param secure Use https if true, http otherwise
         */
        static customEventUrl(eventName: string, secure: boolean): URL {
            throw new NotSupportedError();
        }

        /**
         * Return an string url usable to generate HTTP custom event.
         * The 'host' and 'port' parameters are useful if you want to be able to post events from outside a firewall.
         * In this case, indicate the host and port values on the firewall and make the firewall transfer the requests to the VMO server, usually on port 8080.
         * @param eventName Custom event name
         * @param host Host to use in URL
         * @param port Port to use in URL
         * @param secure Use https if true, http otherwise
         */
        static customEventUrlforServer(eventName: string, host: string, port: string, secure: boolean): URL {
            throw new NotSupportedError();
        }

        /**
         * Log a text in the system with a DEBUG threshold.
         * @param text text to log
         */
        static debug(text: string): void {
            logger.debug(text);
        }

        /**
         * Convert a decimal number value to a hexadecimal string.
         * @param value Decimal value
         */
        static decimalToHex(value: number): string {
            throw new NotSupportedError();
        }

        /**
         * Log a text in the system with a ERROR threshold.
         * @param text text to log
         */
        static error(text: string): void {
            logger.error(text);
        }

        /**
         * Extract the directory part from a file path.
         * @param fullPath String,Path or File object
         */
        static extractDirectory(fullPath: any): string {
            throw new NotSupportedError();
        }

        /**
         * Extract the file name part from a file path.
         * @param fullPath String,Path or File object
         */
        static extractFileName(fullPath: any): string {
            throw new NotSupportedError();
        }

        /**
         * Extract the file extension part from a file path.
         * @param fullPath String,Path or File object
         */
        static extractFileNameExtension(fullPath: any): string {
            throw new NotSupportedError();
        }

        /**
         * Extract the file without extension part from a file path.
         * @param fullPath String,Path or File object
         */
        static extractFileNameWithoutExtension(fullPath: any): string {
            throw new NotSupportedError();
        }

        /**
         * Filter non authorized objects for the current user. If the parameter is an object the result will be null if the user is not authorized to use it, if it is an array or property, a recursive check is done and the unauthorized objects are removed.
         * @param obj Object to check
         */
        static filterAuthorized(obj: any): any {
            throw new NotSupportedError();
        }

        /**
         * Format a given number in a human readable binary size format. (i.e 2048 -> 2K Bytes, 4718592 -> 4.5M Bytes)
         * @param value Number to format
         */
        static formatBinaryValue(value: number): string {
            let divCount = 0;
            while (value > 1024 && divCount <= 4) {
                value /= 1024;
            }
            let suffix = "";
            switch (divCount) {
                case 0:
                    suffix = " Bytes";
                    break;
                case 1:
                    suffix = "K Bytes";
                    break;
                case 2:
                    suffix = "M Bytes";
                    break;
                case 3:
                    suffix = "G Bytes";
                    break;
                case 4:
                    suffix = "T Bytes";
                    break;
            }
            return value.toFixed(2) + suffix;
        }

        /**
         * Format a given date in a given format.
         * Date and time formats are specified by date and time pattern  strings. Within date and time pattern strings, unquoted letters from 'A' to 'Z' and from 'a' to 'z' are interpreted as pattern letters representing the components of a date or time string. Text can be quoted using single quotes (') to avoid interpretation. "''" represents a single quote. All other characters are not interpreted; they're simply copied into the output string during formatting or matched against the input string during parsing.
         * The following pattern letters are defined (all other characters from 'A' to 'Z' and from 'a' to 'z' are reserved):
         *
         * G 	Era designator (AD)
         * y 	Year (1996; 96)
         * M 	Month in year (July; Jul; 07)
         * w 	Week in year
         * W 	Week in month
         * D 	Day in year
         * d 	Day in month
         * F 	Day of week in month
         * E 	Day in week 	(Tuesday; Tue)
         * a 	Am/pm marker 	(PM)
         * H 	Hour in day (0-23)
         * k 	Hour in day (1-24)
         * K 	Hour in am/pm (0-11
         * h 	Hour in am/pm (1-12)
         * m 	Minute in hour
         * s 	Second in minute
         * S 	Millisecond
         * z 	Time zone 	General time zone 	(Pacific Standard Time; PST; GMT-08:00)
         * Z 	Time zone 	RFC 822 time zone 	(-0800)
         * Examples
         *
         * The following examples show how date and time patterns are interpreted in the U.S. locale. The given date and time are 2001-07-04 12:08:56 local time in the U.S. Pacific Time time zone.
         * Date and Time Pattern 	Result
         * "yyyy.MM.dd G 'at' HH:mm:ss z" --> "2001.07.04 AD at 12:08:56 PDT"
         * "EEE, MMM d, ''yy" --> "Wed, Jul 4, '01"
         * "h:mm a" --> "12:08 PM"
         * "hh 'o''clock' a, zzzz" --> "12 o'clock PM, Pacific Daylight Time"
         * "K:mm a, z" --> "0:08 PM, PDT"
         * "yyyyy.MMMMM.dd GGG hh:mm aaa" --> "02001.July.04 AD 12:08 PM"
         * "EEE, d MMM yyyy HH:mm:ss Z" --> "Wed, 4 Jul 2001 12:08:56 -0700"
         * "yyMMddHHmmssZ" --> "010704120856-0700"
         * @param aDate Date to format
         * @param pattern Format pattern
         */
        static formatDate(date: Date, pattern: string): string {
            return dateFormat(date, pattern);
        }

        /**
         * Format a given millisecond number in a human readable format
         * @param milisecond Millisecond number to format
         * @param showMili (Optional) Show millisecond if true. Default value is false.
         * @param showZero (Optional) Show leading zero values if true. Default value is false.
         */
        static formatDuration(milisecond: number, showMili: boolean, showZero: boolean): string {
            return milisecond.toString();
        }

        /**
         * Format a number
         * @param aNumber Number to format
         * @param pattern Format pattern
         */
        static formatNumber(num: number, pattern: string): string {
            return num.toString();
        }

        /**
         * Return all action modules.
         */
        static getAllModules(): Module[] {
            return modules.getAllModules();
        }

        /**
         * Get the context object for the current workflow run.
         */
        static getContext(): ExecutionContext {
            return context || (context = new ExecutionContext());
        }

        /**
         * Get current server time.
         */
        static getCurrentTime(): number {
            return new Date().getTime();
        }

        /**
         * Construct a new date from a natural input and a reference date.
         * @param inputString Natural human input text
         * @param refDate (Optional) reference date, now if not set
         */
        static getDate(inputString: string, refDate: Date): Date {
            throw new NotSupportedError();
        }

        /**
         * Parses the given string to produce a date.
         * @param date String to parse
         * @param pattern (Optional) Format pattern. Default value is 'yyyy-MM-dd HH:mm:ss'.
         */
        static getDateFromFormat(date: string, pattern: string): string {
            throw new NotSupportedError();
        }

        /**
         * Return an action module with the given name.
         * @param name The module name
         */
        static getModule(name: string): Module {
            return modules.getModule(name);
        }

        /**
         * Return the class name of any scripting object that typeof(obj) returns "object".
         * @param obj Object to get the class name
         */
        static getObjectClassName(obj: any): string {
            throw new NotSupportedError();
        }

        /**
         * Return the VS-O 'id' for the given object, some object have no ids
         * @param obj Object to get the id
         */
        static getObjectId(obj: any): string {
            throw new NotSupportedError();
        }

        /**
         * Return the plugin name of any scripting object. Base objects return "Server" as plugin
         * @param obj Object to get the plugin name
         */
        static getObjectPluginName(obj: any): string {
            throw new NotSupportedError();
        }

        /**
         * Return the VS-O 'type' for the given object
         * @param obj Object to get the type
         */
        static getObjectType(obj: any): string {
            throw new NotSupportedError();
        }

        /**
         * Return server running os
         */
        static getOsName(): string {
            throw new NotSupportedError();
        }

        /**
         * Return the default temporary directory on the server
         */
        static getTempDirectory(): string {
            throw new NotSupportedError();
        }

        /**
         * Convert a hexadecimal string value to a number.
         * @param value Hexadecimal value
         */
        static hexToDecimal(value: string): number {
            throw new NotSupportedError();
        }

        /**
         * Test whether that address is reachable. Best effort is made by the implementation to try to reach the host, but firewalls and server configuration may block requests resulting in a unreachable status while some specific ports may be accessible. A typical implementation will use ICMP ECHO REQUESTs if the privilege can be obtained, otherwise it will try to establish a TCP connection on port 7 (Echo) of the destination host.
         * @param hostOrIp Host or IP address
         * @param timeout Timeout in milliseconds
         */
        static isHostReachable(hostOrIp: string, timeout: number): boolean {
            throw new NotSupportedError();
        }

        /**
         * Return true is object is a "NotFound" object
         * @param obj Object to check
         */
        static isNotFound(obj: any): boolean {
            throw new NotSupportedError();
        }

        /**
         * Log a text in the system with a INFO threshold.
         * @param text text to log
         */
        static log(text: string): void {
            logger.info(text);
        }

        /**
         * Generate a unique ID.
         */
        static nextUUID(): string {
            const id = crypto.randomBytes(16).toString("hex");
            return `${id.substring(0, 8)}-${id.substring(8, 12)}-${id.substring(12, 16)}-${id.substring(16, 20)}-${id.substring(20)}`;
        }

        /**
         * Returns the IP address given a host name, using a DNS lookup. DNS must be configured on the VMO server. Failure to lookup the host will throw an exception.
         * @param hostName Host name
         */
        static resolveHostName(hostName: string): string {
            throw new NotSupportedError();
        }

        /**
         * Returns the host name given an IP Address (in standard dotted notation, for example "127.0.0.1"), using a DNS lookup. DNS must be configured on the VMO server. Failure to lookup the hostname, or badly formatted IP addresses will throw an exception
         * @param ipAddress The Ip address to lookup the hostname for
         */
        static resolveIpAddress(ipAddress: string): string {
            throw new NotSupportedError();
        }

        /**
         * Send a custom event to all Policies and Workflows.
         * @param eventKey Custom event name
         */
        static sendCustomEvent(eventKey: string): void {
            throw new NotSupportedError();
        }

        /**
         * Used to populate additional log info to every scripting log.
         * @param logMarker Log info that will appear in every scripting log
         */
        static setLogMarker(logMarker: string): void {
            throw new NotSupportedError();
        }

        /**
         * Sleep for a given time.
         * @param ms Time to sleep in miliseconds
         */
        static sleep(ms: number): boolean {
            return this.waitUntil(new Date(Date.now() + ms), 0);
        }

        /**
         * Log a text in the standard output console. Use this for debug only.
         * @param text text to log
         */
        static stdout(text: string): void {
            logger.stdout(text);
        }

        /**
         * Wait for an external custom event (with an expiration date).
         * @param text "internal" or "external" string. if Internal, this custom event cannot be triggered by an http post
         * @param text Custom event key
         * @param endDate Expiration date
         */
        static waitCustomEventUntil(text: string, endDate: Date): any /* EventCustom */ {
            throw new NotSupportedError();
        }

        /**
         * Pause the current script context execution and wait for a given date to continue.
         * @param waitDate Date to wait
         * @param milli Checking delay in milliseconds
         */
        static waitUntil(waitDate: Date, milli: number): boolean {
            return true;
        }

        /**
         * Log a text in the system with a WARNING threshold.
         * @param text text to log
         */
        static warn(text: string): void {
            logger.warn(text);
        }
    }

    global.System = System as any;
}

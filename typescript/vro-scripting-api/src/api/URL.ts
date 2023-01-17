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
    /**
     * vRO URL intrinsic class representation
     */
    export class URL {

        host: string;

        port: string;

        url: string;

        datas: string;

        requestType: string;

        contentType: string;

        result: string;

        /**
         * Adds a parameter at the end of the URL.
         * @param key
         * @param value
         */
        addParameter(key: any, value: any): void {
            throw new NotSupportedError();
        }

        /**
         * Adds a path to the URL.
         * @param value
         */
        addPath(value: any): void {
            throw new NotSupportedError();
        }

        /**
         * Returns the content of the URL.
         */
        getContent(): string {
            throw new NotSupportedError();
        }

        /**
         * Posts the content to the URL.
         * @param content
         */
        postContent(content: any): string {
            throw new NotSupportedError();
        }

        /**
         * Posts the content defined in the datas property to the URL.
         */
        post(): string {
            throw new NotSupportedError();
        }

        /**
         * Return the regex pattern string that matches any valid host name or IP address.
         */
        getHostnameOrIPPatternStr(): string {
            throw new NotSupportedError();
        }

        /**
         * Return the regex pattern string that matches any valid host name.
         */
        tnamePatternStr(): string {
            throw new NotSupportedError();
        }

        getIPAddressPatternStr(): string {
            throw new NotSupportedError();
        }

        /**
         * Return the regex pattern string that matches any valid IPv4 address.
         */
        getIPv4AddressPatternStr(): string {
            throw new NotSupportedError();
        }

        /**
         * Return the regex pattern string that matches any valid IPv6 address.
         */
        getIPv6AddressPatternStr(): string {
            throw new NotSupportedError();
        }

        /**
         * Checks if the parameter is valid host name. Example use: <code><pre> new URL().isValidHostname(hostname) </pre></code>
         * @param hostname
         */
        isValidHostname(hostname: string): boolean {
            throw new NotSupportedError();
        }

        /**
         * Checks if the parameter is valid host name or IP address (both IPv4 and IPv6). Example use: <code><pre> new URL().isHostnameOrIPAddress(hostOrIP) </pre></code>
         * @param hostOrIP
         */
        isValidHostnameOrIPAddress(hostOrIP: string): boolean {
            throw new NotSupportedError();
        }

        /**
         * Checks if the parameter is valid IP address (both IPv4 and IPv6). Example use: <code><pre> new URL().isValidIPAddress(address) </pre></code>
         * @param ipAddress
         */
        isValidIPAddress(ipAddress: string): boolean {
            throw new NotSupportedError();
        }

        /**
         * Checks if the parameter is valid IPv4 address. Example use: <code><pre> new URL().isValidIPv4Address(address) </pre></code>
         * @param ipAddress
         */
        isValidIPv4Address(ipAddress: string): boolean {
            throw new NotSupportedError();
        }

        /**
         * Checks if the parameter is valid IPv6 address. Example use: <code><pre> new URL().isValidIPv6Address(address) </pre></code>
         * @param ipAddress
         */
        isValidIPv6Address(ipAddress: string): boolean {
            throw new NotSupportedError();
        }

        /**
         * Escapes the host if this is needed. Usually you need to escape IPv6 numeric addresses using brackets. If this host does not need to be escaped then the original is returned.
         * @param ipAddress
         */
        escapeHost(ipAddress: string): string {
            throw new NotSupportedError();
        }

        /**
         * Un-escapes the host if this is IPv6 address in brackets. Usually you need to escape IPv6 numeric addresses using brackets but some times you need the plain IPv6 address. If this is not escaped IPv6 address the original is returned.
         * @param ipAddress
         */
        unescapeHost(ipAddress: string): string {
            throw new NotSupportedError();
        }

    }

    global.URL = URL as any;
}

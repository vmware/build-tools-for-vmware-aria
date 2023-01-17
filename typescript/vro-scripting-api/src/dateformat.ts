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
    const token = /y{1,4}|Y{1,4}|m{1,2}|M{1,2}|d{1,2}|h{1,2}|H{1,2}|k{1,2}|K{1,2}|s{1,2}|S{1,2}/g;

    export function dateFormat(date: Date, pattern: string): string {
        const handlers = {
            "y": getYear,
            "Y": getYear,
            "m": getMinuteInHour,
            "M": getMonthInYear,
            "d": getDayInMonth,
            "h": getHourIn_AM_PM,
            "H": getHourInDay,
            "k": getHourInDay,
            "K": getHourIn_AM_PM,
            "s": getSecondInMinute,
            "S": getMilisecondInSecond,
        }
        return pattern.replace(token, match => {
            const handler = handlers[match[0]];
            if (handler) {
                return handler(match);
            }
            return match;
        });
        function getYear(match: string): string {
            let value = date.getFullYear().toString();
            if (match.length === 2) {
                value = value.substring(2);
            }
            return value;
        }
        function getMonthInYear(match: string): string {
            return (date.getMonth() + 1).toString().padStart(match.length, "0");
        }
        function getDayInMonth(match: string): string {
            return date.getDate().toString().padStart(match.length, "0");
        }
        function getHourInDay(match: string): string {
            return date.getHours().toString().padStart(match.length, "0");
        }
        function getHourIn_AM_PM(match: string): string {
            let hours = date.getHours();
            if (hours > 12) {
                hours -= 11;
            }
            return hours.toString().padStart(match.length, "0");
        }

        function getMinuteInHour(match: string): string {
            return date.getHours().toString().padStart(match.length, "0");
        }
        function getSecondInMinute(match: string): string {
            return date.getSeconds().toString().padStart(match.length, "0");
        }
        function getMilisecondInSecond(match: string): string {
            return date.getMilliseconds().toString().padStart(match.length, "0");
        }
    }
}

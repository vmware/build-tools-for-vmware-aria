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
import * as iconvlite from "iconv-lite";
import * as characterEncoding from "charset-detector";
import * as winston from "winston";
import { WINSTON_CONFIGURATION } from "./constants";

export const decode = (buf: Buffer): string => {
	const charEncondings = characterEncoding(buf);
	if (charEncondings.length == 0) {
		winston.loggers.get(WINSTON_CONFIGURATION.logPrefix).info("Encoding not found. Using UTF-16");
		let str = "";
		for (let i = 0; i < buf.length; i++) {
			str += String.fromCharCode(buf[i]);
		}
		
		return str;
	}
	let encoding = charEncondings[0].charsetName;

	return iconvlite.decode(buf, encoding);
}

import * as iconvlite from "iconv-lite";
import * as characterEncoding from 'charset-detector';
import * as winston from 'winston';


export const decode = (buf: Buffer): string => {
	const charEncondings = characterEncoding(buf);
	if (charEncondings.length == 0) {
		winston.loggers.get("vrbt").info("Encoding not found. Using UTF-16");
		let str = "";
		for (let i = 0; i < buf.length; i++) {
			str += String.fromCharCode(buf[i]);
		}
		
		return str;
	}

	let encoding = charEncondings[0].charsetName;
	return iconvlite.decode(buf, encoding);
}

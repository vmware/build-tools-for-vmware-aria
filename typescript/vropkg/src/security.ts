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
import * as fs from "fs-extra";
import * as t from "./types";
import * as crypto from "crypto";
import * as os from "os";
import * as nforge from "node-forge"
import * as winston from "winston";


/**
 * loadCertificate the private key and corresponding certificate chain (from files) and return a
 * t.Certificate object containing the given private key and certificate chain together
 * with a subject to certificate mapping for easy search.
 *
 * Note 1: if privateKey and/or chain is a valid path to existing and readable file, then
 * the contain of that file will be assumed to contain the actual key/certificate chain.
 * Otherwise privateKey and/or chain will be assumed to contain the actual raw key and/or
 * certificate chain (in PEM format).
 *
 * Note 2: No actual check will be done whether the certificate chain actually corresponds
 * to the given private key or anything like that.
 * Private key if encrypted with a password will not be decrypted and will remain in
 * encrypted form.
 *
 * @param {chain} A path to a file containing certificate chain of concatenated certificates
 * in PEM format or a raw string containing the concatenated PEM certificates.
 * @param {privateKey} A path to a file containing the private key in PEM format or the raw PEM
 * content of the key otherwise. Private key may be encrypted using a password
 * in which case it would not be decrypted.
 * @param {privateKeyPassword} a {privateKey} passphrase
 * @return {t.Certificate} object representing the private key,
 * certificate chain together with a Subject to Certificate map for easier search.
 */
const loadCertificate = (chain: t.PEM, privateKey: t.PEM, privateKeyPassword: string): t.Certificate => {
	let privateKeyPEM = getContentIfFile(privateKey);
	let chainPEMs = extractPEMs(getContentIfFile(chain));

	if (!privateKeyPEM) {
		throw "No certificate chain provided."
	}
	if (chainPEMs.length == 0) {
		throw "No certificate chain provided."
	}

	let publicKeyPEM = chainPEMs[chainPEMs.length - 1]

	let subjectPEMChainMap: Map<t.SUBJECT, t.PEM> = new Map();
	chainPEMs.forEach(pem => {
		subjectPEMChainMap.set(getSubject(pem), pem);
	})

	return <t.Certificate>{
		subject: getSubject(publicKeyPEM),
		privateKey: privateKeyPEM,
		privateKeyPassword: privateKeyPassword,
		publicKey: publicKeyPEM,
		chain: subjectPEMChainMap,
	}
}

/**
 * @brief	Determines the OS line end and sets it for future use
 */
const determineEOL = function ( chunk ) {
	const data	= chunk.toString();
	const match	= data.match( new RegExp( `(\r\n|\r|\n)` ) );
	let EOL		= os.EOL;

	if ( match !== null )
		EOL	= match[1];

	return EOL;
}

/**
 * Gets a certificate chain of concatenated certificates in PEM format, and returns an array of
 * certificates again in PEM format that are extracted from the original concatenated content.
 * @param pemOfPEMs One or more certificates in PEM format concatenated together.
 * @return An array of certificates in PEM format that correspond to the same certificates that have been
 *         available in the original concatenated input.
 */
const extractPEMs = (pemOfPEMs: t.PEM): Array<t.PEM> => {
	let BOUNDARY_BEGIN = "-----BEGIN CERTIFICATE-----"
	let BOUNDARY_END = "-----END CERTIFICATE-----"
	let NEW_LINE = determineEOL( pemOfPEMs );
	let OS_EOL = os.EOL;

	let store = false
	let body: Array<string> = [];

	let pems: Array<t.PEM> = []

	let lines = pemOfPEMs.toString().split(NEW_LINE)
	for (let i = 0; i < lines.length; i++) {
		switch (lines[i]) {
			case BOUNDARY_BEGIN:
				store = true;
				body.push(BOUNDARY_BEGIN)
				break;
			case BOUNDARY_END:
				store = false;
				body.push(BOUNDARY_END);
				pems.push(<t.PEM>body.join(OS_EOL));
				body = [];
				break;
			default:
				if (store) {
					body.push(lines[i]);
				}
		}
	}
	return pems;
}

/**
 * Checks if the input string is a a represenation of a path to an existing file and if so,
 * then returns the file content, otherwise it would return the input unchanged.
 *
 * @param pem A path ot an existing and readable file that contains a cert/key or a valid cert/key content otherwise.
 * @return {t.PEM}
 */
const getContentIfFile = (pem: t.PEM | string): t.PEM => {
	if (pem == undefined || pem == null) {
		return pem;
	}
	if (fs.existsSync(pem) && fs.lstatSync(pem).isFile()) {
		winston.loggers.get("vrbt").info(`Using certificate file ${pem}`);
		return <t.PEM>fs.readFileSync(pem, 'UTF-8');
	}else{
		winston.loggers.get("vrbt").info(`Using certificate PEM from console input`);
		return pem;
	}
}

/**
 * Extract the subject form a certificate in PEM format.
 * This does the same as
 * `openssl x509 -in cert.pem -text | grep Subject:`
 *
 * @param certificate A certificate in PEM format.
 * @return The subject of the certificate.
 *    Example: "C=BG, ST=Bulgaria, L=Sofia, O=VMware, OU=PSCoE, CN=yordan/emailAddress=ipetrov@vmware.com"
 */
const getSubject = (certificate: t.PEM): string => {
	return nforge.pki.certificateFromPem(certificate)
		.subject.attributes
		.map(attr => [attr.shortName, attr.value].join('='))
		.join(',')
}


/**
 * Sign the {data} with a certificate private key and store it at an optional location {exportFilePath}
 *
 * @param data
 * @param certificate
 * @param exportFilePath
 */
const sign = (data: string | Buffer, certificate: t.Certificate): Buffer => {
	let signer = crypto.createSign('MD5');
	signer.update(data);
	signer.end();
	return signer.sign(crypto.createPrivateKey({
		key: certificate.privateKey,
		format: "pem",
		passphrase: certificate.privateKeyPassword
	}));
}


/**
 * Get a PEM encoded certificate or key, strip the header and trailer delimiters and then Base 64 decode the content to obtain
 * the raw DER encoded content of the key or certificate and rerurn it as a Buffer object.
 *
 * @param pem A string containing the PEM encoded certificate or key.
 * @return The raw DER encoded certificate or key.
 */
const pemToDer = (pem: t.PEM): Buffer => {
	let b64 = pem.toString()
		.replace(/[\n\r]/g, '')
		.replace(/.*-----BEGIN CERTIFICATE-----/, '')
		.replace(/-----END CERTIFICATE-----/, '')
		.replace(/.*-----BEGIN ENCRYPTED PRIVATE KEY-----/, '')
		.replace(/-----END ENCRYPTED PRIVATE KEY-----/, '');
	return Buffer.from(b64, 'base64');
}

export {loadCertificate, sign, pemToDer}

/**
 * A scripting object representing a X.509 certificate.
 */
declare class CryptoCertificate {
	// Serial Number of the Certificate
	readonly serialNumber: string;
	// Encoded form of the certificate encoded as a Base64 string. Hashing this can create a fingerprint
	readonly encodedBase64: string;
	// The RSA Public Key in PEM format found in the certificate
	readonly publicKeyPem: string;
	// issuedToDN parsed into key/value pairs
	readonly issuedToMap: Properties;
	// Distinguished Name the certificate was issued by
	readonly issuedByDN: string;
	// Distinguished Name the certificate was issued to
	readonly issuedToDN: string;
	// PEM Encoding of the certificate
	readonly pemEncoded: string;
	// issuedByDN parsed into key/value pairs
	readonly issuedByMap: Properties;
	// Signature algorithm used by the certificate signer
	readonly signatureAlgorithm: string;
	// SHA1 fingerprint of the certificate
	readonly sha1Fingerprint: string;
	// Base64 encoded signature of the certificate
	readonly signatureBase64: string;
	// SHA256 fingerprint of the certificate
	readonly sha256Fingerprint: string;
	// A list of subject alternative names found in the certificate. Each will have a colon delimited prefix for the type of SAN found. ex: "dns:"
	readonly subjectAlternativeNames: string[];

	constructor(certString: string);

	/**
	 * Verifies the Certificate was signed by a signing certificates private key.
	 * 
	 * @param pemKey 
	 */
	verify(pemKey: string): boolean;
	/**
	 * The certificate is valid before this date.
	 */
	getValidbefore(): Date;
	/**
	 * The certificate is valid after this date.
	 */
	getValidAfter(): Date;
	/**
	 * Is the certificate valid based on a provided date.
	 * 
	 * @param date 
	 */
	isValidOn(date: Date): boolean;
}

/**
 * Provides methods to parse or fetch certificates.
 */
declare class CryptoCertificateManager {
	/**
	 * Parses a PEM encoded X.509 Certificate.
	 * 
	 * @param pemCertString 
	 */
	public static parseCertificatePem(pemCertString: string): CryptoCertificate;
	/**
	 * Returns array of certificates presented by an https server.
	 * 
	 * @param urlString 
	 */
	public static getHttpsCertificate(urlString: string): CryptoCertificate[];
}

/**
 * Provides methods to hash data with different digests.
 */
declare class CryptoDigest {
	/**
	 * Returns a Base64 encoded 160 bit SHA-1 hash.
	 * 
	 * @param dataB64 
	 */
	public static sha1Base64(dataB64: string): string;
	/**
	 * Returns a Base64 encoded 160 bit SHA-1 hash.
	 * 
	 * @param data 
	 */
	public static sha1(data: string): string;
	/**
	 * Returns a Base64 encoded 128 bit MD5 hash.
	 * 
	 * @param data 
	 */
	public static md5(data: string): string;
	/**
	 * Returns a Base64 encoded 128 bit MD5 hash.
	 * 
	 * @param dataB64 
	 */
	public static md5Base64(dataB64: string): string;
	/**
	 * Returns a Base64 encoded 384 bit SHA384 hash.
	 * 
	 * @param dataB64 
	 */
	public static sha384Base64(dataB64: string): string;
	/**
	 * Returns HmacSHA512 MAC for the given key and data Base64 encoded.
	 * 
	 * @param keyB64 
	 * @param dataB64 
	 */
	public static hmacSha512(keyB64: string, dataB64: string): string;
	/**
	 * Returns HmacMD5 MAC for the given key and data Base64 encoded.
	 * 
	 * @param keyB64 
	 * @param dataB64 
	 */
	public static hmacMd5(keyB64: string, dataB64: string): string;
	/**
	 * Returns HmacSHA384 MAC for the given key and data Base64 encoded.
	 * 
	 * @param keyB64 
	 * @param dataB64 
	 */
	public static hmacSha384(keyB64: string, dataB64: string): string;
	/**
	 * Returns a Base64 encoded 256 bit SHA256 hash.
	 * 
	 * @param data 
	 */
	public static sha256(data: string): string;
	/**
	 * Returns HmacSHA256 MAC for the given key and data Base64 encoded.
	 * 
	 * @param keyB64 
	 * @param dataB64 
	 */
	public static hmacSha256(keyB64: string, dataB64: string): string;
	/**
	 * Returns HmacSHA1 MAC for the given key and data Base64 encoded.
	 * 
	 * @param keyB64 
	 * @param dataB64 
	 */
	public static hmacSha1(keyB64: string, dataB64: string): string;
	/**
	 * Returns a Base64 encoded 384 bit SHA384 hash.
	 * 
	 * @param data 
	 */
	public static sha384(data: string): string;
	/**
	 * Returns a Base64 encoded 256 bit SHA256 hash.
	 * 
	 * @param dataB64 
	 */
	public static sha256Base64(dataB64: string): string;
	/**
	 * Returns a Base64 encoded 512 bit SHA512 hash.
	 * 
	 * @param data 
	 */
	public static sha512(data: string): string;
	/**
	 * Returns a Base64 encoded 512 bit SHA512 hash.
	 * 
	 * @param dataB64 
	 */
	public static sha512Base64(dataB64: string): string;
}

/**
 * Provides methods to encode/decode strings between different encodings.
 */
declare class CryptoEncoding {
	/**
	 * Decodes two Base64 strings and concatenates the binary data. Returns base64 encoded result.
	 * 
	 * @param b64data1 
	 * @param b64data2 
	 */
	public static binaryConcatBase64(b64data1: string, b64data2: string): string;
	/**
	 * Base64 Encoder
	 * 
	 * @param data 
	 */
	public static base64EncodeBytes(data: byte[]): string;
	/**
	 * Base64 Encoder.
	 * 
	 * @param data 
	 */
	public static base64Encode(data: string): string;
	/**
	 * Hex to Base64 Encoder.
	 * 
	 * @param hex 
	 */
	public static hexToBase64(hex: string): string;
	/**
	 * Extracts data from MimeAttachment as Base64.
	 * 
	 * @param mime 
	 */
	public static mimeToBase64(mime: MimeAttachment): string;
	/**
	 * Base64 to MimeAttachment.
	 * 
	 * @param b64data 
	 * @param mimeType 
	 * @param fileName 
	 */
	public static base64ToMime(b64data: string, mimeType: string, fileName: string): MimeAttachment;
	/**
	 * Base64 Decoder.
	 * 
	 * @param b64data 
	 */
	public static base64Decode(b64data: string): string;
	/**
	 * Returns a subset of bytes from a Base64 encoded string.
	 * 
	 * @param b64data 
	 * @param start 
	 * @param end 
	 */
	public static getSubsetBase64(b64data: string, start: number, end: number): string;
	/**
	 * Base64 to Hex Encoder.
	 * 
	 * @param b64data 
	 */
	public static base64toHex(b64data: string): string;
	/**
	 * Decodes a Base64 String and returns the number of bytes that were encoded.
	 * 
	 * @param b64data 
	 */
	public static getLengthBase64(b64data: string): number;
}

/**
 * Provides static methods to encrypt/decrypt data with different ciphers. All ciphers use CBC mode with PKCS5 padding.
 */
declare class CryptoEncryption {
	/**
	 * Returns a number of random bytes encoded as a Base64 string.
	 * 
	 * @param numberOfBytes 
	 */
	public static generateRandomBytes(numberOfBytes: number): string;
	/**
	 * 3DES Encryption. Returns encrypted data Base64 encoded.
	 * 
	 * @param dataB64 
	 * @param secretB64 
	 * @param ivB64 
	 */
	public static tripleDesEncrypt(dataB64: string, secretB64: string, ivB64: string): string;
	/**
	 * AES Encryption. Returns encrypted data Base64 encoded.
	 * 
	 * @param dataB64 
	 * @param secretB64 
	 * @param ivB64 
	 */
	public static aesEncrypt(dataB64: string, secretB64: string, ivB64: string): string;
	/**
	 * AES Decryption. Returns data Base64 encoded.
	 * 
	 * @param encryptedB64 
	 * @param secretB64 
	 * @param ivB64 
	 */
	public static aesDecrypt(encryptedB64: string, secretB64: string, ivB64: string): string;
	/**
	 * 3DES Decryption. Returns data Base64 encoded.
	 * 
	 * @param encryptedB64 
	 * @param secretB64 
	 * @param ivB64 
	 */
	public static tripleDesDecrypt(encryptedB64: string, secretB64: string, ivB64: string): string;
	/**
	 * Returns 16 random bytes encoded as a Base64 string suitable for an AES Initialization Vector.
	 */
	public static generateRandomIv(): string;
}
/**
 * Provides static methods to encrypt / decrypt / sign data with RSA style encryption.
 */
declare class CryptoRSA {
	/**
	 * Asymmetric RSA Encryption. Result is Base64 encoded.
	 * 
	 * @param key 
	 * @param dateB64 
	 */
	public static encrypt(key: string, dateB64: string): string;
	/**
	 * Asymmetric RSA Decryption. Result is Base64 encoded.
	 * 
	 * @param key 
	 * @param encryptedB64 
	 */
	public static decrypt(key: string, encryptedB64: string): string;
	/**
	 * Creates a RSA Signature.
	 * 
	 * @param key 
	 * @param dateB64 
	 */
	public static createSignature(key: string, dateB64: string): string;
	/**
	 * Verifies a RSA Signature.
	 * 
	 * @param key 
	 * @param dateB64 
	 * @param signatureB64 
	 */
	public static verifySignature(key: string, dateB64: string, signatureB64: string): boolean;
}

package org.openxdm.xcap.server.etag;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ETagGenerator {

	public static String HASH_ALGORITHM = "MD5";
	
	//private static final char[] CHARS = {48,49,50,51,52,53,54,55,56,57,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122};
	
	private static final char[] HEXCHARS = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	
	private static String toHexString(byte[] bytes) {
		// convert each byte to hex chars 
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<bytes.length;i++) {
			sb.append(HEXCHARS[(bytes[i] >> 4) & 0x0f]).append(HEXCHARS[bytes[i] & 0x0f]);      
		}
		return sb.toString();
	}
	
	/**
	 * To generate a safe etag the document selector
	 * and current time can be used as input for a digest.
	 * This method creates such etag by converting each
	 * digest byte to hex chars.
	 * @param documentSelector the document selector part of
	 * a xcap uri 
	 * @return a String with the document etag.
	 */
	public static String generate(String documentSelector) {		
		// check argument
		if (documentSelector == null) {
			return null;
		}				
		// get current time
		String date = Long.toString(System.currentTimeMillis());		
		// build digest		
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);			
			md.update(documentSelector.getBytes());
			md.update(date.getBytes());			
			byte[] digest = md.digest();
			// convert bytes to hex string
			return toHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}		
	}
}

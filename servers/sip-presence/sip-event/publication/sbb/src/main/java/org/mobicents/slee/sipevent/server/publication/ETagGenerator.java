package org.mobicents.slee.sipevent.server.publication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ETagGenerator {

	public static String HASH_ALGORITHM = "MD5";
	
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
	 * To generate a safe publication etag the presentity, event package 
	 * and current time, can be used as input for a digest.
	 * This method creates such etag by converting each
	 * digest byte to hex chars.
	 * @return a String with the publication etag.
	 */
	public static String generate(String presentity,String eventPackage) {		
		// check args
		if (presentity == null || eventPackage == null) {
			return null;
		}				
		// get current time
		String date = Long.toString(System.currentTimeMillis());		
		// build digest		
		try {
			MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);			
			md.update(presentity.getBytes());
			md.update(eventPackage.getBytes());
			md.update(date.getBytes());			
			byte[] digest = md.digest();
			// convert bytes to hex string
			return toHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}		
	}
}

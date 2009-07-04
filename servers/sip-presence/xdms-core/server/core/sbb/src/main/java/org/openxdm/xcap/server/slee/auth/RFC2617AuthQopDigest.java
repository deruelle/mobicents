package org.openxdm.xcap.server.slee.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.httpclient.util.EncodingUtil;
import org.openxdm.xcap.common.error.InternalServerErrorException;

/**
 * 
 * @author martins
 *
 */
public class RFC2617AuthQopDigest {
	
	/**
	 * 
	 */
	private final String username;
	
	/**
	 * 
	 */
	private final String realm;
	
	/**
	 * 
	 */
	private final String password;
	
	/**
	 * 
	 */
	private final String nonce;
	
	/**
	 * 
	 */
	private final String nonceCount;
	
	/**
	 * 
	 */
	private final String cnonce;
	
	/**
	 * 
	 */
	private final String qop;
	
	/**
	 * 
	 */
	private final String method;
	
	/**
	 * 
	 */
	private final String digestUri;
	
	/**
	 * 
	 * @param username
	 * @param realm
	 * @param password
	 * @param nonce
	 * @param nonceCount
	 * @param cnonce
	 * @param qop
	 * @param method
	 * @param digestUri
	 */
	public RFC2617AuthQopDigest(String username, String realm,
			String password, String nonce, String nonceCount, String cnonce,
			String qop, String method, String digestUri) {
		this.username = username;
		this.realm = realm;
		this.password = password;
		this.nonce = nonce;
		this.nonceCount = nonceCount;
		this.cnonce = cnonce;
		this.qop = qop;
		this.method = method;
		this.digestUri = digestUri;
	}

	/**
	 * Calculates the encoded http digest according to RFC 2617 with "auth" qop.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String digest() throws InternalServerErrorException {
		
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("failed to get instance of MD5 digest, used in "+this.getClass());
		}
		
		final String a1 = username + ":" + realm + ":" + password;
		final String ha1 = encode(messageDigest.digest(EncodingUtil.getAsciiBytes(a1)));
		
		
		final String a2 = method + ":" + digestUri;
		final String ha2 = encode(messageDigest.digest(EncodingUtil.getAsciiBytes(a2)));
		
		final String kd = ha1 + ":" + nonce + ":" + nonceCount + ":" + cnonce + ":" + qop + ":" + ha2;
		
		return encode(messageDigest.digest(EncodingUtil.getAsciiBytes(kd)));
	}
	
	/*
	 * 
	 */
	private String encode(byte[] bytes) {
		
		if (bytes.length != 16) {
            return null;
        } 

        char[] buffer = new char[32];
        for (int i = 0; i < 16; i++) {
            int low = (int) (bytes[i] & 0x0f);
            int high = (int) ((bytes[i] & 0xf0) >> 4);
            buffer[i * 2] = HEXADECIMAL[high];
            buffer[(i * 2) + 1] = HEXADECIMAL[low];
        }

        return new String(buffer);
	}
	
	/*
	 * 
	 */
	private static final char[] HEXADECIMAL = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 
        'e', 'f'
    };
	
}

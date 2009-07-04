package org.openxdm.xcap.server.slee.auth;

public class AsciiHexStringEncoder {

	/*
	 * 
	 */
	public static String encode(byte[] bytes) {
		
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

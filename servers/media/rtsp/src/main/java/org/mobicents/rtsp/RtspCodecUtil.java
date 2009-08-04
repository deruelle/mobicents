package org.mobicents.rtsp;

/**
 * 
 * @author amit.bhayani
 *
 */
public class RtspCodecUtil {
	// space ' '
	static final byte SP = 32;

	// tab ' '
	static final byte HT = 9;

	/**
	 * Carriage return
	 */
	static final byte CR = 13;

	/**
	 * Equals '='
	 */
	static final byte EQUALS = 61;

	/**
	 * Line feed character
	 */
	static final byte LF = 10;

	/**
	 * carriage return line feed
	 */
	static final byte[] CRLF = new byte[] { CR, LF };

	/**
	 * Colon ':'
	 */
	static final byte COLON = 58;

	/**
	 * Semicolon ';'
	 */
	static final byte SEMICOLON = 59;

	/**
	 * comma ','
	 */
	static final byte COMMA = 44;

	static final byte DOUBLE_QUOTE = '"';

	static final String DEFAULT_CHARSET = "UTF-8";

	private RtspCodecUtil() {
		super();
	}
}

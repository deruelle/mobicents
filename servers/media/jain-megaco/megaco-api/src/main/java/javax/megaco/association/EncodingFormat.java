package javax.megaco.association;

import java.io.Serializable;

public class EncodingFormat implements Serializable {

	/**
	 * Identifies the encoding format to the peer messages from the stack shall
	 * be text (ABNF format).
	 */
	public static final int M_TEXT = 0;
	/**
	 * Identifies the encoding format to the peer messages from the stack shall
	 * be binary (ASN.1 with BER format).
	 */
	public static final int M_ASN = 1;

	/**
	 * Identifies a EncodingFormat object that constructs the class with the
	 * constant M_TEXT. Since it is reference to static final object, it
	 * prevents further instantiation of the same object in the system.
	 */
	public static final EncodingFormat TEXT = new EncodingFormat(M_TEXT);
	/**
	 * Identifies a EncodingFormat object that constructs the class with the
	 * constant M_ASN. Since it is reference to static final object, it prevents
	 * further instantiation of the same object in the system.
	 */
	public static final EncodingFormat ASN = new EncodingFormat(M_ASN);

	private int encodingFormat = -1;

	private EncodingFormat(int encoding_format) {
		encodingFormat = encoding_format;
	}

	private Object readResolve() {
		return this.getObject(this.encodingFormat);
	}

	/**
	 * Returns reference of the EncodingFormat object that identifies the
	 * encoding format as value passed to this method.
	 * 
	 * @param value
	 *            - It is one of the possible values of the static constant that
	 *            this class provides.
	 * @return Returns reference of the EncodingFormat object.
	 * @throws IllegalArgumentException
	 *             - If the value passed to this method is invalid, then this
	 *             exception is raised.
	 */
	public static Object getObject(int value) throws IllegalArgumentException {
		switch (value) {

		case M_TEXT:
			return TEXT;
		case M_ASN:
			return ASN;
		default:
			throw new IllegalArgumentException("Wrogn value passed, there is no encoding with code: " + value);
		}
	}

	/**
	 * This method returns one of the static field constants defined in this
	 * class.
	 * 
	 * @return Returns an integer value that identifies the encoding format of
	 *         the association, which could to be one of ASN or TEXT.
	 */
	public int getEncodingFormat() {
		return this.encodingFormat;
	}

	@Override
	public String toString() {
		switch (this.encodingFormat) {

		case M_TEXT:
			return "EncodingFormat[TEXT]";
		case M_ASN:
			return "EncodingFormat[ASN]";
		default:
			return "EncodingFormat[" + this.encodingFormat + "]";
		}
	}

}

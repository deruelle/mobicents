package javax.megaco.message.descriptor;

import java.io.Serializable;

/**
 * 
 * The DigitStringPosition object is a class that shall be used to set the Digit
 * Position in a digit map string in the Digit Map descriptor. This is an
 * independent class derived from java.util.Object and shall not have any
 * derived classes. Each digit position object shall specify the list of digits
 * that can be valid at that digit position.
 */
public class DigitStringPosition implements Serializable {

	/**
	 * Identifies THE DIGIT 0. Its value shall be set to 0.
	 */
	public static final int DIGIT0 = 0;

	/**
	 * Identifies THE DIGIT 1. Its value shall be set to 1.
	 */
	public static final int DIGIT1 = 1;

	/**
	 * Identifies THE DIGIT 2. Its value shall be set to 2.
	 */
	public static final int DIGIT2 = 2;

	/**
	 * Identifies THE DIGIT 3. Its value shall be set to 3.
	 */
	public static final int DIGIT3 = 3;

	/**
	 * Identifies THE DIGIT 4. Its value shall be set to 4.
	 */
	public static final int DIGIT4 = 4;

	/**
	 * Identifies THE DIGIT 5. Its value shall be set to 5.
	 */
	public static final int DIGIT5 = 5;

	/**
	 * Identifies THE DIGIT 6. Its value shall be set to 6.
	 */
	public static final int DIGIT6 = 6;

	/**
	 * Identifies THE DIGIT 7. Its value shall be set to 7.
	 */
	public static final int DIGIT7 = 7;

	/**
	 * Identifies THE DIGIT 8. Its value shall be set to 8.
	 */
	public static final int DIGIT8 = 8;

	/**
	 * Identifies THE DIGIT 9. Its value shall be set to 9.
	 */
	public static final int DIGIT9 = 9;

	/**
	 * Identifies THE DIGIT A. Its value shall be set to 10.
	 */
	public static final int DIGITA = 10;

	/**
	 * Identifies THE DIGIT B. Its value shall be set to 11.
	 */
	public static final int DIGITB = 11;

	/**
	 * Identifies THE DIGIT C. Its value shall be set to 12.
	 */
	public static final int DIGITC = 12;

	/**
	 * Identifies THE DIGIT D. Its value shall be set to 13.
	 */
	public static final int DIGITD = 13;

	/**
	 * Identifies THE DIGIT STAR. Its value shall be set to 14.
	 */
	public static final int DIGIT_STAR = 14;

	/**
	 * Identifies THE DIGIT HASH. Its value shall be set to 15.
	 */
	public static final int DIGIT_HASH = 15;

	/**
	 * Identifies THE DIGIT G. Its value shall be set to 16.
	 */
	public static final int DIGITG = 16;

	/**
	 * Identifies THE DIGIT H. Its value shall be set to 17.
	 */
	public static final int DIGITH = 17;

	/**
	 * Identifies THE DIGIT I. Its value shall be set to 18.
	 */
	public static final int DIGITI = 18;

	/**
	 * Identifies THE DIGIT J. Its value shall be set to 19.
	 */
	public static final int DIGITJ = 19;

	/**
	 * Identifies THE DIGIT K. Its value shall be set to 20.
	 */
	public static final int DIGITK = 20;

	/**
	 * Identifies THE DIGIT DOT. Its value shall be set to 21.
	 */
	public static final int DIGIT_DOT = 21;

	/**
	 * Identifies THE DIGIT L. This refers to the inter-event long duration
	 * timer. Its value shall be set to 22.
	 */
	public static final int DIGITL = 22;

	/**
	 * Identifies THE DIGIT S. This refers to the inter-event short duration
	 * timer. Its value shall be set to 23.
	 */
	public static final int DIGITS = 23;

	/**
	 * Identifies THE DIGIT Z. This refers to the Long duration timer. Its value
	 * shall be set to 24.
	 */
	public static final int DIGITZ = 24;

	/**
	 * Identifies THE DIGIT X. Its value shall be set to 25.
	 */
	public static final int DIGITX = 25;

	private final static int _LOW_MARK = 0;
	private final static int _HIGH_MARK = 25;

	private int[] digits = null;

	/**
	 * Constructs a Digit Map Position Object. This shall contain information
	 * about the Digit Position in the digit string.
	 */
	public DigitStringPosition() {
		super();

	}

	/**
	 * The method is used to get the vector of the digits that can occur at the
	 * digit position.
	 * 
	 * @return The vector specifying the digits at the digit position.
	 
	 */
	public int[] getDigitStringPosition(){

		return this.digits;
	}

	/**
	 * The method sets the vector of integers specifying digits that are valid
	 * for the digit position.
	 * 
	 * @param digits
	 *            The vector of the integer for digits.
	 * @throws IllegalArgumentException
	 *             - if the vector of digits contains values other than the
	 *             static constants as defined for this class.
	 */
	public void setDigitStringPosition(int[] digits) throws IllegalArgumentException {
		if (digits == null) {
			// FIXME:?
			throw new IllegalArgumentException("Digits must not be null");
		}

		for (int i : digits) {
			if (i > _HIGH_MARK || i < _LOW_MARK) {
				throw new IllegalArgumentException("One of passed digits is out of defined scope: " + i);
			}
		}

		this.digits = digits;

	}

	public java.lang.String toString() {
		//FIXME: add proper dump
		return super.toString();
	}
}

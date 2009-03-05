package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the DigitMap
 * descriptor.
 */
public class DigitMapDescriptor extends Descriptor implements Serializable {

	private String digitMapName = null;
	private DigitMapValue digitMapValue = null;
	private String digitMapStr = null;

	/**
	 * Constructs a DigitMap Descriptor.
	 */
	public DigitMapDescriptor() {
		super();
		super.descriptorId = DescriptorType.M_DIGIT_MAP_DESC;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type descriptor DigitMap.
	 * This method overrides the corresponding method of the base class
	 * Descriptor.
	 * 
	 * @return Returns an integer value that identifies this DigitMap object as
	 *         the type of DigitMap descriptor. It returns the value
	 *         M_DIGIT_MAP_DESC for a DigitMap Descriptor.
	 */
	public final int getDescriptorId() {

		return super.descriptorId;
	}

	/**
	 * This method returns the digit map name if set for the digit map
	 * descriptor.
	 * 
	 * @return Returns a string value that identifies the digit map name for the
	 *         DigitMap descriptor. If the digit map name is not set, then this
	 *         shall return a null value.
	 */
	public final java.lang.String getDigitMapName() {
		return this.digitMapName;
	}

	/**
	 * This method sets the digit map name if set for the digit map descriptor.
	 * 
	 * @param digitName
	 *            - Sets a string value that identifies the digit map name for
	 *            the DigitMap descriptor. If the digit map name is not set,
	 *            then this shall return a null value.
	 * @throws IllegalArgumentException
	 *             - Thrown if the string value does not satisfy the grammar for
	 *             the digit map name.
	 */
	public final void setDigitMapName(java.lang.String digitName) throws IllegalArgumentException {

		// FIXME: add grammar check
		// if(getDigitMapValue() != null)
		// {
		// throw new
		// IllegalArgumentException("Digit map value must not be present when name is set.");
		// }
		this.digitMapName = digitName;

	}

	/**
	 * This method gets the DigitMap Value. The DigitMap value class contains
	 * information about the dial plan. This may also contains special symbols
	 * like wildcards and timer values to be used on application of the dial
	 * plan.
	 * 
	 * @returnReturns a DigitMapValue object.
	 */
	public final DigitMapValue getDigitMapValue() {
		return this.digitMapValue;
	}

	/**
	 * This method sets the DigitMap Value.
	 * 
	 * @param digitValue
	 *            - The DigitMap value object refernce.
	 * @throws IllegalArgumentException
	 *             - Thrown if the digit map value passed in the arguments
	 *             contains invalid parameters.
	 */
	public final void setDigitMapValue(DigitMapValue digitValue) throws IllegalArgumentException {

		// if(getDigitMapName() != null)
		// {
		// throw new
		// IllegalArgumentException("Digit map name must not be present when value is set.");
		// }
		// FIXME: add checks

		this.digitMapValue = digitValue;

	}

	/**
	 * This method gets the DigitMap Value in the string format. The DigitMap
	 * value class contains information about the dial plan. This may also
	 * contains special symbols like wildcards and timer values to be used on
	 * application of the dial plan.
	 * 
	 * @return Returns a DigitMapValue java.lang.String object.
	 */
	public final java.lang.String getDigitMapValueStr() {
		return this.digitMapStr;

	}

	/**
	 * This method sets the DigitMap Value in the string format. The
	 * applications may use this method in case it has pre-encoded dial plan and
	 * wants to use the same for subsequent calls. The digitValueStr string set
	 * in this method must be in the same format as defined by the MEGACO
	 * protocol.
	 * 
	 * @param digitValue
	 *            - The DigitMap value object refernce.
	 * @throws IllegalArgumentException
	 *             - Thrown if the digit map value passed in the arguments
	 *             contains invalid parameters.
	 */
	public final void setDigitMapValueStr(java.lang.String digitValueStr) throws IllegalArgumentException {

		if (getDigitMapName() != null) {
			throw new IllegalArgumentException("Digit map name must not be present when digit map string is set.");
		}
		// FIXME: add checks:
		// FIXME - shouldnt it be DigitMapString ?

		this.digitMapStr = digitValueStr;

	}

}

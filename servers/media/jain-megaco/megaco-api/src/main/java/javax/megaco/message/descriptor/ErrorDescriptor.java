package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.message.Descriptor;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the error
 * descriptor.
 */
public class ErrorDescriptor extends Descriptor implements Serializable {

	private int protErrorCode = -1;
	private String errorString = null;

	/**
	 * Constructs a Error Descriptor. This contains the protocol specified error
	 * code and optionally the error string qualifying the error code.
	 * 
	 * @param errorCode
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid error code is set.
	 */
	public ErrorDescriptor(int errorCode) throws IllegalArgumentException {
		// FIXME: section 13.2 ... does not clear this...
		this.protErrorCode = errorCode;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type Error descriptor. This
	 * method overrides the corresponding method of the base class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object as the type
	 *         of Error descriptor. It returns that it is Error Descriptor i.e.,
	 *         M_ERROR_DESC.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the error code spcified by the Megaco protocol.
	 * 
	 * @return Returns an integer value that identifies the error code.
	 */
	public final int getProtErrorCode() {
		return this.protErrorCode;
	}

	/**
	 * This method cannot be overridden by the derived class. This method sets
	 * the error string corresponding to the error code as specified by the
	 * Megaco protocol.
	 * 
	 * @param errorStr
	 *            Returns the string corresponding to the error code.
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid string is set.
	 */
	public final void setErrorString(java.lang.String errorStr) throws IllegalArgumentException {
		if (errorStr.length() > 80) {
			throw new IllegalArgumentException("Error String must nto be longer than 80 chars, see section 13.2 of RFC 3525");
		}
		this.errorString = errorStr;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the error string corresponding to the error code as specified by
	 * the Megaco protocol.
	 * 
	 * @return Returns the string corresponding to the error code. If the error
	 *         string is not set then this method would return NULL.
	 */
	public final java.lang.String getErrorString() {
		return this.errorString;
	}

}

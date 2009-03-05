package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.MethodInvocationException;
import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;
import javax.megaco.message.Termination;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the mux
 * descriptor.
 */
public class MuxDescriptor extends Descriptor implements Serializable {

	private MuxType muxType;
	private Termination[] termList;
	private String extMux;

	/**
	 * Constructs a Mux Descriptor with the mux type and the termination id
	 * list.
	 * 
	 * @param muxType
	 *            - This specifies an object identifier of the mux type to be
	 *            specified for the mux descriptor.
	 * @param termList
	 *            - This specifies list of termination ids for the specified mux
	 *            type.
	 * @throws IllegalArgumentException
	 *             : This exception is raised if the reference of MuxType or
	 *             Termination Id list passed to this method is NULL.
	 */
	public MuxDescriptor(MuxType muxType, Termination[] termList) throws IllegalArgumentException {

		super.descriptorId = DescriptorType.M_MUX_DESC;

		if (muxType == null) {
			throw new IllegalArgumentException("MuxType must not be null");
		}

		if (termList == null) {
			throw new IllegalArgumentException("Termination[] must not be null");
		}

		if (termList.length == 0) {
			throw new IllegalArgumentException("Termination[] lmust not be empty");
		}

		this.muxType = muxType;
		this.termList = termList;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns that the descriptor identifier is of type Mux descriptor. This
	 * method overrides the corresponding method of the base class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object as the type
	 *         of Mux descriptor. It returns that it is Mux Descriptor i.e.,
	 *         M_MUX_DESC.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * This method cannot be overridden by the derived class. This method
	 * returns the identity of the mux type. The constants for the mux type are
	 * defined in MuxType.
	 * 
	 * @return Returns value that identifies Mux type. It returns one of the
	 *         values defined in, MuxType.
	 */
	public final MuxType getMuxType() {
		return this.muxType;
	}

	/**
	 * This method returns the extension string of the mux type. The extension
	 * string should be prefixed with "X-" or "X+". The extension characters
	 * following the prefix should be at most of 6 characters. The extension
	 * string would be set only when the mux type specifies MUX_TYPE_EXTENSION.
	 * 
	 * @return Gets the string for the extension of the mux type. The extension
	 *         string would be set only when the mux type specifies
	 *         {@link MuxType.EXT}.
	 * @throws IllegalStateException
	 *             if the method has been called when the mux type denotes
	 *             anything other than {@link MuxType.EXT}.
	 */
	public java.lang.String getExtensionString() throws IllegalStateException {
		if (this.muxType.getMuxType() != muxType.M_EXT) {
			throw new IllegalStateException("MuxType must be: EXT");
		}
		return this.extMux;
	}

	/**
	 * Sets the string for the extension of the mux type. Should be set only
	 * when the mux type specifies MUX_TYPE_EXT.
	 * 
	 * @param extMux
	 *            - Sets the string for the extension of the mux type. The
	 *            extension string should be prefixed with "X-" or "X+". The
	 *            extension characters following the prefix should be at most of
	 *            6 characters. The extension string would be set only when the
	 *            mux type specifies {@link MuxType.EXT}.
	 * @throws IllegalArgumentException
	 *             if the extension string is not in proper format. It should be
	 *             prefixed with either "X+" or "X-" followed by at most 6
	 *             characters.
	 * @throws IllegalStateException
	 *             if the method has been called when the mux type denotes
	 *             anything other than {@link MuxType.EXT}.
	 */
	public void setExtensionString(java.lang.String extMux) throws IllegalArgumentException, IllegalStateException {

		if (this.muxType.getMuxType() != muxType.M_EXT) {
			throw new IllegalStateException("MuxType must be: EXT");
		}

		if (extMux == null) {
			new IllegalArgumentException("ExtMux must not be null");
		}

		DescriptorUtils.checkMethodExtensionRules(extMux);

		if (extMux.length() > 8) {
			throw new IllegalArgumentException("ExtMux must not be longer than 8 characters.");
		}
		this.extMux = extMux;

	}

	/**
	 * Gets the list of termination ids for which the mux type is specified.
	 * 
	 * @return Returns the list of termination ids for which the muxt type is
	 *         specified.
	 */
	public final Termination[] getTerminationIdList() {
		return this.termList;
	}

}

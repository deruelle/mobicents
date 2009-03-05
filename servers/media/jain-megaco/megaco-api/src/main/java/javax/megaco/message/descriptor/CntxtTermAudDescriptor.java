package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.MethodInvocationException;
import javax.megaco.message.Descriptor;
import javax.megaco.message.DescriptorType;
import javax.megaco.message.Termination;

/**
 * The class extends JAIN MEGACO Descriptor. This class describes the Context
 * termination audit descriptor.
 */
public class CntxtTermAudDescriptor extends Descriptor implements Serializable {

	private ErrorDescriptor errorDescriptor;
	private Termination[] terminationList;

	/**
	 * Constructs an Context Termination Descriptor object. This extends the
	 * Descriptor class.
	 */
	public CntxtTermAudDescriptor() {
		super.descriptorId = DescriptorType.M_CTX_TERM_AUDIT_DESC;
	}

	/**
	 * This method returns that the descriptor identifier is of type descriptor
	 * Context Termination Audit. This method overrides the corresponding method
	 * of the base class Descriptor.
	 * 
	 * @return Returns an integer value that identifies this object as the type
	 *         of Context Termination Audit descriptor. It returns the value
	 *         M_CTX_TERM_AUDIT_DESC for a Context Termination Audit Descriptor.
	 */
	public int getDescriptorId() {
		return super.descriptorId;
	}

	/**
	 * This method returns that the descriptor info of type Error descriptor.
	 * 
	 * @return Returns an descriptor info that identifies this object as the
	 *         type of Error descriptor. It returns that it is Error Descriptor
	 *         i.e., M_ERROR_DESC. If the descriptor is absent in the command,
	 *         then method would return NULL.
	 */
	public ErrorDescriptor getErrorDescriptor() {
		return this.errorDescriptor;
	}

	/**
	 * This method is used to get the list of the termination Ids.
	 * 
	 * @return The function returns vector of the Termination. If the descriptor
	 *         is absent in the command, then method would return NULL.
	 */
	public Termination[] getTerminationIdList() {
		return this.terminationList;
	}

	/**
	 * This method sets the Megaco Error descriptor.
	 * 
	 * @param errorDescriptor
	 *            - Megaco Error descriptor to be set for this command.
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid descriptor is set.
	 * @throws IllegalStateException
	 *             - Thrown if the Termination Id list has already been
	 *             specified for this command. The object of this class can have
	 *             either error descriptor or termination Id list associated
	 *             with it and not both.
	 */
	public void setErrorDescriptor(ErrorDescriptor errorDescriptor) throws IllegalArgumentException, IllegalStateException {
		if (errorDescriptor == null) {
			throw new IllegalArgumentException("ErrorDescriptor must not be null.");
		}

		if (this.terminationList != null) {
			throw new IllegalStateException("ErrorDescriptor must not be set when Termination[] is present");
		}

		this.errorDescriptor = errorDescriptor;
	}

	/**
	 * The method is used to set the Megaco Termination Id List.
	 * 
	 * @param termIdList
	 *            - Vector of Megaco terminationId descriptor to be set for this
	 *            command.
	 * @throws IllegalArgumentException
	 *             - Thrown if an invalid descriptor is set.
	 * @throws IllegalStateException
	 *             - Thrown if the error descriptor has already been specified
	 *             for this command. The object of this class can have either
	 *             error descriptor or termination Id list associated with it
	 *             and not both.
	 */
	public void setTerminationId(Termination[] termIdList) throws IllegalArgumentException, IllegalStateException {
		if (termIdList == null) {
			throw new IllegalArgumentException("Termination[] must not be null.");
		}

		if (termIdList.length == 0) {
			throw new IllegalArgumentException("Termination[] must not be empty.");
		}

		if (this.errorDescriptor != null) {
			throw new IllegalStateException("Termination[] must not be set when ErrorDescriptor is present");
		}

		this.terminationList = termIdList;
	}
}

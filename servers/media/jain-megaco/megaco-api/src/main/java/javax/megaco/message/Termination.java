package javax.megaco.message;

import java.io.Serializable;

import javax.megaco.ExceptionInfoCode;

/**
 * The Jain MEGACO termination class is used to get the detailed information
 * about the termination and the wildcard data for the termination.
 * 
 * 
 */
public class Termination implements Serializable {

	private String terminationName = null;
	private byte[] terminationNameBinary = null;
	private TermType termType = null;
	private byte[] termWC = null;

	public Termination() {

	}

	/**
	 * Sets the termination name as a string value for text encoding.
	 * 
	 * @param termName
	 *            An string format of the termination name.
	 * @throws IllegalArgumentException
	 *             If the termination name is set such that it violates the
	 *             protocol.
	 */
	public void setTermName(java.lang.String termName) throws IllegalArgumentException {
		if (termName == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("Termination Name cannot be null");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.SYNTAX_ERR_IN_TERM_NAME);
			throw invalidArgumentException;
		}
		this.terminationName = termName;

	}

	/**
	 * Sets the termination name as a byte stream for binary encoding.
	 * 
	 * @param termName
	 *            A byte stream of the termination name.
	 * @throws IllegalArgumentException
	 *             If the termination name is set such that it violates the
	 *             syntax defined by the protocol.
	 */
	public void setTermName(byte[] termName) throws IllegalArgumentException {
		if (termName == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("Termination Name cannot be null");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.SYNTAX_ERR_IN_TERM_NAME);
			throw invalidArgumentException;
		}
		this.terminationNameBinary = termName;
	}

	/**
	 * Sets a reference object corresponding to the termination type for the
	 * termination name set.
	 * 
	 * @param termType
	 *            A reference to an object specifying the type of the
	 *            termination name. These could be one of normal, choose, root
	 *            or wildcarded.
	 * @throws IllegalArgumentException
	 *             This exception is raised if the reference of Termination Type
	 *             passed to this method is NULL.
	 */
	public void setTermType(TermType termType) throws IllegalArgumentException {
		if (termType == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("TermType cannot be null");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_TERM_TYPE);
			throw invalidArgumentException;
		}
		this.termType = termType;
	}

	/**
	 * Sets the termination name wild card as a byte stream for binary encoding.
	 * 
	 * @param termWC
	 *            A byte stream of the termination name wildcarding information.
	 * @throws IllegalArgumentException
	 *             If the termination name is set such that it violates the
	 *             protocol.
	 */
	public void setTermWildCard(byte[] termWC) throws IllegalArgumentException {
		if (termWC == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("TermType cannot be null");
			// invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_TERM_TYPE);
			throw invalidArgumentException;
		}
		this.termWC = termWC;
	}

	/**
	 * Gets the termination name assuming the termination name is string
	 * encoded.
	 * 
	 * 
	 * @return Returns a string for the text termination identifier.
	 */
	public java.lang.String getTextTermName() {
		return this.terminationName;
	}

	/**
	 * Gets the termination name assuming the termination name is octect byte
	 * encoded for binary encoding.
	 * 
	 * @return Returns a binary encoded termination name in byte format.
	 */
	public byte[] getBinTermName() {
		return this.terminationNameBinary;
	}

	/**
	 * Gets the termination type as an integer value. See
	 * javax.megaco.message.TermType for the definition of the constants for the
	 * termination type.
	 * 
	 * @return Returns a termination type as an integer value.
	 */
	public int getTermType() {
		return this.termType.getTermType();
	}

	/**
	 * Gets the wild card bytes for termination name assuming the termination
	 * name is received in binary format and returns a wildcard octet.
	 * 
	 * @return Returns a binary encoded termination name wild card in byte
	 *         format.
	 */
	public byte[] getTermWildCard() {
		return this.termWC;
	}

	@Override
	public java.lang.String toString() {
		return "Text Format : Term Name = " + this.terminationName + " Termtype = " + this.termType + ". Binnary Format : Term Name = " + this.terminationNameBinary + " Termtype = " + this.termWC;
	}
}

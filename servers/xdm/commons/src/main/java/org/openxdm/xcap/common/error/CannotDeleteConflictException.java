package org.openxdm.xcap.common.error;

public class CannotDeleteConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CannotDeleteConflictException() { }

	protected String getConflictError() {
		return "<cannot-delete />";
	}

}

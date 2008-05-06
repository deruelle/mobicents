package org.openxdm.xcap.common.error;

public class NotWellFormedConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotWellFormedConflictException() {}
	
	protected String getConflictError() {
		return "<not-well-formed />";
	}

}

package org.openxdm.xcap.common.error;

public class UniquenessFailureConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UniquenessFailureConflictException() {}
	
	protected String getConflictError() {
		//TODO add exists
		return "<uniqueness-failure />";
	}

}

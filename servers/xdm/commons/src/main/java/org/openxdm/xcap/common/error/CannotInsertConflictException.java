package org.openxdm.xcap.common.error;

public class CannotInsertConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CannotInsertConflictException() {}

	protected String getConflictError() {
		return "<cannot-insert />";
	}

}

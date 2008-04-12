package org.openxdm.xcap.common.error;

public class NotUTF8ConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotUTF8ConflictException() {}
	
	protected String getConflictError() {
		return "<not-utf-8 />";
	}
}

package org.openxdm.xcap.common.error;

public class SchemaValidationErrorConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public SchemaValidationErrorConflictException() {}
	
	protected String getConflictError() {
		return "<schema-validation-error />";
	}

}

package org.openxdm.xcap.common.error;

public class NotXMLAttributeValueConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotXMLAttributeValueConflictException() {}
	
	protected String getConflictError() {
		return "<not-xml-att-value />";
	}

}
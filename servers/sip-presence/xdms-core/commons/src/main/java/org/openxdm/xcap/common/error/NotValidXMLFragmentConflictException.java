package org.openxdm.xcap.common.error;

public class NotValidXMLFragmentConflictException extends ConflictException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotValidXMLFragmentConflictException() {}
	
	protected String getConflictError() {
		return "<not-xml-frag />";
	}

}
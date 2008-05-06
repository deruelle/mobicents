package org.openxdm.xcap.common.uri;

public class ParseException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String validParent;
	
	public ParseException(String validParent) {
		super();
		this.validParent = validParent;
	}

	public ParseException(String validParent,String msg) {
		super(msg);
		this.validParent = validParent;
	}
	
	public String getValidParent() {
		return this.validParent;
	}
}
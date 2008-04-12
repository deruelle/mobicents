package org.openxdm.xcap.common.error;

public class UnsupportedMediaTypeException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnsupportedMediaTypeException() {}

	public int getResponseStatus() {
		return 415;
	}

	public String getResponseContent() {		
		return null;
	}	
	
}

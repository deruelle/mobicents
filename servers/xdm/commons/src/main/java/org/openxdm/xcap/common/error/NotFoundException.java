package org.openxdm.xcap.common.error;

public class NotFoundException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException() {}

	public int getResponseStatus() {		
		return 404;
	}
	
}
package org.openxdm.xcap.common.error;

public class BadRequestException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int RESPONSE_STATUS = 400;
	
	public BadRequestException() {}
	
	public int getResponseStatus() {
		return RESPONSE_STATUS;
	}

}
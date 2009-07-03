package org.openxdm.xcap.common.error;

public class NotAuthorizedRequestException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int RESPONSE_STATUS = 403;
	
	public NotAuthorizedRequestException() {}
	
	public int getResponseStatus() {
		return RESPONSE_STATUS;
	}

}
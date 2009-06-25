package org.openxdm.xcap.common.error;

public class InternalServerErrorException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InternalServerErrorException(String msg) {
		super(msg);
	}
	
	public InternalServerErrorException(String msg, Throwable e) {
		super(msg,e);
	}
	
	public int getResponseStatus() {
		return 500;
	}

}

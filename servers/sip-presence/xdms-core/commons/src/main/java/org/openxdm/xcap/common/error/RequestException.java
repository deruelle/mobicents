package org.openxdm.xcap.common.error;

public abstract class RequestException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestException(String msg) {
		super(msg);
	}
	
	public RequestException(String msg,Throwable e) {
		super(msg,e);
	}
	
	public RequestException() {
		super();
	}
	
	public abstract int getResponseStatus();	
	
}

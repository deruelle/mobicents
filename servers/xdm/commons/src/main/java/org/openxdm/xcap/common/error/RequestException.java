package org.openxdm.xcap.common.error;

public abstract class RequestException extends Exception {
	
	public RequestException(String msg) {
		super(msg);
	}
	
	public RequestException() {
		super();
	}
	
	public abstract int getResponseStatus();	
	
}

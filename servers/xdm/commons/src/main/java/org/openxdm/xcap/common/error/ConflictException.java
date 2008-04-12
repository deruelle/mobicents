package org.openxdm.xcap.common.error;

public abstract class ConflictException extends RequestException {	
		
	public int getResponseStatus() {
		return 409;
	}
	
	protected abstract String getConflictError();
	
	public String getResponseContent() {
		StringBuffer sb = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><xcap-error xmlns='urn:ietf:params:xml:ns:xcap-error'>");
		sb.append(getConflictError());
		sb.append("</xcap-error>");
		return sb.toString();
	}
		
}

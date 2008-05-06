package org.openxdm.xcap.common.error;

import java.util.Map;

public class MethodNotAllowedException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int RESPONSE_STATUS = 405;
	private Map<String,String> responseHeaders = null;
	
	public MethodNotAllowedException(Map<String,String> responseHeaders) {
		if (responseHeaders == null) {
			throw new IllegalArgumentException("responseHeaders can't be null");
		}
		this.responseHeaders = responseHeaders;
	}

	public int getResponseStatus() {		
		return RESPONSE_STATUS;
	}
	
	public String getResponseContent() {		
		return null;
	}

	public Map<String,String> getResponseHeaders() {
		return responseHeaders;
	}
}

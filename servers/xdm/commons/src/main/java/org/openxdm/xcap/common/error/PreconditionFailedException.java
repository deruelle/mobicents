package org.openxdm.xcap.common.error;

public class PreconditionFailedException extends RequestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PreconditionFailedException() {}

	public int getResponseStatus() {		
		return 412;
	}

	public String getResponseContent() {		
		return null;
	}

}

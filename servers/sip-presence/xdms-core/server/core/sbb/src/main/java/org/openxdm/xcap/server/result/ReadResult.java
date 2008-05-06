package org.openxdm.xcap.server.result;

import org.openxdm.xcap.common.resource.Resource;

public class ReadResult {

	private Resource responseDataObject;
	private String responseEntityTag;
		
	public ReadResult(String responseEntityTag,Resource responseDataObject) {
		this.responseEntityTag = responseEntityTag;
		this.responseDataObject = responseDataObject;		
	}
	
	public Resource getResponseDataObject() {
		return responseDataObject;
	}
	
	public String getResponseEntityTag() {
		return responseEntityTag;
	}
		
}

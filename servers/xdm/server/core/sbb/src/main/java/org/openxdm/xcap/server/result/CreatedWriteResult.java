package org.openxdm.xcap.server.result;

public class CreatedWriteResult implements WriteResult {

	private String responseEntityTag = null;
	
	public CreatedWriteResult(String responseEntityTag) {
		this.responseEntityTag = responseEntityTag;
	}
	
	public CreatedWriteResult() {}
	
	public String getResponseEntityTag() {
		return responseEntityTag;
	}

	public void setResponseEntityTag(String responseEntityTag) {
		this.responseEntityTag = responseEntityTag;
	}
	
	public int getResponseStatus() {
		return 201;
	}

}

package org.openxdm.xcap.server.result;

public class OKWriteResult implements WriteResult {

	private String responseEntityTag = null;
	
	public OKWriteResult() {}
	
	public OKWriteResult(String responseEntityTag) {
		this.responseEntityTag = responseEntityTag;
	}
	
	public String getResponseEntityTag() {
		return responseEntityTag;
	}
	
	public void setResponseEntityTag(String responseEntityTag) {
		this.responseEntityTag = responseEntityTag;		
	}
	
	public int getResponseStatus() {
		return 200;
	}

}

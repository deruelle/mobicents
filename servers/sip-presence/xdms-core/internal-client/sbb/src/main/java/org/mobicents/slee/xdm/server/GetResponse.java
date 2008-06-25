package org.mobicents.slee.xdm.server;

public class GetResponse {

	private final String eTag;
	private final String mimetype;
	private final String content;
	
	public GetResponse(String eTag, String mimetype, String content) {
		this.eTag = eTag;
		this.mimetype = mimetype;
		this.content = content;
	}

	public String getETag() {
		return eTag;
	}

	public String getMimetype() {
		return mimetype;
	}
	
	public String getContent() {
		return content;
	}
	
	
}

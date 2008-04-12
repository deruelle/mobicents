package org.openxdm.xcap.common.resource;

public class DocumentResource implements Resource {	
	
	private String document;
	private String mimetype;
	
	public DocumentResource(String document, String mimetype) {
		this.document = document;
		this.mimetype = mimetype;
	}
	
	public String getDocument() {
		return document;
	}
	
	public String getMimetype() {
		return mimetype;
	}

	public String toXML() {
		return document;
	}
	
	private static final long serialVersionUID = 1L;
}

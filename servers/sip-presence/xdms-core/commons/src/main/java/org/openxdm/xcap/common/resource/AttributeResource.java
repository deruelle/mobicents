package org.openxdm.xcap.common.resource;

public class AttributeResource implements Resource {	
	
	public static final String MIMETYPE = "application/xcap-att+xml";
	
	private String attribute;
	
	public AttributeResource(String attribute) {
		this.attribute = attribute;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	public String getMimetype() {
		return MIMETYPE;
	}

	public String toXML() {
		return attribute;
	}
	
	private static final long serialVersionUID = 1L;
}

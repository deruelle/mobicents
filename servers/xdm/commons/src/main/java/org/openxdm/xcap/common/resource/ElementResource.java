package org.openxdm.xcap.common.resource;

public class ElementResource implements Resource {	
	
	public static final String MIMETYPE = "application/xcap-el+xml";
	
	private Object element;
	
	public ElementResource(Object element) {
		this.element = element;
	}
	
	public Object getElement() {
		return element;
	}
	
	public String getMimetype() {
		return MIMETYPE;
	}

	public String toXML() {
		return element.toString();
	}
	
	private static final long serialVersionUID = 1L;
}

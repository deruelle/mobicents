package org.openxdm.xcap.common.resource;

import java.util.Iterator;
import java.util.Map;

public class NamespaceBindings implements Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String elementLocalName;
	private Map<String,String> namespaceBindings;
	
	public NamespaceBindings(String elementLocalName, Map<String,String> namespaceBindings) {
		this.elementLocalName = elementLocalName;
		this.namespaceBindings = namespaceBindings;
	}
	
	public String getMimetype() {		
		return "application/xcap-ns+xml";
	}

	public String getElementLocalName() {
		return elementLocalName;
	}
	
	public Map<String,String> getNamespaceBindings() {
		return namespaceBindings;
	}
	
	public String toXML() {
		StringBuilder sb = new StringBuilder("<").append(elementLocalName);
		for(Iterator<String> it=namespaceBindings.keySet().iterator();it.hasNext();) {
			String namespaceKey = it.next();
			sb.append(' ').append(namespaceKey).append("=\"").append(namespaceBindings.get(namespaceKey)).append("\"");
		}
		sb.append("/>");
		return sb.toString();
	}

}

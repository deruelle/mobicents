package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;
import java.util.Map;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;
import org.w3c.dom.Element;

public final class ElementUpdatedEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * IMPORTANT: must sync with the event descriptor data!!!!
	 */
	public static final String EVENT_NAME = "ElementUpdatedEvent";
	public static final String EVENT_VENDOR = "org.openxdm";
	public static final String EVENT_VERSION = "1.0";
		
	private final DocumentSelector documentSelector;
	private final NodeSelector nodeSelector;
	private final Map<String, String> namespaces;
	private final String oldETag;
	private final String newETag;
	private final String documentAsString;
	private final String elementAsString;
	private final Element element;
	
	public ElementUpdatedEvent(DocumentSelector documentSelector,
			NodeSelector nodeSelector, Map<String, String> namespaces,
			String oldETag, String newETag, String documentAsString,
			String elementAsString, Element element) {
		if (newETag == null) {
			throw new IllegalArgumentException("newETag arg can't be null");
		}
		this.documentSelector = documentSelector;
		this.nodeSelector = nodeSelector;
		this.namespaces = namespaces;
		this.oldETag = oldETag;
		this.newETag = newETag;
		this.documentAsString = documentAsString;
		
		this.elementAsString = elementAsString;
		this.element = element;
	}

	public String getDocumentAsString() {
		return documentAsString;
	}
	
	public DocumentSelector getDocumentSelector() {
		return documentSelector;
	}
	
	public Element getElement() {
		return element;
	}
	
	public String getElementAsString() {
		return elementAsString;
	}
	
	public String getNewETag() {
		return newETag;
	}
	
	public Map<String, String> getNamespaces() {
		return namespaces;
	}
	
	public NodeSelector getNodeSelector() {
		return nodeSelector;
	}
	
	public String getOldETag() {
		return oldETag;
	}
	
	public boolean equals(Object o) {
		if (o != null && o.getClass() == this.getClass()) {
			return ((ElementUpdatedEvent)o).newETag.equals(newETag);
		}
		else {
			return false;
		}	
	}
	
	public int hashCode() {		
		return newETag.hashCode();
	}
}

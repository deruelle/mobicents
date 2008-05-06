package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;
import java.util.Map;

import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;

public final class AttributeUpdatedEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * IMPORTANT: must sync with the event descriptor data!!!!
	 */
	public static final String EVENT_NAME = "AttributeUpdatedEvent";
	public static final String EVENT_VENDOR = "org.openxdm";
	public static final String EVENT_VERSION = "1.0";
		
	private final DocumentSelector documentSelector;
	private final NodeSelector nodeSelector;
	private final AttributeSelector attributeSelector;
	private final Map<String, String> namespaces;
	private final String oldETag;
	private final String newETag;
	private final String documentAsString;
	
	private final String attributeValue;
	
	public AttributeUpdatedEvent(DocumentSelector documentSelector,
			NodeSelector nodeSelector, AttributeSelector attributeSelector,
			Map<String, String> namespaces, String oldETag, String newETag,
			String documentAsString, String attributeValue) {
		if (newETag == null) {
			throw new IllegalArgumentException("newETag arg can't be null");
		}
		this.documentSelector = documentSelector;
		this.nodeSelector = nodeSelector;
		this.attributeSelector = attributeSelector;
		this.namespaces = namespaces;
		this.oldETag = oldETag;
		this.newETag = newETag;
		this.documentAsString = documentAsString;
		
		this.attributeValue = attributeValue;
	}

	public AttributeSelector getAttributeSelector() {
		return attributeSelector;
	}
	
	public String getAttributeValue() {
		return attributeValue;
	}
	
	public String getDocumentAsString() {
		return documentAsString;
	}
	
	public DocumentSelector getDocumentSelector() {
		return documentSelector;
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
			return ((AttributeUpdatedEvent)o).newETag.equals(newETag);
		}
		else {
			return false;
		}	
	}
	
	public int hashCode() {		
		return newETag.hashCode();
	}
}

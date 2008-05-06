package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.w3c.dom.Document;

public final class DocumentUpdatedEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * IMPORTANT: must sync with the event descriptor data!!!!
	 */
	public static final String EVENT_NAME = "DocumentUpdatedEvent";
	public static final String EVENT_VENDOR = "org.openxdm";
	public static final String EVENT_VERSION = "1.0";
	
	private final String id;
		
	private final DocumentSelector documentSelector;
	private final String oldETag;
	private final String newETag;
	private final String documentAsString;
	private final Document document;
	
	public DocumentUpdatedEvent(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString,
			Document document) {		
		this.documentSelector = documentSelector;
		this.oldETag = oldETag;
		this.newETag = newETag;
		this.documentAsString = documentAsString;
		this.document = document;
		// if doc was deleted add a non hex char to the old etag as event id
		id = (newETag != null) ? newETag : oldETag + "g";
	}
	
	public Document getDocument() {
		return document;
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
	
	public String getOldETag() {
		return oldETag;
	}
	
	public boolean equals(Object o) {
		if (o != null && o.getClass() == this.getClass()) {
			return ((DocumentUpdatedEvent)o).id.equals(id);
		}
		else {
			return false;
		}	
	}
	
	public int hashCode() {		
		return id.hashCode();
	}
}

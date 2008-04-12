package org.openxdm.xcap.server.slee.resource.datasource;

import java.io.Serializable;

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
	
	private String documentName = null;
	private String documentCollectionName = null;
	private Document oldDocument = null;
	private Document newDocument = null;
	
	public DocumentUpdatedEvent(String documentCollectionName, String documentName, Document oldDocument, Document newDocument) {
		this.documentCollectionName = documentCollectionName;
		this.documentName = documentName;		
		this.oldDocument = oldDocument;
		this.newDocument = newDocument;
	}
	
	public String getDocumentCollectionName() {
		return documentCollectionName;
	}
	
	public String getDocumentName() {
		return documentName;
	}
	
	public Document getNewDocument() {
		return newDocument;
	}
	
	public Document getOldDocument() {
		return oldDocument;
	}
	
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o == null) return false;		
		return (o instanceof DocumentUpdatedEvent) && ((DocumentUpdatedEvent)o).documentName.equals(documentName) && ((DocumentUpdatedEvent)o).documentCollectionName.equals(documentCollectionName);
	}
	
	public int hashCode() {		
		return documentCollectionName.hashCode()*31+documentName.hashCode();
	}
}

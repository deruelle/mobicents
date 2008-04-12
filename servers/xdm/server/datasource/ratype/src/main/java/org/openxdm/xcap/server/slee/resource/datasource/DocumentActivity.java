package org.openxdm.xcap.server.slee.resource.datasource;

import javax.slee.resource.ActivityHandle;

import org.openxdm.xcap.common.uri.DocumentSelector;

/**
 * Activity Object for the DataSource Resource Adaptor. Represents the events
 * that update a Document.
 * 
 * @author Eduardo Martins
 * 
 */
public class DocumentActivity implements ActivityHandle {

	private final DocumentSelector documentSelector;

	public DocumentActivity(DocumentSelector documentSelector) {
		this.documentSelector = documentSelector;
	}

	public DocumentSelector getDocumentSelector() {
		return documentSelector;
	}

	

	public int hashCode() {
		return documentSelector.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			DocumentActivity other = (DocumentActivity) obj;
			return this.documentSelector
					.equals(other.documentSelector);
		}
		return false;
	}

	public String toString() {
		return new StringBuilder("DocumentActivity[documentSelector="
				+ documentSelector+ "]").toString();
	}
}

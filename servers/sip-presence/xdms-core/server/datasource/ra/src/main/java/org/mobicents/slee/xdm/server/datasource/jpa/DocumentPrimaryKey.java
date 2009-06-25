package org.mobicents.slee.xdm.server.datasource.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * 
 * @author eduardomartins
 * 
 */

@Embeddable
public class DocumentPrimaryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6638892043798746768L;

	@Column(name = "DOCUMENT_NAME", nullable = false)
	private String documentName;

	@ManyToOne(optional=false)
    @JoinColumns ({
    	@JoinColumn(name="APPUSAGE_ID", referencedColumnName = "APPUSAGE_ID"),
    	@JoinColumn(name="COLLECTION_NAME", referencedColumnName = "COLLECTION_NAME")
    	})
	private Collection collection;

	public DocumentPrimaryKey() {
		// TODO Auto-generated constructor stub
	}
	
	public DocumentPrimaryKey(String documentName, Collection collection) {
		setDocumentName(documentName);
		setCollection(collection);
	}

	// -- GETTERS AND SETTERS

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			DocumentPrimaryKey other = (DocumentPrimaryKey) obj;
			return this.documentName.equals(other.documentName)
					&& this.collection.equals(other.collection);
		} else {
			return false;
		}
	}

	public int hashCode() {
		int result;
		result = documentName.hashCode();
		result = 31 * result + collection.hashCode();
		return result;
	}

	public String toString() {
		return "DocumentPrimaryKey : documentName = " + documentName
				+ " , collection = " + collection;
	}

}
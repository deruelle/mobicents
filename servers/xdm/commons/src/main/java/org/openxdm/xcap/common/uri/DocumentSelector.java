package org.openxdm.xcap.common.uri;

/**
 * A document selector points to a document resource on
 * a XCAP server. It's built from a application usage id (auid), document selector string, a document parent selector string, and the document name.
 * 
 * Usage Example that creates a document selector pointing to 'resource-lists' document named
 * 'index', for user 'sip:eduardo@openxdm.org'
 * 
 * DocumentSelector documentSelector = new DocumentSelector(
 * "resource-lists", "users/user/sip%3Aeduardo%40openxdm.org",index");
 * 
 * Note that you need to take care of percent enconding chars that are not
 * allowed in a valid URI.
 * 
 * @author Eduardo Martins
 *
 */

public class DocumentSelector {

	private String auid = null;

	private String documentParent = null;
	private String completeDocumentParent = null;

	private String documentName = null;

	/**
	 * Creates a new instance of a document selector, from the specified application usage id (auid), document parent and document name. 
	 * @param auid the application usage id of the document resource.
	 * @param documentParent the parent of the document.
	 * @param documentName the document name.
	 */
	public DocumentSelector(String auid, String documentParent,
			String documentName) {
		this.documentParent = documentParent;
		this.documentName = documentName;
		this.auid = auid;
		completeDocumentParent = new StringBuilder("/").append(auid).append('/').append(documentParent).toString();
	}

	/**
	 * Retreives the application usage id of the document resource.
	 * @return
	 */
	public String getAUID() {
		return auid;
	}

	/**
	 * Retreives the document's name of the document resource. 
	 * @return
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Retreives the document's parent of the document resource, relative to the auid 
	 * @return
	 */
	public String getDocumentParent() {
		return documentParent;
	}
	
	/**
	 * Retreives the document's parent of the document resource, including the auid 
	 * @return
	 */
	public String getCompleteDocumentParent() {
		return completeDocumentParent;
	}

	public String toString() {
		return new StringBuilder(completeDocumentParent).append('/').append(documentName).toString();
	}
	
	public int hashCode() {
		return (auid.hashCode()*31+documentParent.hashCode())*31+documentName.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			DocumentSelector other = (DocumentSelector) obj;
			return this.auid.equals(other.auid) && this.documentParent.equals(other.documentParent) && this.documentName.equals(other.documentName);
		}
		else {
			return false;
		}
	}
}

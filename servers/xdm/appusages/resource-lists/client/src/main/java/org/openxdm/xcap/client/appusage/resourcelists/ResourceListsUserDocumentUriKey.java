package org.openxdm.xcap.client.appusage.resourcelists;

import org.openxdm.xcap.client.key.UserDocumentUriKey;

public class ResourceListsUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceListsUserDocumentUriKey(String user,String documentName) {
		super("resource-lists",user,documentName);
	}
	
}

package org.openxdm.xcap.client.appusage.resourcelists.key;

import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class ResourceListsUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceListsUserDocumentUriKey(String user,String documentName) {
		super("resource-lists",user,documentName);
	}
	
}

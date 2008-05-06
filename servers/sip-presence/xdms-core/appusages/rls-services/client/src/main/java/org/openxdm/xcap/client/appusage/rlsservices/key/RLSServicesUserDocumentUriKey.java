package org.openxdm.xcap.client.appusage.rlsservices.key;

import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class RLSServicesUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RLSServicesUserDocumentUriKey(String user,String documentName) {
		super("rls-services",user,documentName);
	}
	
}

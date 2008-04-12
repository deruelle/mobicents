package org.openxdm.xcap.client.appusage.presrules;

import org.openxdm.xcap.client.key.UserDocumentUriKey;

public class PresRulesUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PresRulesUserDocumentUriKey(String user,String documentName) {
		super("pres-rules",user,documentName);
	}
	
}

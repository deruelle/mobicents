package org.openxdm.xcap.client.appusage.presrules.key;

import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class PresRulesUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PresRulesUserDocumentUriKey(String user,String documentName) {
		super("pres-rules",user,documentName);
	}
	
}

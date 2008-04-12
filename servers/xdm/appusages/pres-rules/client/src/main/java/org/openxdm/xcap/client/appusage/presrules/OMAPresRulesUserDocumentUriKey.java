package org.openxdm.xcap.client.appusage.presrules;

import org.openxdm.xcap.client.key.UserDocumentUriKey;

public class OMAPresRulesUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OMAPresRulesUserDocumentUriKey(String user,String documentName) {
		super("org.openmobilealliance.pres-rules",user,documentName);
	}
	
}


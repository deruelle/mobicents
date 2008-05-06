package org.openxdm.xcap.client.appusage.omapresrules.key;

import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class OMAPresRulesUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OMAPresRulesUserDocumentUriKey(String user,String documentName) {
		super("org.openmobilealliance.pres-rules",user,documentName);
	}
	
}


package org.openxdm.xcap.client.appusage.xcapcaps.key;

import org.openxdm.xcap.common.key.UserDocumentUriKey;

public class XcapCapsUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XcapCapsUserDocumentUriKey(String user,String documentName) {
		super("xcap-caps",user,documentName);
	}
	
}

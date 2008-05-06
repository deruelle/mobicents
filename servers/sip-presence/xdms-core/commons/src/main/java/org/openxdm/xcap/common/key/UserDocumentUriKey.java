package org.openxdm.xcap.common.key;

import org.openxdm.xcap.common.uri.DocumentSelector;

public class UserDocumentUriKey extends DocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String user;	
	
	public UserDocumentUriKey(String auid, String user,String documentName) {
		super(new DocumentSelector(auid,KeyUtils.getPercentEncodedDocumentParent(user),documentName));
		this.user = user;
	}
	
	public String getUser() {
		return user;
	}
}

package org.openxdm.xcap.client.test.error;

import org.openxdm.xcap.client.key.UserDocumentUriKey;
import org.openxdm.xcap.client.key.UserAttributeUriKey;

public class AnotherUnsupportedMediaTypeFakeDocumentUriKey extends UserDocumentUriKey {

	private UserAttributeUriKey key;
	
	public AnotherUnsupportedMediaTypeFakeDocumentUriKey(UserAttributeUriKey key) {
		super(key.getDocumentSelector().getAUID(), key.getUser(), key.getDocumentSelector().getDocumentName());
		this.key = key;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return key.toString();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

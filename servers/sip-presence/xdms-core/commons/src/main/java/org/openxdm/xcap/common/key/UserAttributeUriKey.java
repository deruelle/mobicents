package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;

public class UserAttributeUriKey extends AttributeUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String user;
	
	public UserAttributeUriKey(String auid, String user, String documentName, ElementSelector elementSelector,AttributeSelector attributeSelector,Map<String,String> namespaces) {
		super(new DocumentSelector(auid,KeyUtils.getPercentEncodedDocumentParent(user),documentName),elementSelector,attributeSelector,namespaces);
		this.user = user;
	}	
	
	public String getUser() {
		return user;
	}
	
}

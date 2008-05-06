package org.openxdm.xcap.common.key;

import java.util.Map;

import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;

public class UserNamespaceBindingsUriKey extends NamespaceBindingsUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String user;
	
	public UserNamespaceBindingsUriKey(String auid,String user,String documentName,ElementSelector elementSelector,Map<String,String> namespaces) {
		super(new DocumentSelector(auid,KeyUtils.getPercentEncodedDocumentParent(user),documentName),elementSelector,namespaces);
		this.user = user;
	}	
	
	public String getUser() {
		return user;
	}
}

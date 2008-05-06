package org.openxdm.xcap.client.test.error;

import java.util.Map;

import org.openxdm.xcap.common.key.UserDocumentUriKey;
import org.openxdm.xcap.common.key.UserNamespaceBindingsUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;

public class MethodNotAllowedTestFakeUserDocumentUriKey extends UserDocumentUriKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserNamespaceBindingsUriKey nsKey;
	
	public MethodNotAllowedTestFakeUserDocumentUriKey(String auid, String user, String documentName, ElementSelector elementSelector, Map<String,String> namespaces) {
		super(auid, user, documentName);
		nsKey = new UserNamespaceBindingsUriKey(auid,user,documentName,elementSelector,namespaces);
	}

	@Override
	public String toString() {
		return nsKey.toString();
	}
}

package org.openxdm.xcap.client.appusage.resourcelists.key;

import java.util.Map;

import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;

public class ResourceListsUserElementUriKey extends UserElementUriKey {

	public ResourceListsUserElementUriKey(String user, String documentName, ElementSelector elementSelector, Map<String, String> namespaces) {
		super("resource-lists", user, documentName, elementSelector, namespaces);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

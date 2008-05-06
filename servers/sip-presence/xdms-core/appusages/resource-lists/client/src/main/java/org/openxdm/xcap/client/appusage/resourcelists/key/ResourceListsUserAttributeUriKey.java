package org.openxdm.xcap.client.appusage.resourcelists.key;

import java.util.Map;

import org.openxdm.xcap.common.key.UserAttributeUriKey;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.ElementSelector;

public class ResourceListsUserAttributeUriKey extends UserAttributeUriKey {

	public ResourceListsUserAttributeUriKey(String user, String documentName, ElementSelector elementSelector, AttributeSelector attributeSelector, Map<String, String> namespaces) {
		super("resource-lists", user, documentName, elementSelector, attributeSelector, namespaces);		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

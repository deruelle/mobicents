package org.openxdm.xcap.client.appusage.rlsservices.key;

import java.util.Map;

import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;

public class RLSServicesUserElementUriKey extends UserElementUriKey {

	public RLSServicesUserElementUriKey(String user, String documentName, ElementSelector elementSelector, Map<String, String> namespaces) {
		super("rls-services", user, documentName, elementSelector, namespaces);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

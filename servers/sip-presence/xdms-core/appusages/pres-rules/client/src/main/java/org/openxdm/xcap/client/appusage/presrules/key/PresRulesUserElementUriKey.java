package org.openxdm.xcap.client.appusage.presrules.key;

import java.util.Map;

import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;

public class PresRulesUserElementUriKey extends UserElementUriKey {

	public PresRulesUserElementUriKey(String user, String documentName, ElementSelector elementSelector, Map<String, String> namespaces) {
		super("pres-rules", user, documentName, elementSelector, namespaces);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

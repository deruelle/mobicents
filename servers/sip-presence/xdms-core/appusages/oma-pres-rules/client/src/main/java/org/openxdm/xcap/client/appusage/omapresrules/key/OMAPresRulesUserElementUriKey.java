package org.openxdm.xcap.client.appusage.omapresrules.key;

import java.util.Map;

import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;

public class OMAPresRulesUserElementUriKey extends UserElementUriKey {

	public OMAPresRulesUserElementUriKey(String user, String documentName, ElementSelector elementSelector, Map<String, String> namespaces) {
		super("org.openmobilealliance.pres-rules", user, documentName, elementSelector, namespaces);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

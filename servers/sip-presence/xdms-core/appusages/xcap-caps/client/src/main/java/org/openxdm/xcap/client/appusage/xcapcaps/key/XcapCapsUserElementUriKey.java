package org.openxdm.xcap.client.appusage.xcapcaps.key;

import java.util.Map;

import org.openxdm.xcap.common.key.UserElementUriKey;
import org.openxdm.xcap.common.uri.ElementSelector;

public class XcapCapsUserElementUriKey extends UserElementUriKey {

	public XcapCapsUserElementUriKey(String user, String documentName, ElementSelector elementSelector, Map<String, String> namespaces) {
		super("xcap-caps", user, documentName, elementSelector, namespaces);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

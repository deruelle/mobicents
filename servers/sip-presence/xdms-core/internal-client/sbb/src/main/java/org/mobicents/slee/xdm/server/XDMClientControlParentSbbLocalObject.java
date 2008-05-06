package org.mobicents.slee.xdm.server;

import javax.slee.SbbLocalObject;

import org.openxdm.xcap.common.uri.DocumentSelector;

public interface XDMClientControlParentSbbLocalObject extends SbbLocalObject {

	/**
	 * Callback that indicates a document subscribed in the XDM client was updated
	 * @param documentSelector
	 * @param document
	 */
	public void documentUpdated(DocumentSelector documentSelector, Object document);
	
}

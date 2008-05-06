package org.mobicents.slee.xdm.server;

import javax.slee.SbbLocalObject;

import org.openxdm.xcap.common.uri.DocumentSelector;

public interface XDMClientControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Sets the parent sbb to be used on call backs originated from messages received by the XDM
	 * @param parent
	 */
	public void setParentSbb(XDMClientControlParentSbbLocalObject parentSbb);
	
	/**
	 * Retrieves the xml document, stored on the XDM,  for the specified resource.
	 * @param documentSelector
	 * @return
	 */
	public Object getDocument(DocumentSelector documentSelector);
	
	/**
	 * Subscribes changes on a XML document, stored on the XDM, for the specified resource.
	 * @param documentSelector
	 */
	public void subscribeDocument(DocumentSelector documentSelector);
	
	/**
	 * Unsubscribes changes on a XML document, stored on the XDM, for the specified resource.
	 * @param documentSelector
	 */
	public void unsubscribeDocument(DocumentSelector documentSelector);
	
	/**
	 * Subscribes changes on XML documents of the specified app usage, stored on the XDM.
	 * @param auid
	 */
	public void subscribeAppUsage(String auid);
	
	/**
	 * Unsubscribes changes on XML documents of the specified app usage, stored on the XDM.
	 * @param auid
	 */
	public void unsubscribeAppUsage(String auid);
}

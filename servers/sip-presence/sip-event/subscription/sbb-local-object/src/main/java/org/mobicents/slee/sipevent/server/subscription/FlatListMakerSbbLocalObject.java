package org.mobicents.slee.sipevent.server.subscription;

import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ServiceType;

public interface FlatListMakerSbbLocalObject extends XDMClientControlParentSbbLocalObject {

	/**
	 * Used to set the call back sbb local object in the sbb implementing this
	 * interface. Must be used whenever a new object of this interface is
	 * created.
	 * 
	 * An example:
	 * 
	 * ChildRelation childRelation = getChildRelation();
	 * FlatListMakerSbbLocalObject childSbb =
	 * (FlatListMakerSbbLocalObject) childRelation.create();
	 * childSbb.setParentSbb(
	 * (FlatListMakerParentSbbLocalObject)this.getSbbContext().getSbbLocalObject());
	 * 
	 * 
	 * @param parent
	 */
	public void setParentSbb(
			FlatListMakerParentSbbLocalObject sbbLocalObject);

	/**
	 * Makes a flat list from a {@link ServiceType} contained list.
	 * @param serviceType
	 */
	public void makeFlatList(ServiceType serviceType);
	
}

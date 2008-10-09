package org.mobicents.slee.xdm.server.subscription;

import java.util.Map;

import javax.sip.header.HeaderFactory;
import javax.slee.CreateException;
import javax.slee.SLEEException;
import javax.slee.TransactionRequiredLocalException;
import javax.xml.bind.Unmarshaller;

import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;

/**
 * Extending the mandatory interfaces with methods needed by {@link XcapDiffSubscriptionControl}
 * @author martins
 *
 */
public interface XcapDiffSubscriptionControlSbbLocalObject extends XDMClientControlParentSbbLocalObject, ImplementedSubscriptionControlSbbLocalObject {

	public void setSubscriptionsMap(Map rules);
	public Map getSubscriptionsMap();
	
	public ImplementedSubscriptionControlParentSbbLocalObject getParentSbbCMP();

	public XDMClientControlSbbLocalObject getXDMClientControlSbb()
			throws TransactionRequiredLocalException, SLEEException,
			CreateException;
	
	public HeaderFactory getHeaderFactory();
	
	public Unmarshaller getUnmarshaller();
	
	public DataSourceSbbInterface getDataSourceSbbInterface();
	
}

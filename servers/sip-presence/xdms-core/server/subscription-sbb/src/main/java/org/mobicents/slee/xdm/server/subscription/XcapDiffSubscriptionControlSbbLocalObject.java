package org.mobicents.slee.xdm.server.subscription;

import java.util.Map;

import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.HeaderFactory;
import javax.xml.bind.Unmarshaller;

import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;

public interface XcapDiffSubscriptionControlSbbLocalObject extends XDMClientControlParentSbbLocalObject {

	public void setSubscriptionsMap(Map rules);
	public Map getSubscriptionsMap();
	
	public HeaderFactory getHeaderFactory();
	
	public XDMClientControlSbbLocalObject getXDMChildSbb();
	
	public void newSubscriptionAuthorization(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires, int responseCode);
	
	public DataSourceSbbInterface getDataSourceSbbInterface();
	
	public void notifySubscriber(SubscriptionKey key, Object content,
			ContentTypeHeader contentTypeHeader);
	
	public Unmarshaller getUnmarshaller();
}

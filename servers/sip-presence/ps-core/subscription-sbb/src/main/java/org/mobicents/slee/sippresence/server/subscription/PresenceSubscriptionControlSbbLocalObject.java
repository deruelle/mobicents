package org.mobicents.slee.sippresence.server.subscription;

import java.util.Map;

import javax.sip.RequestEvent;
import javax.sip.header.HeaderFactory;
import javax.xml.bind.Unmarshaller;

import org.mobicents.slee.sipevent.server.publication.PublicationControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.SubscriptionControlSbbLocalObject;
import org.mobicents.slee.sippresence.server.subscription.rules.PublishedSphereSource;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;

public interface PresenceSubscriptionControlSbbLocalObject extends XDMClientControlParentSbbLocalObject, SubscriptionControlSbbLocalObject, PublishedSphereSource {

	public void setCombinedRules(Map rules);
	public Map getCombinedRules();
	
	public void authorizationChanged(String subscriber, String notifier,
			String eventPackage, int authorizationCode);
	
	public void newSubscriptionAuthorization(RequestEvent event,
			String subscriber, String notifier, String eventPackage,
			String eventId, int expires, int responseCode);
	
	public HeaderFactory getHeaderFactory();
	
	public PublicationControlSbbLocalObject getPublicationChildSbb();
	
	public XDMClientControlSbbLocalObject getXDMChildSbb();
	
	public Unmarshaller getUnmarshaller();
}

package org.mobicents.slee.sippresence.server.subscription;

import java.util.Map;

import javax.sip.header.HeaderFactory;
import javax.slee.CreateException;
import javax.slee.SLEEException;
import javax.slee.TransactionRequiredLocalException;
import javax.xml.bind.Unmarshaller;

import org.mobicents.slee.sipevent.server.publication.PublicationControlSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.ImplementedSubscriptionControlSbbLocalObject;
import org.mobicents.slee.sippresence.server.subscription.rules.PublishedSphereSource;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;

/**
 * Extending the mandatory interfaces with methods needed by {@link PresenceSubscriptionControl}
 * @author martins
 *
 */
public interface PresenceSubscriptionControlSbbLocalObject extends
		XDMClientControlParentSbbLocalObject,
		ImplementedSubscriptionControlSbbLocalObject, PublishedSphereSource {

	public Map getCombinedRules();

	public void setCombinedRules(Map combinedRules);

	public ImplementedSubscriptionControlParentSbbLocalObject getParentSbbCMP();

	public XDMClientControlSbbLocalObject getXDMClientControlSbb()
			throws TransactionRequiredLocalException, SLEEException,
			CreateException;

	public PublicationControlSbbLocalObject getPublicationChildSbb()
			throws TransactionRequiredLocalException, SLEEException,
			CreateException;
	
	public HeaderFactory getHeaderFactory();
	
	public Unmarshaller getUnmarshaller();
}

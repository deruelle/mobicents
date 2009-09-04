/**
 * Start time:10:06:29 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.message;

import org.mobicents.ss7.isup.message.parameter.*;
import org.mobicents.ss7.isup.message.parameter.accessTransport.AccessTransport;

/**
 * Start time:10:06:29 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ReleaseMessage extends ISUPMessage {
	public CauseIndicators getCauseIndicators();

	public void setCauseIndicators(CauseIndicators v);

	public RedirectionInformation getRedirectionInformation();

	public void setRedirectionInformation(RedirectionInformation v);

	public RedirectionNumber getRedirectionNumber();

	public void setRedirectionNumber(RedirectionNumber v);

	public AccessTransport getAccessTransport();

	public void setAccessTransport(AccessTransport v);

	public SignalingPointCode getSignalingPointCode();

	public void setSignalingPointCode(SignalingPointCode v);

	public UserToUserInformation getU2UInformation();

	public void setU2UInformation(UserToUserInformation v);

	public AutomaticCongestionLevel getAutomaticCongestionLevel();

	public void setAutomaticCongestionLevel(AutomaticCongestionLevel v);

	public NetworkSpecificFacility getNetworkSpecificFacility();

	public void setNetworkSpecificFacility(NetworkSpecificFacility v);

	public AccessDeliveryInformation getAccessDeliveryInformation();

	public void setAccessDeliveryInformation(AccessDeliveryInformation v);

	public ParameterCompatibilityInformation getParameterCompatibilityInformation();

	public void setParameterCompatibilityInformation(ParameterCompatibilityInformation v);

	public UserToUserIndicators getU2UIndicators();

	public void setU2UIndicators(UserToUserIndicators v);

	public DisplayInformation getDisplayInformation();

	public void setDisplayInformation(DisplayInformation v);

	public RemoteOperations getRemoteOperations();

	public void setRemoteOperations(RemoteOperations v);

	public HTRInformation getHTRInformation();

	public void setHTRInformation(HTRInformation v);

	public RedirectCounter getRedirectCounter();

	public void setRedirectCounter(RedirectCounter v);

	public RedirectBackwardInformation getRedirectBackwardInformation();

	public void setRedirectBackwardInformation(RedirectBackwardInformation v);
}

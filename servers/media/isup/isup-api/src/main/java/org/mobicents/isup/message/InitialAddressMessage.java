package org.mobicents.isup.messages;

import org.mobicents.isup.parameters.CCSS;
import org.mobicents.isup.parameters.CallReference;
import org.mobicents.isup.parameters.CalledPartyNumber;
import org.mobicents.isup.parameters.CallingPartyCategory;
import org.mobicents.isup.parameters.CallingPartyNumber;
import org.mobicents.isup.parameters.ClosedUserGroupInterlockCode;
import org.mobicents.isup.parameters.ConnectionRequest;
import org.mobicents.isup.parameters.ForwardCallIndicators;
import org.mobicents.isup.parameters.ForwardGVNS;
import org.mobicents.isup.parameters.GenericDigits;
import org.mobicents.isup.parameters.GenericNotificationIndicator;
import org.mobicents.isup.parameters.GenericNumber;
import org.mobicents.isup.parameters.GenericReference;
import org.mobicents.isup.parameters.LocationNumber;
import org.mobicents.isup.parameters.MLPPPrecedence;
import org.mobicents.isup.parameters.NatureOfConnectionIndicators;
import org.mobicents.isup.parameters.NetworkManagementControls;
import org.mobicents.isup.parameters.NetworkSpecificFacility;
import org.mobicents.isup.parameters.OptionalForwardCallIndicators;
import org.mobicents.isup.parameters.OriginalCalledNumber;
import org.mobicents.isup.parameters.OriginatingISCPointCode;
import org.mobicents.isup.parameters.ParameterCompatibilityInformation;
import org.mobicents.isup.parameters.PropagationDelayCounter;
import org.mobicents.isup.parameters.RedirectingNumber;
import org.mobicents.isup.parameters.RedirectionInformation;
import org.mobicents.isup.parameters.RemoteOperations;
import org.mobicents.isup.parameters.ServiceActivation;
import org.mobicents.isup.parameters.TransimissionMediumRequierementPrime;
import org.mobicents.isup.parameters.TransitNetworkSelection;
import org.mobicents.isup.parameters.TransmissionMediumRequirement;
import org.mobicents.isup.parameters.UserServiceInformation;
import org.mobicents.isup.parameters.UserServiceInformationPrime;
import org.mobicents.isup.parameters.UserTeleserviceInformation;
import org.mobicents.isup.parameters.UserToUserIndicators;
import org.mobicents.isup.parameters.UserToUserInformation;
import org.mobicents.isup.parameters.accessTransport.AccessTransport;

/**
 * Start time:09:54:07 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface InitialAddressMessage extends ISUPMessage {
	public NatureOfConnectionIndicators getNatureOfConnectionIndicators();

	public void setNatureOfConnectionIndicators(NatureOfConnectionIndicators v);

	public ForwardCallIndicators getForwardCallIndicators();

	public void setForwardCallIndicators(ForwardCallIndicators v);

	public CallingPartyCategory getCallingPartCategory();

	public void setCallingPartCategory(CallingPartyCategory v);

	public TransmissionMediumRequirement getTransmissionMediumRequirement();

	public void setTransmissionMediumRequirement(TransmissionMediumRequirement v);

	public CalledPartyNumber getCalledPartyNumber();

	public void setCalledPartyNumber(CalledPartyNumber v);

	public TransitNetworkSelection getTransitNetworkSelection();

	public void setTransitNetworkSelection(TransitNetworkSelection v);

	public CallReference getCallReference();

	public void setCallReference(CallReference v);

	public CallingPartyNumber getCallingPartyNumber();

	public void setCallingPartyNumber(CallingPartyNumber v);

	public OptionalForwardCallIndicators getOptForwardCallIndicators();

	public void setOptForwardCallIndicators(OptionalForwardCallIndicators v);

	public RedirectingNumber getRedirectingNumber();

	public void setRedirectingNumber(RedirectingNumber v);

	public RedirectionInformation getRedirectionInformation();

	public void setRedirectionInformation(RedirectionInformation v);

	public ClosedUserGroupInterlockCode getCUserGroupInterlockCode();

	public void setCUserGroupInterlockCode(ClosedUserGroupInterlockCode v);

	public ConnectionRequest getConnectionRequest();

	public void setConnectionRequest(ConnectionRequest v);

	public OriginalCalledNumber getOriginalCalledNumber();

	public void setOriginalCalledNumber(OriginalCalledNumber v);

	public UserToUserInformation getU2UInformation();

	public void setU2UInformation(UserToUserInformation v);

	public UserServiceInformation getUserServiceInformation();

	public void setUserServiceInformation(UserServiceInformation v);

	public NetworkSpecificFacility getNetworkSpecificFacility();

	public void setNetworkSpecificFacility(NetworkSpecificFacility v);

	public GenericDigits getGenericDigits();

	public void setGenericDigits(GenericDigits v);

	public OriginatingISCPointCode getOriginatingISCPointCode();

	public void setOriginatingISCPointCode(OriginatingISCPointCode v);

	public UserTeleserviceInformation getUserTeleserviceInformation();

	public void setUserTeleserviceInformation(UserTeleserviceInformation v);

	public RemoteOperations getRemoteOperations();

	public void setRemoteOperations(RemoteOperations v);

	public ParameterCompatibilityInformation getParameterCompatibilityInformation();

	public void setParameterCompatibilityInformation(ParameterCompatibilityInformation v);

	public GenericNotificationIndicator getGenericNotificationIndicator();

	public void setGenericNotificationIndicator(GenericNotificationIndicator v);

	public ServiceActivation getServiceActivation();

	public void setServiceActivation(ServiceActivation v);

	public GenericReference getGenericReference();

	public void setGenericReference(GenericReference v);

	public MLPPPrecedence getMLPPPrecedence();

	public void setMLPPPrecedence(MLPPPrecedence v);

	public TransimissionMediumRequierementPrime getTransimissionMediumReqPrime();

	public void setTransimissionMediumReqPrime(TransimissionMediumRequierementPrime v);

	public LocationNumber getLocationNumber();

	public void setLocationNumber(LocationNumber v);

	public ForwardGVNS getForwardGVNS();

	public void setForwardGVNS(ForwardGVNS v);

	public CCSS getCCSS();

	public void setCCSS(CCSS v);

	public NetworkManagementControls getNetworkManagementControls();

	public void setNetworkManagementControls(NetworkManagementControls v);

	/**
	 * @param usip
	 */
	public void setUserServiceInformationPrime(UserServiceInformationPrime v);

	public UserServiceInformationPrime getUserServiceInformationPrime();

	/**
	 * @param pdc
	 */
	public void setPropagationDelayCounter(PropagationDelayCounter v);

	public PropagationDelayCounter getPropagationDelayCounter();

	/**
	 * @param gn
	 */
	public void setGenericNumber(GenericNumber v);

	public GenericNumber getGenericNumber();

	/**
	 * @param utui
	 */
	public void setU2UIndicators(UserToUserIndicators v);

	public UserToUserIndicators getU2UIndicators();

	/**
	 * @param at
	 */
	public void setAccessTransport(AccessTransport v);

	public AccessTransport getAccessTransport();
}

package org.mobicents.ss7.isup.message;

import org.mobicents.ss7.isup.message.parameter.CCSS;
import org.mobicents.ss7.isup.message.parameter.CallReference;
import org.mobicents.ss7.isup.message.parameter.CalledPartyNumber;
import org.mobicents.ss7.isup.message.parameter.CallingPartyCategory;
import org.mobicents.ss7.isup.message.parameter.CallingPartyNumber;
import org.mobicents.ss7.isup.message.parameter.ClosedUserGroupInterlockCode;
import org.mobicents.ss7.isup.message.parameter.ConnectionRequest;
import org.mobicents.ss7.isup.message.parameter.ForwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.ForwardGVNS;
import org.mobicents.ss7.isup.message.parameter.GenericDigits;
import org.mobicents.ss7.isup.message.parameter.GenericNotificationIndicator;
import org.mobicents.ss7.isup.message.parameter.GenericNumber;
import org.mobicents.ss7.isup.message.parameter.GenericReference;
import org.mobicents.ss7.isup.message.parameter.LocationNumber;
import org.mobicents.ss7.isup.message.parameter.MLPPPrecedence;
import org.mobicents.ss7.isup.message.parameter.NatureOfConnectionIndicators;
import org.mobicents.ss7.isup.message.parameter.NetworkManagementControls;
import org.mobicents.ss7.isup.message.parameter.NetworkSpecificFacility;
import org.mobicents.ss7.isup.message.parameter.OptionalForwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.OriginalCalledNumber;
import org.mobicents.ss7.isup.message.parameter.OriginatingISCPointCode;
import org.mobicents.ss7.isup.message.parameter.ParameterCompatibilityInformation;
import org.mobicents.ss7.isup.message.parameter.PropagationDelayCounter;
import org.mobicents.ss7.isup.message.parameter.RedirectingNumber;
import org.mobicents.ss7.isup.message.parameter.RedirectionInformation;
import org.mobicents.ss7.isup.message.parameter.RemoteOperations;
import org.mobicents.ss7.isup.message.parameter.ServiceActivation;
import org.mobicents.ss7.isup.message.parameter.TransimissionMediumRequierementPrime;
import org.mobicents.ss7.isup.message.parameter.TransitNetworkSelection;
import org.mobicents.ss7.isup.message.parameter.TransmissionMediumRequirement;
import org.mobicents.ss7.isup.message.parameter.UserServiceInformation;
import org.mobicents.ss7.isup.message.parameter.UserServiceInformationPrime;
import org.mobicents.ss7.isup.message.parameter.UserTeleserviceInformation;
import org.mobicents.ss7.isup.message.parameter.UserToUserIndicators;
import org.mobicents.ss7.isup.message.parameter.UserToUserInformation;
import org.mobicents.ss7.isup.message.parameter.accessTransport.AccessTransport;

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

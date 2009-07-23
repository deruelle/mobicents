package org.mobicents.isup.messages;

import org.mobicents.isup.parameters.AccessDeliveryInformation;
import org.mobicents.isup.parameters.ApplicationTransportParameter;
import org.mobicents.isup.parameters.BackwardCallIndicators;
import org.mobicents.isup.parameters.CCNRPossibleIndicator;
import org.mobicents.isup.parameters.CallDiversionInformation;
import org.mobicents.isup.parameters.CallReference;
import org.mobicents.isup.parameters.CauseIndicators;
import org.mobicents.isup.parameters.ConferenceTreatmentIndicators;
import org.mobicents.isup.parameters.EchoControlInformation;
import org.mobicents.isup.parameters.GenericNotificationIndicator;
import org.mobicents.isup.parameters.HTRInformation;
import org.mobicents.isup.parameters.NetworkSpecificFacility;
import org.mobicents.isup.parameters.OptionalBakwardCallIndicators;
import org.mobicents.isup.parameters.ParameterCompatibilityInformation;
import org.mobicents.isup.parameters.PivotRoutingBackwardInformation;
import org.mobicents.isup.parameters.RedirectStatus;
import org.mobicents.isup.parameters.RedirectionNumber;
import org.mobicents.isup.parameters.RedirectionNumberRestriction;
import org.mobicents.isup.parameters.RemoteOperations;
import org.mobicents.isup.parameters.ServiceActivation;
import org.mobicents.isup.parameters.TransmissionMediumUsed;
import org.mobicents.isup.parameters.UIDActionIndicators;
import org.mobicents.isup.parameters.UserToUserIndicators;
import org.mobicents.isup.parameters.UserToUserInformation;
import org.mobicents.isup.parameters.accessTransport.AccessTransport;

/**
 * Start time:09:41:44 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface AddressCompleteMessage extends ISUPMessage {

	public void setBackwardCallIndicators(BackwardCallIndicators indicators);

	public BackwardCallIndicators getBackwardCallIndicators();

	public void setOptionalBakwardCallIndicators(OptionalBakwardCallIndicators value);

	public OptionalBakwardCallIndicators getOptionalBakwardCallIndicators();

	public void setCallReference(CallReference value);

	public CallReference getCallReference();

	public void setCauseIndicators(CauseIndicators value);

	public CauseIndicators getCauseIndicators();

	public void setUserToUserIndicators(UserToUserIndicators value);

	public UserToUserIndicators getUserToUserIndicators();

	public void setUserToUserInformation(UserToUserInformation value);

	public UserToUserInformation getUserToUserInformation();

	public void setAccessTransport(AccessTransport value);

	public AccessTransport getAccessTransport();

	public void setGenericNotificationIndicator(GenericNotificationIndicator value);

	public GenericNotificationIndicator getGenericNotificationIndicator();

	public void setTransmissionMediumUsed(TransmissionMediumUsed value);

	public TransmissionMediumUsed getTransmissionMediumUsed();

	public void setEchoControlInformation(EchoControlInformation value);

	public EchoControlInformation getEchoControlInformation();

	public void setAccessDeliveryInformation(AccessDeliveryInformation value);

	public AccessDeliveryInformation getAccessDeliveryInformation();

	public void setRedirectionNumber(RedirectionNumber value);

	public RedirectionNumber getRedirectionNumber();

	public void setParameterCompatibilityInformation(ParameterCompatibilityInformation value);

	public ParameterCompatibilityInformation getParameterCompatibilityInformation();

	public void setCallDiversionInformation(CallDiversionInformation value);

	public CallDiversionInformation getCallDiversionInformation();

	public void setNetworkSpecificFacility(NetworkSpecificFacility value);

	public NetworkSpecificFacility getNetworkSpecificFacility();

	public void setRemoteOperations(RemoteOperations value);

	public RemoteOperations getRemoteOperations();

	public void setServiceActivation(ServiceActivation value);

	public RedirectionNumberRestriction getRedirectionNumberRestriction();

	public void setRedirectionNumberRestriction(RedirectionNumberRestriction value);

	public ServiceActivation getServiceActivation();

	public void setConferenceTreatmentIndicators(ConferenceTreatmentIndicators value);

	public ConferenceTreatmentIndicators getConferenceTreatmentIndicators();

	public void setUIDActionIndicators(UIDActionIndicators value);

	public UIDActionIndicators getUIDActionIndicators();

	public void setApplicationTransportParameter(ApplicationTransportParameter value);

	public ApplicationTransportParameter getApplicationTransportParameter();

	public void setCCNRPossibleIndicator(CCNRPossibleIndicator value);

	public CCNRPossibleIndicator getCCNRPossibleIndicator();

	public void setHTRInformation(HTRInformation value);

	public HTRInformation getHTRInformation();

	public void setPivotRoutingBackwardInformation(PivotRoutingBackwardInformation value);

	public PivotRoutingBackwardInformation getPivotRoutingBackwardInformation();

	public void setRedirectStatus(RedirectStatus value);

	public RedirectStatus getRedirectStatus();

}

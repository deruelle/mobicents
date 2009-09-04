package org.mobicents.ss7.isup.message;

import org.mobicents.ss7.isup.message.parameter.AccessDeliveryInformation;
import org.mobicents.ss7.isup.message.parameter.ApplicationTransportParameter;
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.CCNRPossibleIndicator;
import org.mobicents.ss7.isup.message.parameter.CallDiversionInformation;
import org.mobicents.ss7.isup.message.parameter.CallReference;
import org.mobicents.ss7.isup.message.parameter.CauseIndicators;
import org.mobicents.ss7.isup.message.parameter.ConferenceTreatmentIndicators;
import org.mobicents.ss7.isup.message.parameter.EchoControlInformation;
import org.mobicents.ss7.isup.message.parameter.GenericNotificationIndicator;
import org.mobicents.ss7.isup.message.parameter.HTRInformation;
import org.mobicents.ss7.isup.message.parameter.NetworkSpecificFacility;
import org.mobicents.ss7.isup.message.parameter.OptionalBakwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.ParameterCompatibilityInformation;
import org.mobicents.ss7.isup.message.parameter.PivotRoutingBackwardInformation;
import org.mobicents.ss7.isup.message.parameter.RedirectStatus;
import org.mobicents.ss7.isup.message.parameter.RedirectionNumber;
import org.mobicents.ss7.isup.message.parameter.RedirectionNumberRestriction;
import org.mobicents.ss7.isup.message.parameter.RemoteOperations;
import org.mobicents.ss7.isup.message.parameter.ServiceActivation;
import org.mobicents.ss7.isup.message.parameter.TransmissionMediumUsed;
import org.mobicents.ss7.isup.message.parameter.UIDActionIndicators;
import org.mobicents.ss7.isup.message.parameter.UserToUserIndicators;
import org.mobicents.ss7.isup.message.parameter.UserToUserInformation;
import org.mobicents.ss7.isup.message.parameter.accessTransport.AccessTransport;

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

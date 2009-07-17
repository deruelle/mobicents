/**
 * Start time:14:30:39 2009-04-20<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages;

import java.util.TreeMap;

import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.AccessDeliveryInformation;
import org.mobicents.isup.parameters.ApplicationTransportParameter;
import org.mobicents.isup.parameters.BackwardCallIndicators;
import org.mobicents.isup.parameters.CCNRPossibleIndicator;
import org.mobicents.isup.parameters.CallDiversionInformation;
import org.mobicents.isup.parameters.CallReference;
import org.mobicents.isup.parameters.CauseIndicators;
import org.mobicents.isup.parameters.ConferenceTreatmentIndicators;
import org.mobicents.isup.parameters.EchoControlInformation;
import org.mobicents.isup.parameters.EndOfOptionalParameters;
import org.mobicents.isup.parameters.GenericNotificationIndicator;
import org.mobicents.isup.parameters.HTRInformation;
import org.mobicents.isup.parameters.ISUPParameter;
import org.mobicents.isup.parameters.MessageType;
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
 * Start time:14:30:39 2009-04-20<br>
 * Project: mobicents-isup-stack<br>
 * See Table 21/Q.763
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class AddressCompleteMessage extends ISUPMessage {

	public static final int _MESSAGE_CODE = 0x06;
	public static final MessageType _MESSAGE_TYPE = new MessageType(_MESSAGE_CODE);

	protected static final int _INDEX_F_MessageType = 0;
	protected static final int _INDEX_F_BackwardCallIndicators = 1;
	// FIXME: those can be sent in any order, but we prefer this way, its faster
	// to access by index than by hash ?
	protected static final int _INDEX_O_OptionalBakwardCallIndicators = 0;
	protected static final int _INDEX_O_CallReference = 1;
	protected static final int _INDEX_O_CauseIndicators = 2;
	protected static final int _INDEX_O_UserToUserIndicators = 3;
	protected static final int _INDEX_O_UserToUserInformation = 4;
	protected static final int _INDEX_O_AccessTransport = 5;
	// FIXME: There can be more of those.
	protected static final int _INDEX_O_GenericNotificationIndicator = 6;
	protected static final int _INDEX_O_TransmissionMediumUsed = 7;
	protected static final int _INDEX_O_EchoControlInformation = 8;
	protected static final int _INDEX_O_AccessDeliveryInformation = 9;
	protected static final int _INDEX_O_RedirectionNumber = 10;
	protected static final int _INDEX_O_ParameterCompatibilityInformation = 11;
	protected static final int _INDEX_O_CallDiversionInformation = 12;
	protected static final int _INDEX_O_NetworkSpecificFacility = 13;
	protected static final int _INDEX_O_RemoteOperations = 14;
	protected static final int _INDEX_O_ServiceActivation = 15;
	protected static final int _INDEX_O_RedirectionNumberRestriction = 16;
	protected static final int _INDEX_O_ConferenceTreatmentIndicators = 17;
	protected static final int _INDEX_O_UIDActionIndicators = 18;
	protected static final int _INDEX_O_ApplicationTransportParameter = 19;
	protected static final int _INDEX_O_CCNRPossibleIndicator = 20;
	protected static final int _INDEX_O_HTRInformation = 21;
	protected static final int _INDEX_O_PivotRoutingBackwardInformation = 22;
	protected static final int _INDEX_O_RedirectStatus = 23;
	protected static final int _INDEX_O_EndOfOptionalParameters = 24;

	public AddressCompleteMessage(Object source, byte[] b) throws  ParameterRangeInvalidException{
		super(source);
		super.f_Parameters = new TreeMap<Integer,ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer,ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer,ISUPParameter>();
	
		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());
		decodeElement(b);
		super.o_Parameters.put(_INDEX_O_EndOfOptionalParameters,_END_OF_OPTIONAL_PARAMETERS);

	}

	public AddressCompleteMessage() {
		
		super.f_Parameters = new TreeMap<Integer,ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer,ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer,ISUPParameter>();
	
		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());

	}

	@Override
	public boolean hasAllMandatoryParameters() {
		if (super.f_Parameters.get(_INDEX_F_MessageType) == null || super.f_Parameters.get(_INDEX_F_MessageType).getCode() != MessageType._PARAMETER_CODE) {
			return false;
		}

		if (super.f_Parameters.get(_INDEX_F_BackwardCallIndicators) == null || super.f_Parameters.get(_INDEX_F_BackwardCallIndicators).getCode() != BackwardCallIndicators._PARAMETER_CODE) {
			return false;
		}

		return true;
	}

	@Override
	public MessageType getMessageType() {
		return _MESSAGE_TYPE;
	}

	public void setBackwardCallIndicators(BackwardCallIndicators indicators) {
		super.f_Parameters.put(_INDEX_F_BackwardCallIndicators, indicators);
	}

	public BackwardCallIndicators getBackwardCallIndicators() {
		return (BackwardCallIndicators) super.f_Parameters.get(_INDEX_F_BackwardCallIndicators);
	}

	public void setOptionalBakwardCallIndicators(OptionalBakwardCallIndicators value) {
		super.o_Parameters.put(_INDEX_O_OptionalBakwardCallIndicators, value);
		
	}

	public OptionalBakwardCallIndicators getOptionalBakwardCallIndicators() {
		return (OptionalBakwardCallIndicators) super.o_Parameters.get(_INDEX_O_OptionalBakwardCallIndicators);
	}

	public void setCallReference(CallReference value) {
		super.o_Parameters.put(_INDEX_O_CallReference, value);
	}

	public CallReference getCallReference() {
		return (CallReference) super.o_Parameters.get(_INDEX_O_CallReference);
	}

	public void setCauseIndicators(CauseIndicators value) {
		super.o_Parameters.put(_INDEX_O_CauseIndicators, value);
		
	}

	public CauseIndicators getCauseIndicators() {
		return (CauseIndicators) super.o_Parameters.get(_INDEX_O_CauseIndicators);
	}

	public void setUserToUserIndicators(UserToUserIndicators value) {
		super.o_Parameters.put(_INDEX_O_UserToUserIndicators, value);
	}

	public UserToUserIndicators getUserToUserIndicators() {
		return (UserToUserIndicators) super.o_Parameters.get(_INDEX_O_UserToUserIndicators);
	}

	public void setUserToUserInformation(UserToUserInformation value) {
		super.o_Parameters.put(_INDEX_O_UserToUserInformation, value);
	}

	public UserToUserInformation getUserToUserInformation() {
		return (UserToUserInformation) super.o_Parameters.get(_INDEX_O_UserToUserInformation);
	}

	public void setAccessTransport(AccessTransport value) {
		super.o_Parameters.put(_INDEX_O_AccessTransport, value);
	}

	public AccessTransport getAccessTransport() {
		return (AccessTransport) super.o_Parameters.get(_INDEX_O_AccessTransport);
	}

	public void setGenericNotificationIndicator(GenericNotificationIndicator value) {
		super.o_Parameters.put(_INDEX_O_GenericNotificationIndicator, value);
	}

	public GenericNotificationIndicator getGenericNotificationIndicator() {
		return (GenericNotificationIndicator) super.o_Parameters.get(_INDEX_O_GenericNotificationIndicator);
	}

	public void setTransmissionMediumUsed(TransmissionMediumUsed value) {
		super.o_Parameters.put(_INDEX_O_TransmissionMediumUsed, value);
	}

	public TransmissionMediumUsed getTransmissionMediumUsed() {
		return (TransmissionMediumUsed) super.o_Parameters.get(_INDEX_O_TransmissionMediumUsed);
	}

	public void setEchoControlInformation(EchoControlInformation value) {
		super.o_Parameters.put(_INDEX_O_EchoControlInformation, value);
	}

	public EchoControlInformation getEchoControlInformation() {
		return (EchoControlInformation) super.o_Parameters.get(_INDEX_O_EchoControlInformation);
	}

	public void setAccessDeliveryInformation(AccessDeliveryInformation value) {
		super.o_Parameters.put(_INDEX_O_AccessDeliveryInformation, value);
	}

	public AccessDeliveryInformation getAccessDeliveryInformation() {
		return (AccessDeliveryInformation) super.o_Parameters.get(_INDEX_O_AccessDeliveryInformation);
	}

	public void setRedirectionNumber(RedirectionNumber value) {
		super.o_Parameters.put(_INDEX_O_RedirectionNumber, value);
	}

	public RedirectionNumber getRedirectionNumber() {
		return (RedirectionNumber) super.o_Parameters.get(_INDEX_O_RedirectionNumber);
	}

	public void setParameterCompatibilityInformation(ParameterCompatibilityInformation value) {
		super.o_Parameters.put(_INDEX_O_ParameterCompatibilityInformation, value);
	}

	public ParameterCompatibilityInformation getParameterCompatibilityInformation() {
		return (ParameterCompatibilityInformation) super.o_Parameters.get(_INDEX_O_ParameterCompatibilityInformation);
	}

	public void setCallDiversionInformation(CallDiversionInformation value) {
		super.o_Parameters.put(_INDEX_O_CallDiversionInformation, value);
	}

	public CallDiversionInformation getCallDiversionInformation() {
		return (CallDiversionInformation) super.o_Parameters.get(_INDEX_O_CallDiversionInformation);
	}

	public void setNetworkSpecificFacility(NetworkSpecificFacility value) {
		super.o_Parameters.put(_INDEX_O_NetworkSpecificFacility, value);
	}

	public NetworkSpecificFacility getNetworkSpecificFacility() {
		return (NetworkSpecificFacility) super.o_Parameters.get(_INDEX_O_NetworkSpecificFacility);
	}

	public void setRemoteOperations(RemoteOperations value) {
		super.o_Parameters.put(_INDEX_O_RemoteOperations, value);
	}

	public RemoteOperations getRemoteOperations() {
		return (RemoteOperations) super.o_Parameters.get(_INDEX_O_RemoteOperations);
	}

	public void setServiceActivation(ServiceActivation value) {
		super.o_Parameters.put(_INDEX_O_ServiceActivation, value);
	}

	public RedirectionNumberRestriction getRedirectionNumberRestriction() {
		return (RedirectionNumberRestriction) super.o_Parameters.get(_INDEX_O_ServiceActivation);
	}

	public void setRedirectionNumberRestriction(RedirectionNumberRestriction value) {
		super.o_Parameters.put(_INDEX_O_RedirectionNumberRestriction, value);
	}

	public ServiceActivation getServiceActivation() {
		return (ServiceActivation) super.o_Parameters.get(_INDEX_O_RedirectionNumberRestriction);
	}

	public void setConferenceTreatmentIndicators(ConferenceTreatmentIndicators value) {
		super.o_Parameters.put(_INDEX_O_ConferenceTreatmentIndicators, value);
	}

	public ConferenceTreatmentIndicators getConferenceTreatmentIndicators() {
		return (ConferenceTreatmentIndicators) super.o_Parameters.get(_INDEX_O_ConferenceTreatmentIndicators);
	}

	public void setUIDActionIndicators(UIDActionIndicators value) {
		super.o_Parameters.put(_INDEX_O_UIDActionIndicators, value);
	}

	public UIDActionIndicators getUIDActionIndicators() {
		return (UIDActionIndicators) super.o_Parameters.get(_INDEX_O_UIDActionIndicators);
	}

	public void setApplicationTransportParameter(ApplicationTransportParameter value) {
		super.o_Parameters.put(_INDEX_O_ApplicationTransportParameter, value);
	}

	public ApplicationTransportParameter getApplicationTransportParameter() {
		return (ApplicationTransportParameter) super.o_Parameters.get(_INDEX_O_ApplicationTransportParameter);
	}

	public void setCCNRPossibleIndicator(CCNRPossibleIndicator value) {
		super.o_Parameters.put(_INDEX_O_CCNRPossibleIndicator, value);
	}

	public CCNRPossibleIndicator getCCNRPossibleIndicator() {
		return (CCNRPossibleIndicator) super.o_Parameters.get(_INDEX_O_CCNRPossibleIndicator);
	}

	public void setHTRInformation(HTRInformation value) {
		super.o_Parameters.put(_INDEX_O_HTRInformation, value);
	}

	public HTRInformation getHTRInformation() {
		return (HTRInformation) super.o_Parameters.get(_INDEX_O_HTRInformation);
	}

	public void setPivotRoutingBackwardInformation(PivotRoutingBackwardInformation value) {
		super.o_Parameters.put(_INDEX_O_PivotRoutingBackwardInformation, value);
	}

	public PivotRoutingBackwardInformation getPivotRoutingBackwardInformation() {
		return (PivotRoutingBackwardInformation) super.o_Parameters.get(_INDEX_O_PivotRoutingBackwardInformation);
	}

	public void setRedirectStatus(RedirectStatus value) {
		super.o_Parameters.put(_INDEX_O_RedirectStatus, value);
	}

	public RedirectStatus getRedirectStatus() {
		return (RedirectStatus) super.o_Parameters.get(_INDEX_O_RedirectStatus);
	}

	@Override
	protected int decodeMandatoryParameters(byte[] b, int index) throws ParameterRangeInvalidException {
		int localIndex = index;
		if (b.length - index > 1) {
			
			// Message Type
			if(b[index]!=this._MESSAGE_CODE)
			{
				throw new ParameterRangeInvalidException("Message code is not: "+this._MESSAGE_CODE);
			}
			index++;
			// this.circuitIdentificationCode = b[index++];
			try{
				byte[] backwardCallIndicator = new byte[2];
				backwardCallIndicator[0] = b[index++];
				backwardCallIndicator[1] = b[index++];
				BackwardCallIndicators bci = new BackwardCallIndicators(backwardCallIndicator);
				this.setBackwardCallIndicators(bci);
			}catch(Exception e)
			{
				//AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse BackwardCallIndicators due to: ",e);
			}
			
			//return 3;
			return index-localIndex;
		} else {
			throw new IllegalArgumentException("byte[] must have atleast two octets");
		}

	}

	@Override
	protected int getNumberOfMandatoryVariableLengthParameters() {
		
		return 0;
	}
	protected void decodeMandatoryVariableBody(byte[] parameterBody, int parameterIndex) throws ParameterRangeInvalidException
	{
		//we dont have those.
	}
	
	protected void decodeOptionalBody(byte[] parameterBody, byte parameterCode) throws ParameterRangeInvalidException {


		switch ((int) parameterCode) {
		case OptionalBakwardCallIndicators._PARAMETER_CODE:
			OptionalBakwardCallIndicators obi = new OptionalBakwardCallIndicators(parameterBody);
			this.setOptionalBakwardCallIndicators(obi);
			break;
		case CallReference._PARAMETER_CODE:
			CallReference cr = new CallReference(parameterBody);
			this.setCallReference(cr);
			break;
		case CauseIndicators._PARAMETER_CODE:
			CauseIndicators ci = new CauseIndicators(parameterBody);
			this.setCauseIndicators(ci);
			break;
		case UserToUserIndicators._PARAMETER_CODE:
			UserToUserIndicators utsi = new UserToUserIndicators(parameterBody);
			this.setUserToUserIndicators(utsi);
			break;
		case UserToUserInformation._PARAMETER_CODE:
			UserToUserInformation utsi2 = new UserToUserInformation(parameterBody);
			this.setUserToUserInformation(utsi2);
			break;
		case AccessTransport._PARAMETER_CODE:
			AccessTransport at = new AccessTransport(parameterBody);
			this.setAccessTransport(at);
			break;
		// FIXME: There can be more of those.
		case GenericNotificationIndicator._PARAMETER_CODE:
			GenericNotificationIndicator gni = new GenericNotificationIndicator(parameterBody);
			this.setGenericNotificationIndicator(gni);
			break;
		case TransmissionMediumUsed._PARAMETER_CODE:
			TransmissionMediumUsed tmu = new TransmissionMediumUsed(parameterBody);
			this.setTransmissionMediumUsed(tmu);
			break;
		case EchoControlInformation._PARAMETER_CODE:
			EchoControlInformation eci = new EchoControlInformation(parameterBody);
			this.setEchoControlInformation(eci);
			break;
		case AccessDeliveryInformation._PARAMETER_CODE:
			AccessDeliveryInformation adi = new AccessDeliveryInformation(parameterBody);
			this.setAccessDeliveryInformation(adi);
			break;
		case RedirectionNumber._PARAMETER_CODE:
			RedirectionNumber rn = new RedirectionNumber(parameterBody);
			this.setRedirectionNumber(rn);
			break;
		case ParameterCompatibilityInformation._PARAMETER_CODE:
			ParameterCompatibilityInformation pci = new ParameterCompatibilityInformation(parameterBody);
			this.setParameterCompatibilityInformation(pci);
			break;
		case CallDiversionInformation._PARAMETER_CODE:
			CallDiversionInformation cdi = new CallDiversionInformation(parameterBody);
			this.setCallDiversionInformation(cdi);
			break;
		case NetworkSpecificFacility._PARAMETER_CODE:
			NetworkSpecificFacility nsf = new NetworkSpecificFacility(parameterBody);
			this.setNetworkSpecificFacility(nsf);
			break;
		case RemoteOperations._PARAMETER_CODE:
			RemoteOperations ro = new RemoteOperations(parameterBody);
			this.setRemoteOperations(ro);
			break;
		case ServiceActivation._PARAMETER_CODE:
			ServiceActivation sa = new ServiceActivation(parameterBody);
			this.setServiceActivation(sa);
			break;
		case RedirectionNumberRestriction._PARAMETER_CODE:
			RedirectionNumberRestriction rnr = new RedirectionNumberRestriction(parameterBody);
			this.setRedirectionNumberRestriction(rnr);
			break;
		case ConferenceTreatmentIndicators._PARAMETER_CODE:
			ConferenceTreatmentIndicators cti = new ConferenceTreatmentIndicators(parameterBody);
			this.setConferenceTreatmentIndicators(cti);
			break;
		case UIDActionIndicators._PARAMETER_CODE:
			UIDActionIndicators uidAI = new UIDActionIndicators(parameterBody);
			this.setUIDActionIndicators(uidAI);
			break;
		case ApplicationTransportParameter._PARAMETER_CODE:
			ApplicationTransportParameter atp = new ApplicationTransportParameter(parameterBody);
			this.setApplicationTransportParameter(atp);
			break;
		case CCNRPossibleIndicator._PARAMETER_CODE:
			CCNRPossibleIndicator ccnrPI = new CCNRPossibleIndicator(parameterBody);
			this.setCCNRPossibleIndicator(ccnrPI);
			break;
		case HTRInformation._PARAMETER_CODE:
			HTRInformation htr = new HTRInformation(parameterBody);
			this.setHTRInformation(htr);
			break;
		case PivotRoutingBackwardInformation._PARAMETER_CODE:
			PivotRoutingBackwardInformation pivot = new PivotRoutingBackwardInformation(parameterBody);
			this.setPivotRoutingBackwardInformation(pivot);
			break;
		case RedirectStatus._PARAMETER_CODE:
			RedirectStatus rs = new RedirectStatus(parameterBody);
			this.setRedirectStatus(rs);
			break;
		case EndOfOptionalParameters._PARAMETER_CODE:
			// we add this by default
			break;

		default:
			throw new IllegalArgumentException("Unrecognized parameter code for optional part: "+parameterCode);
		}

	}

	/* (non-Javadoc)
	 * @see org.mobicents.isup.messages.ISUPMessage#getNumberOfMandatoryVariableLengthParameters()
	 */
	
}

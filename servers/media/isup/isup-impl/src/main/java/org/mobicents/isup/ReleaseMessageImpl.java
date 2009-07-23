/**
 * Start time:21:00:56 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.isup;

import java.util.HashSet;
import java.util.TreeMap;

import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.messages.ReleaseMessage;
import org.mobicents.isup.parameters.AccessDeliveryInformation;
import org.mobicents.isup.parameters.AccessDeliveryInformationImpl;
import org.mobicents.isup.parameters.AutomaticCongestionLevel;
import org.mobicents.isup.parameters.AutomaticCongestionLevelImpl;
import org.mobicents.isup.parameters.CCSSImpl;
import org.mobicents.isup.parameters.CallReferenceImpl;
import org.mobicents.isup.parameters.CalledPartyNumberImpl;
import org.mobicents.isup.parameters.CallingPartyCategoryImpl;
import org.mobicents.isup.parameters.CallingPartyNumberImpl;
import org.mobicents.isup.parameters.CauseIndicators;
import org.mobicents.isup.parameters.CauseIndicatorsImpl;
import org.mobicents.isup.parameters.ClosedUserGroupInterlockCodeImpl;
import org.mobicents.isup.parameters.ConnectionRequestImpl;
import org.mobicents.isup.parameters.DisplayInformation;
import org.mobicents.isup.parameters.DisplayInformationImpl;
import org.mobicents.isup.parameters.ForwardCallIndicatorsImpl;
import org.mobicents.isup.parameters.ForwardGVNSImpl;
import org.mobicents.isup.parameters.GenericDigitsImpl;
import org.mobicents.isup.parameters.GenericNotificationIndicatorImpl;
import org.mobicents.isup.parameters.GenericNumberImpl;
import org.mobicents.isup.parameters.GenericReferenceImpl;
import org.mobicents.isup.parameters.HTRInformation;
import org.mobicents.isup.parameters.HTRInformationImpl;
import org.mobicents.isup.parameters.ISUPParameter;
import org.mobicents.isup.parameters.LocationNumberImpl;
import org.mobicents.isup.parameters.MLPPPrecedenceImpl;
import org.mobicents.isup.parameters.MessageType;
import org.mobicents.isup.parameters.MessageTypeImpl;
import org.mobicents.isup.parameters.NatureOfConnectionIndicatorsImpl;
import org.mobicents.isup.parameters.NetworkManagementControlsImpl;
import org.mobicents.isup.parameters.NetworkSpecificFacility;
import org.mobicents.isup.parameters.NetworkSpecificFacilityImpl;
import org.mobicents.isup.parameters.OptionalForwardCallIndicatorsImpl;
import org.mobicents.isup.parameters.OriginalCalledNumberImpl;
import org.mobicents.isup.parameters.OriginatingISCPointCodeImpl;
import org.mobicents.isup.parameters.ParameterCompatibilityInformation;
import org.mobicents.isup.parameters.ParameterCompatibilityInformationImpl;
import org.mobicents.isup.parameters.PropagationDelayCounterImpl;
import org.mobicents.isup.parameters.RedirectBackwardInformation;
import org.mobicents.isup.parameters.RedirectBackwardInformationImpl;
import org.mobicents.isup.parameters.RedirectCounter;
import org.mobicents.isup.parameters.RedirectCounterImpl;
import org.mobicents.isup.parameters.RedirectingNumberImpl;
import org.mobicents.isup.parameters.RedirectionInformation;
import org.mobicents.isup.parameters.RedirectionInformationImpl;
import org.mobicents.isup.parameters.RedirectionNumber;
import org.mobicents.isup.parameters.RedirectionNumberImpl;
import org.mobicents.isup.parameters.RemoteOperations;
import org.mobicents.isup.parameters.RemoteOperationsImpl;
import org.mobicents.isup.parameters.ServiceActivationImpl;
import org.mobicents.isup.parameters.SignalingPointCode;
import org.mobicents.isup.parameters.SignalingPointCodeImpl;
import org.mobicents.isup.parameters.TransitNetworkSelectionImpl;
import org.mobicents.isup.parameters.TransmissionMediumRequirementImpl;
import org.mobicents.isup.parameters.UserServiceInformationImpl;
import org.mobicents.isup.parameters.UserServiceInformationPrimeImpl;
import org.mobicents.isup.parameters.UserTeleserviceInformationImpl;
import org.mobicents.isup.parameters.UserToUserIndicators;
import org.mobicents.isup.parameters.UserToUserIndicatorsImpl;
import org.mobicents.isup.parameters.UserToUserInformation;
import org.mobicents.isup.parameters.UserToUserInformationImpl;
import org.mobicents.isup.parameters.accessTransport.AccessTransport;
import org.mobicents.isup.parameters.accessTransport.AccessTransportImpl;

/**
 * Start time:21:00:56 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class ReleaseMessageImpl extends ISUPMessageImpl implements ReleaseMessage{

	
	public static final MessageTypeImpl _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_REL);

	private static final int _MANDATORY_VAR_COUNT = 1;
	// mandatory fixed L
	protected static final int _INDEX_F_MessageType = 0;
	// mandatory variable L
	protected static final int _INDEX_V_CauseIndicators = 0;
	// optional
	protected static final int _INDEX_O_RedirectionInformation = 0;
	protected static final int _INDEX_O_RedirectionNumber = 1;
	protected static final int _INDEX_O_AccessTransport = 2;
	protected static final int _INDEX_O_SignalingPointCode = 3;
	protected static final int _INDEX_O_U2UInformation = 4;
	protected static final int _INDEX_O_AutomaticCongestionLevel = 5;
	protected static final int _INDEX_O_NetworkSpecificFacility = 6;
	protected static final int _INDEX_O_AccessDeliveryInformation = 7;
	protected static final int _INDEX_O_ParameterCompatibilityInformation = 8;
	protected static final int _INDEX_O_U2UIndicators = 9;
	protected static final int _INDEX_O_DisplayInformation = 10;
	protected static final int _INDEX_O_RemoteOperations = 11;
	protected static final int _INDEX_O_HTRInformation = 12;
	protected static final int _INDEX_O_RedirectCounter = 13;
	protected static final int _INDEX_O_RedirectBackwardInformation = 14;
	protected static final int _INDEX_O_EndOfOptionalParameters = 15;

	/**
	 * 
	 * @param source
	 * @throws IllegalArgumentException
	 */
	ReleaseMessageImpl(Object source, byte[] b) throws ParameterRangeInvalidException {
		super(source);

		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());
		decodeElement(b);
		super.o_Parameters.put(_INDEX_O_EndOfOptionalParameters, _END_OF_OPTIONAL_PARAMETERS);
		
		
		
		super.mandatoryVariableCodes.add(CauseIndicators._PARAMETER_CODE);
		super.mandatoryVariableCodeToIndex.put(CauseIndicators._PARAMETER_CODE, _INDEX_V_CauseIndicators);
		
		super.optionalCodes.add(RedirectionInformation._PARAMETER_CODE);
		super.optionalCodes.add(RedirectionNumber._PARAMETER_CODE);
		super.optionalCodes.add(AccessTransport._PARAMETER_CODE);
		super.optionalCodes.add(SignalingPointCode._PARAMETER_CODE);
		super.optionalCodes.add(UserToUserInformation._PARAMETER_CODE);
		super.optionalCodes.add(AutomaticCongestionLevel._PARAMETER_CODE);
		super.optionalCodes.add(NetworkSpecificFacility._PARAMETER_CODE);
		super.optionalCodes.add(AccessDeliveryInformation._PARAMETER_CODE);
		super.optionalCodes.add(ParameterCompatibilityInformation._PARAMETER_CODE);
		super.optionalCodes.add(UserToUserIndicators._PARAMETER_CODE);
		super.optionalCodes.add(DisplayInformation._PARAMETER_CODE);
		super.optionalCodes.add(RemoteOperations._PARAMETER_CODE);
		super.optionalCodes.add(HTRInformation._PARAMETER_CODE);
		super.optionalCodes.add(RedirectCounter._PARAMETER_CODE);
		super.optionalCodes.add(RedirectBackwardInformation._PARAMETER_CODE);
		
		
		super.optionalCodeToIndex.put(RedirectionInformation._PARAMETER_CODE,_INDEX_O_RedirectionInformation);
		super.optionalCodeToIndex.put(RedirectionNumber._PARAMETER_CODE,_INDEX_O_RedirectionNumber);
		super.optionalCodeToIndex.put(AccessTransport._PARAMETER_CODE,_INDEX_O_AccessTransport);
		super.optionalCodeToIndex.put(SignalingPointCode._PARAMETER_CODE,_INDEX_O_SignalingPointCode);
		super.optionalCodeToIndex.put(UserToUserInformation._PARAMETER_CODE,_INDEX_O_U2UInformation);
		super.optionalCodeToIndex.put(AutomaticCongestionLevel._PARAMETER_CODE,_INDEX_O_AutomaticCongestionLevel);
		super.optionalCodeToIndex.put(NetworkSpecificFacility._PARAMETER_CODE,_INDEX_O_NetworkSpecificFacility);
		super.optionalCodeToIndex.put(AccessDeliveryInformation._PARAMETER_CODE,_INDEX_O_AccessDeliveryInformation);
		super.optionalCodeToIndex.put(ParameterCompatibilityInformation._PARAMETER_CODE,_INDEX_O_ParameterCompatibilityInformation);
		super.optionalCodeToIndex.put(UserToUserIndicators._PARAMETER_CODE,_INDEX_O_U2UIndicators);
		super.optionalCodeToIndex.put(DisplayInformation._PARAMETER_CODE,_INDEX_O_DisplayInformation);
		super.optionalCodeToIndex.put(RemoteOperations._PARAMETER_CODE,_INDEX_O_RemoteOperations);
		super.optionalCodeToIndex.put(HTRInformation._PARAMETER_CODE,_INDEX_O_HTRInformation);
		super.optionalCodeToIndex.put(RedirectCounter._PARAMETER_CODE,_INDEX_O_RedirectCounter);
		super.optionalCodeToIndex.put(RedirectBackwardInformation._PARAMETER_CODE,_INDEX_O_RedirectBackwardInformation);
		
	}

	/**
	 * @throws ParameterRangeInvalidException 
	 * 	
	 */
	ReleaseMessageImpl(Object source) throws ParameterRangeInvalidException {
		super(source);
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());
		
		
		super.mandatoryVariableCodes.add(CauseIndicators._PARAMETER_CODE);
		super.mandatoryVariableCodeToIndex.put(CauseIndicators._PARAMETER_CODE, _INDEX_V_CauseIndicators);
		
		super.optionalCodes.add(RedirectionInformation._PARAMETER_CODE);
		super.optionalCodes.add(RedirectionNumber._PARAMETER_CODE);
		super.optionalCodes.add(AccessTransport._PARAMETER_CODE);
		super.optionalCodes.add(SignalingPointCode._PARAMETER_CODE);
		super.optionalCodes.add(UserToUserInformation._PARAMETER_CODE);
		super.optionalCodes.add(AutomaticCongestionLevel._PARAMETER_CODE);
		super.optionalCodes.add(NetworkSpecificFacility._PARAMETER_CODE);
		super.optionalCodes.add(AccessDeliveryInformation._PARAMETER_CODE);
		super.optionalCodes.add(ParameterCompatibilityInformation._PARAMETER_CODE);
		super.optionalCodes.add(UserToUserIndicators._PARAMETER_CODE);
		super.optionalCodes.add(DisplayInformation._PARAMETER_CODE);
		super.optionalCodes.add(RemoteOperations._PARAMETER_CODE);
		super.optionalCodes.add(HTRInformation._PARAMETER_CODE);
		super.optionalCodes.add(RedirectCounter._PARAMETER_CODE);
		super.optionalCodes.add(RedirectBackwardInformation._PARAMETER_CODE);
		
		
		super.optionalCodeToIndex.put(RedirectionInformation._PARAMETER_CODE,_INDEX_O_RedirectionInformation);
		super.optionalCodeToIndex.put(RedirectionNumber._PARAMETER_CODE,_INDEX_O_RedirectionNumber);
		super.optionalCodeToIndex.put(AccessTransport._PARAMETER_CODE,_INDEX_O_AccessTransport);
		super.optionalCodeToIndex.put(SignalingPointCode._PARAMETER_CODE,_INDEX_O_SignalingPointCode);
		super.optionalCodeToIndex.put(UserToUserInformation._PARAMETER_CODE,_INDEX_O_U2UInformation);
		super.optionalCodeToIndex.put(AutomaticCongestionLevel._PARAMETER_CODE,_INDEX_O_AutomaticCongestionLevel);
		super.optionalCodeToIndex.put(NetworkSpecificFacility._PARAMETER_CODE,_INDEX_O_NetworkSpecificFacility);
		super.optionalCodeToIndex.put(AccessDeliveryInformation._PARAMETER_CODE,_INDEX_O_AccessDeliveryInformation);
		super.optionalCodeToIndex.put(ParameterCompatibilityInformation._PARAMETER_CODE,_INDEX_O_ParameterCompatibilityInformation);
		super.optionalCodeToIndex.put(UserToUserIndicators._PARAMETER_CODE,_INDEX_O_U2UIndicators);
		super.optionalCodeToIndex.put(DisplayInformation._PARAMETER_CODE,_INDEX_O_DisplayInformation);
		super.optionalCodeToIndex.put(RemoteOperations._PARAMETER_CODE,_INDEX_O_RemoteOperations);
		super.optionalCodeToIndex.put(HTRInformation._PARAMETER_CODE,_INDEX_O_HTRInformation);
		super.optionalCodeToIndex.put(RedirectCounter._PARAMETER_CODE,_INDEX_O_RedirectCounter);
		super.optionalCodeToIndex.put(RedirectBackwardInformation._PARAMETER_CODE,_INDEX_O_RedirectBackwardInformation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.messages.ISUPMessage#decodeMandatoryParameters(byte[],
	 * int)
	 */
	@Override
	protected int decodeMandatoryParameters(byte[] b, int index) throws ParameterRangeInvalidException {
		int localIndex = index;

		if (b.length - index > 1) {

			// Message Type
			if (b[index] != this._MESSAGE_CODE_REL) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_REL);
			}
			index++;

			return index - localIndex;
		} else {
			throw new ParameterRangeInvalidException("byte[] must have atleast one octet");
		}
	}

	/**
	 * @param parameterBody
	 * @param parameterCode
	 * @throws ParameterRangeInvalidException
	 */
	protected void decodeMandatoryVariableBody(byte[] parameterBody, int parameterIndex) throws ParameterRangeInvalidException {
		switch (parameterIndex) {
		case _INDEX_V_CauseIndicators:
			CauseIndicatorsImpl cpn = new CauseIndicatorsImpl(parameterBody);
			this.setCauseIndicators(cpn);
			break;
		default:
			throw new ParameterRangeInvalidException("Unrecognized parameter index for mandatory variable part: " + parameterIndex);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.messages.ISUPMessage#decodeOptionalBody(byte[],
	 * byte)
	 */
	@Override
	protected void decodeOptionalBody(byte[] parameterBody, byte parameterCode) throws ParameterRangeInvalidException {

		switch ((int) parameterCode) {
		case RedirectionNumberImpl._PARAMETER_CODE:
			RedirectionNumberImpl rn = new RedirectionNumberImpl(parameterBody);
			this.setRedirectionNumber(rn);
			break;
		case RedirectionInformationImpl._PARAMETER_CODE:
			RedirectionInformationImpl ri = new RedirectionInformationImpl(parameterBody);
			this.setRedirectionInformation(ri);
			break;
		case AccessTransportImpl._PARAMETER_CODE:
			AccessTransportImpl at = new AccessTransportImpl(parameterBody);
			this.setAccessTransport(at);
			break;
		case SignalingPointCodeImpl._PARAMETER_CODE:
			SignalingPointCodeImpl v = new SignalingPointCodeImpl(parameterBody);
			this.setSignalingPointCode(v);
			break;
		case UserToUserInformationImpl._PARAMETER_CODE:
			UserToUserInformationImpl u2ui = new UserToUserInformationImpl(parameterBody);
			this.setU2UInformation(u2ui);
			break;
		case AutomaticCongestionLevelImpl._PARAMETER_CODE:
			AutomaticCongestionLevelImpl acl = new AutomaticCongestionLevelImpl(parameterBody);
			this.setAutomaticCongestionLevel(acl);
			break;
		case NetworkSpecificFacilityImpl._PARAMETER_CODE:
			NetworkSpecificFacilityImpl nsf = new NetworkSpecificFacilityImpl(parameterBody);
			this.setNetworkSpecificFacility(nsf);
			break;
		case AccessDeliveryInformationImpl._PARAMETER_CODE:
			AccessDeliveryInformationImpl adi = new AccessDeliveryInformationImpl(parameterBody);
			this.setAccessDeliveryInformation(adi);
			break;
		case ParameterCompatibilityInformationImpl._PARAMETER_CODE:
			ParameterCompatibilityInformationImpl pci = new ParameterCompatibilityInformationImpl(parameterBody);
			this.setParameterCompatibilityInformation(pci);
			break;
		case UserToUserIndicatorsImpl._PARAMETER_CODE:
			UserToUserIndicatorsImpl utui = new UserToUserIndicatorsImpl(parameterBody);
			this.setU2UIndicators(utui);
			break;
		case DisplayInformationImpl._PARAMETER_CODE:
			DisplayInformationImpl di = new DisplayInformationImpl(parameterBody);
			this.setDisplayInformation(di);
			break;
		case RemoteOperationsImpl._PARAMETER_CODE:
			RemoteOperationsImpl ro = new RemoteOperationsImpl(parameterBody);
			this.setRemoteOperations(ro);
			break;
		case HTRInformationImpl._PARAMETER_CODE:
			HTRInformationImpl htri = new HTRInformationImpl(parameterBody);
			this.setHTRInformation(htri);
			break;
		case RedirectBackwardInformationImpl._PARAMETER_CODE:
			RedirectBackwardInformationImpl rbi = new RedirectBackwardInformationImpl(parameterBody);
			this.setRedirectBackwardInformation(rbi);
			break;
		case RedirectCounterImpl._PARAMETER_CODE:
			RedirectCounterImpl rc = new RedirectCounterImpl(parameterBody);
			this.setRedirectCounter(rc);
			break;

		default:
			throw new IllegalArgumentException("Unrecognized parameter code for optional part: " + parameterCode);
		}

	}

	public CauseIndicators getCauseIndicators() {
		return (CauseIndicators) super.v_Parameters.get(_INDEX_V_CauseIndicators);
	}

	public void setCauseIndicators(CauseIndicators v) {
		super.v_Parameters.put(_INDEX_V_CauseIndicators, v);
	}

	public RedirectionInformation getRedirectionInformation() {
		return (RedirectionInformation) super.o_Parameters.get(_INDEX_O_RedirectionInformation);
	}

	public void setRedirectionInformation(RedirectionInformation v) {
		super.o_Parameters.put(_INDEX_O_RedirectionInformation, v);
	}

	public RedirectionNumber getRedirectionNumber() {
		return (RedirectionNumber) super.o_Parameters.get(_INDEX_O_RedirectionNumber);
	}

	public void setRedirectionNumber(RedirectionNumber v) {
		super.o_Parameters.put(_INDEX_O_RedirectionNumber, v);
	}

	public AccessTransport getAccessTransport() {
		return (AccessTransport) super.o_Parameters.get(_INDEX_O_AccessTransport);
	}

	public void setAccessTransport(AccessTransport v) {
		super.o_Parameters.put(_INDEX_O_AccessTransport, v);
	}

	public SignalingPointCode getSignalingPointCode() {
		return (SignalingPointCode) super.o_Parameters.get(_INDEX_O_SignalingPointCode);
	}

	public void setSignalingPointCode(SignalingPointCode v) {
		super.o_Parameters.put(_INDEX_O_SignalingPointCode, v);
	}

	public UserToUserInformation getU2UInformation() {
		return (UserToUserInformation) super.o_Parameters.get(_INDEX_O_U2UInformation);
	}

	public void setU2UInformation(UserToUserInformation v) {
		super.o_Parameters.put(_INDEX_O_U2UInformation, v);
	}

	public AutomaticCongestionLevel getAutomaticCongestionLevel() {
		return (AutomaticCongestionLevel) super.o_Parameters.get(_INDEX_O_AutomaticCongestionLevel);
	}

	public void setAutomaticCongestionLevel(AutomaticCongestionLevel v) {
		super.o_Parameters.put(_INDEX_O_AutomaticCongestionLevel, v);
	}

	public NetworkSpecificFacility getNetworkSpecificFacility() {
		return (NetworkSpecificFacility) super.o_Parameters.get(_INDEX_O_NetworkSpecificFacility);
	}

	public void setNetworkSpecificFacility(NetworkSpecificFacility v) {
		super.o_Parameters.put(_INDEX_O_NetworkSpecificFacility, v);
	}

	public AccessDeliveryInformation getAccessDeliveryInformation() {
		return (AccessDeliveryInformation) super.o_Parameters.get(_INDEX_O_AccessDeliveryInformation);
	}

	public void setAccessDeliveryInformation(AccessDeliveryInformation v) {
		super.o_Parameters.put(_INDEX_O_AccessDeliveryInformation, v);
	}

	public ParameterCompatibilityInformation getParameterCompatibilityInformation() {
		return (ParameterCompatibilityInformation) super.o_Parameters.get(_INDEX_O_ParameterCompatibilityInformation);
	}

	public void setParameterCompatibilityInformation(ParameterCompatibilityInformation v) {
		super.o_Parameters.put(_INDEX_O_ParameterCompatibilityInformation, v);
	}

	public UserToUserIndicators getU2UIndicators() {
		return (UserToUserIndicators) super.o_Parameters.get(_INDEX_O_U2UIndicators);
	}

	public void setU2UIndicators(UserToUserIndicators v) {
		super.o_Parameters.put(_INDEX_O_U2UIndicators, v);
	}

	public DisplayInformation getDisplayInformation() {
		return (DisplayInformation) super.o_Parameters.get(_INDEX_O_DisplayInformation);
	}

	public void setDisplayInformation(DisplayInformation v) {
		super.o_Parameters.put(_INDEX_O_DisplayInformation, v);
	}

	public RemoteOperations getRemoteOperations() {
		return (RemoteOperations) super.o_Parameters.get(_INDEX_O_RemoteOperations);
	}

	public void setRemoteOperations(RemoteOperations v) {
		super.o_Parameters.put(_INDEX_O_RemoteOperations, v);
	}

	public HTRInformation getHTRInformation() {
		return (HTRInformation) super.o_Parameters.get(_INDEX_O_HTRInformation);
	}

	public void setHTRInformation(HTRInformation v) {
		super.o_Parameters.put(_INDEX_O_HTRInformation, v);
	}

	public RedirectCounter getRedirectCounter() {
		return (RedirectCounter) super.o_Parameters.get(_INDEX_O_RedirectCounter);
	}

	public void setRedirectCounter(RedirectCounter v) {
		super.o_Parameters.put(_INDEX_O_RedirectCounter, v);
	}

	public RedirectBackwardInformation getRedirectBackwardInformation() {
		return (RedirectBackwardInformation) super.o_Parameters.get(_INDEX_O_RedirectBackwardInformation);
	}

	public void setRedirectBackwardInformation(RedirectBackwardInformation v) {
		super.o_Parameters.put(_INDEX_O_RedirectBackwardInformation, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.messages.ISUPMessage#getMessageType()
	 */
	@Override
	public MessageType getMessageType() {
		return this._MESSAGE_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.messages.ISUPMessage#
	 * getNumberOfMandatoryVariableLengthParameters()
	 */
	@Override
	protected int getNumberOfMandatoryVariableLengthParameters() {

		return _MANDATORY_VAR_COUNT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.messages.ISUPMessage#hasAllMandatoryParameters()
	 */
	@Override
	public boolean hasAllMandatoryParameters() {
		if (this.f_Parameters.get(_INDEX_F_MessageType) == null || this.f_Parameters.get(_INDEX_F_MessageType).getCode() != this.getMessageType().getCode()) {
			return false;
		}
		if (this.v_Parameters.get(_INDEX_V_CauseIndicators) == null) {
			return false;
		}
		return true;
	}



}

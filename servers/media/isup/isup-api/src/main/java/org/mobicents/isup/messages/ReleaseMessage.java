/**
 * Start time:21:00:56 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages;

import java.util.TreeMap;

import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.AccessDeliveryInformation;
import org.mobicents.isup.parameters.AutomaticCongestionLevel;
import org.mobicents.isup.parameters.CCSS;
import org.mobicents.isup.parameters.CallReference;
import org.mobicents.isup.parameters.CalledPartyNumber;
import org.mobicents.isup.parameters.CallingPartyCategory;
import org.mobicents.isup.parameters.CallingPartyNumber;
import org.mobicents.isup.parameters.CauseIndicators;
import org.mobicents.isup.parameters.ClosedUserGroupInterlockCode;
import org.mobicents.isup.parameters.ConnectionRequest;
import org.mobicents.isup.parameters.DisplayInformation;
import org.mobicents.isup.parameters.ForwardCallIndicators;
import org.mobicents.isup.parameters.ForwardGVNS;
import org.mobicents.isup.parameters.GenericDigits;
import org.mobicents.isup.parameters.GenericNotificationIndicator;
import org.mobicents.isup.parameters.GenericNumber;
import org.mobicents.isup.parameters.GenericReference;
import org.mobicents.isup.parameters.HTRInformation;
import org.mobicents.isup.parameters.ISUPParameter;
import org.mobicents.isup.parameters.LocationNumber;
import org.mobicents.isup.parameters.MLPPPrecedence;
import org.mobicents.isup.parameters.MessageType;
import org.mobicents.isup.parameters.NatureOfConnectionIndicators;
import org.mobicents.isup.parameters.NetworkManagementControls;
import org.mobicents.isup.parameters.NetworkSpecificFacility;
import org.mobicents.isup.parameters.OptionalForwardCallIndicators;
import org.mobicents.isup.parameters.OriginalCalledNumber;
import org.mobicents.isup.parameters.OriginatingISCPointCode;
import org.mobicents.isup.parameters.ParameterCompatibilityInformation;
import org.mobicents.isup.parameters.PropagationDelayCounter;
import org.mobicents.isup.parameters.RedirectBackwardInformation;
import org.mobicents.isup.parameters.RedirectCounter;
import org.mobicents.isup.parameters.RedirectingNumber;
import org.mobicents.isup.parameters.RedirectionInformation;
import org.mobicents.isup.parameters.RedirectionNumber;
import org.mobicents.isup.parameters.RemoteOperations;
import org.mobicents.isup.parameters.ServiceActivation;
import org.mobicents.isup.parameters.SignalingPointCode;
import org.mobicents.isup.parameters.TransitNetworkSelection;
import org.mobicents.isup.parameters.TransmissionMediumRequirement;
import org.mobicents.isup.parameters.UserServiceInformation;
import org.mobicents.isup.parameters.UserServiceInformationPrime;
import org.mobicents.isup.parameters.UserTeleserviceInformation;
import org.mobicents.isup.parameters.UserToUserIndicators;
import org.mobicents.isup.parameters.UserToUserInformation;
import org.mobicents.isup.parameters.accessTransport.AccessTransport;

/**
 * Start time:21:00:56 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ReleaseMessage extends ISUPMessage {

	public static final int _MESSAGE_CODE = 0x0C;
	public static final MessageType _MESSAGE_TYPE = new MessageType(_MESSAGE_CODE);

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
	public ReleaseMessage(Object source, byte[] b) throws ParameterRangeInvalidException {
		super(source);

		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());
		decodeElement(b);
		super.o_Parameters.put(_INDEX_O_EndOfOptionalParameters, _END_OF_OPTIONAL_PARAMETERS);

	}

	/**
	 * 	
	 */
	public ReleaseMessage() {
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());

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
			if (b[index] != this._MESSAGE_CODE) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE);
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
			CauseIndicators cpn = new CauseIndicators(parameterBody);
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
		case RedirectionNumber._PARAMETER_CODE:
			RedirectionNumber rn = new RedirectionNumber(parameterBody);
			this.setRedirectionNumber(rn);
			break;
		case RedirectionInformation._PARAMETER_CODE:
			RedirectionInformation ri = new RedirectionInformation(parameterBody);
			this.setRedirectionInformation(ri);
			break;
		case AccessTransport._PARAMETER_CODE:
			AccessTransport at = new AccessTransport(parameterBody);
			this.setAccessTransport(at);
			break;
		case SignalingPointCode._PARAMETER_CODE:
			SignalingPointCode v = new SignalingPointCode(parameterBody);
			this.setSignalingPointCode(v);
			break;
		case UserToUserInformation._PARAMETER_CODE:
			UserToUserInformation u2ui = new UserToUserInformation(parameterBody);
			this.setU2UInformation(u2ui);
			break;
		case AutomaticCongestionLevel._PARAMETER_CODE:
			AutomaticCongestionLevel acl = new AutomaticCongestionLevel(parameterBody);
			this.setAutomaticCongestionLevel(acl);
			break;
		case NetworkSpecificFacility._PARAMETER_CODE:
			NetworkSpecificFacility nsf = new NetworkSpecificFacility(parameterBody);
			this.setNetworkSpecificFacility(nsf);
			break;
		case AccessDeliveryInformation._PARAMETER_CODE:
			AccessDeliveryInformation adi = new AccessDeliveryInformation(parameterBody);
			this.setAccessDeliveryInformation(adi);
			break;
		case ParameterCompatibilityInformation._PARAMETER_CODE:
			ParameterCompatibilityInformation pci = new ParameterCompatibilityInformation(parameterBody);
			this.setParameterCompatibilityInformation(pci);
			break;
		case UserToUserIndicators._PARAMETER_CODE:
			UserToUserIndicators utui = new UserToUserIndicators(parameterBody);
			this.setU2UIndicators(utui);
			break;
		case DisplayInformation._PARAMETER_CODE:
			DisplayInformation di = new DisplayInformation(parameterBody);
			this.setDisplayInformation(di);
			break;
		case RemoteOperations._PARAMETER_CODE:
			RemoteOperations ro = new RemoteOperations(parameterBody);
			this.setRemoteOperations(ro);
			break;
		case HTRInformation._PARAMETER_CODE:
			HTRInformation htri = new HTRInformation(parameterBody);
			this.setHTRInformation(htri);
			break;
		case RedirectBackwardInformation._PARAMETER_CODE:
			RedirectBackwardInformation rbi = new RedirectBackwardInformation(parameterBody);
			this.setRedirectBackwardInformation(rbi);
			break;
		case RedirectCounter._PARAMETER_CODE:
			RedirectCounter rc = new RedirectCounter(parameterBody);
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

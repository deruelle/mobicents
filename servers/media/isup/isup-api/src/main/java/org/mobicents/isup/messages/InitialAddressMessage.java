/**
 * Start time:08:17:13 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages;

import java.util.TreeMap;

import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.BackwardCallIndicators;
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
import org.mobicents.isup.parameters.ISUPParameter;
import org.mobicents.isup.parameters.LocationNumber;
import org.mobicents.isup.parameters.MLPPPrecedence;
import org.mobicents.isup.parameters.MessageType;
import org.mobicents.isup.parameters.NatureOfConnectionIndicators;
import org.mobicents.isup.parameters.NetworkManagementControls;
import org.mobicents.isup.parameters.NetworkSpecificFacility;
import org.mobicents.isup.parameters.OptionalBakwardCallIndicators;
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
 * Start time:08:17:13 2009-07-17<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class InitialAddressMessage extends ISUPMessage {

	public static final int _MESSAGE_CODE = 0x01;
	public static final MessageType _MESSAGE_TYPE = new MessageType(_MESSAGE_CODE);
	private static final int _MANDATORY_VAR_COUNT = 1;
	// mandatory fixed L
	protected static final int _INDEX_F_MessageType = 0;
	protected static final int _INDEX_F_NatureOfConnectionIndicators = 1;
	protected static final int _INDEX_F_ForwardCallIndicators = 2;
	protected static final int _INDEX_F_CallingPartyCategory = 3;
	protected static final int _INDEX_F_TransmissionMediumRequirement = 4;
	// mandatory variable L
	protected static final int _INDEX_V_CalledPartyNumber = 0;
	// optional
	protected static final int _INDEX_O_TransitNetworkSelection = 0;
	protected static final int _INDEX_O_CallReference = 1;
	protected static final int _INDEX_O_CallingPartyNumber = 2;
	protected static final int _INDEX_O_OptionalForwardCallIndicators = 3;
	protected static final int _INDEX_O_RedirectingNumber = 4;
	protected static final int _INDEX_O_RedirectionInformation = 5;
	protected static final int _INDEX_O_ClosedUserGroupInterlockCode = 6;
	protected static final int _INDEX_O_ConnectionRequest = 7;
	protected static final int _INDEX_O_OriginalCalledNumber = 8;
	protected static final int _INDEX_O_UserToUserInformation = 9;
	protected static final int _INDEX_O_UserServiceInformation = 10;
	protected static final int _INDEX_O_NetworkSPecificFacility = 11;
	protected static final int _INDEX_O_GenericDigits = 12;
	protected static final int _INDEX_O_OriginatingISCPointCode = 13;
	protected static final int _INDEX_O_UserTeleserviceInformation = 14;
	protected static final int _INDEX_O_RemoteOperations = 15;
	protected static final int _INDEX_O_ParameterCompatibilityInformation = 16;
	protected static final int _INDEX_O_GenericNotificationIndicator = 17;
	protected static final int _INDEX_O_ServiceActivation = 18;
	protected static final int _INDEX_O_GenericReference = 19;
	protected static final int _INDEX_O_MLPPPrecedence = 20;
	protected static final int _INDEX_O_TransimissionMediumRequierementPrime = 21;
	protected static final int _INDEX_O_LocationNumber = 22;
	protected static final int _INDEX_O_ForwardGVNS = 23;
	protected static final int _INDEX_O_CCSS = 24;
	protected static final int _INDEX_O_NetworkManagementControls = 25;
	protected static final int _INDEX_O_EndOfOptionalParameters = 26;

	public InitialAddressMessage(Object source, byte[] b)  throws ParameterRangeInvalidException{
		super(source);
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());
		decodeElement(b);
		super.o_Parameters.put(_INDEX_O_EndOfOptionalParameters, _END_OF_OPTIONAL_PARAMETERS);

	}

	public InitialAddressMessage() {

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
			if(b[index]!=this._MESSAGE_CODE)
			{
				throw new ParameterRangeInvalidException("Message code is not: "+this._MESSAGE_CODE);
			}
			index++;
			
			try{
				byte[] natureOfConnectionIndicators = new byte[1];
				natureOfConnectionIndicators[0] = b[index++];
				
				NatureOfConnectionIndicators _nai = new NatureOfConnectionIndicators(natureOfConnectionIndicators);
				this.setNatureOfConnectionIndicators(_nai);
			}catch(Exception e)
			{
				//AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse NatureOfConnectionIndicators due to: ",e);
			}
			
			try{
				byte[] body = new byte[2];
				body[0] = b[index++];
				body[1] = b[index++];
				
				ForwardCallIndicators v = new ForwardCallIndicators(body);
				this.setForwardCallIndicators(v);
			}catch(Exception e)
			{
				//AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse ForwardCallIndicators due to: ",e);
			}
			
			try{
				byte[] body = new byte[2];
				body[0] = b[index++];
				body[1] = b[index++];
				
				ForwardCallIndicators v = new ForwardCallIndicators(body);
				this.setForwardCallIndicators(v);
			}catch(Exception e)
			{
				//AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse ForwardCallIndicators due to: ",e);
			}
			
			try{
				byte[] body = new byte[1];
				body[0] = b[index++];

				
				CallingPartyCategory v = new CallingPartyCategory(body);
				this.setCallingPartCategory(v);
			}catch(Exception e)
			{
				//AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse CallingPartyCategory due to: ",e);
			}
			try{
				byte[] body = new byte[1];
				body[0] = b[index++];

				
				TransmissionMediumRequirement v = new TransmissionMediumRequirement(body);
				this.setTransmissionMediumRequirement(v);
			}catch(Exception e)
			{
				//AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse TransmissionMediumRequirement due to: ",e);
			}
			return index-localIndex;
		} else {
			throw new IllegalArgumentException("byte[] must have atleast two octets");
		}
	}

	

	/**
	 * @param parameterBody
	 * @param parameterCode
	 */
	protected void decodeMandatoryVariableBody(byte[] parameterBody, int parameterIndex) {
		switch (parameterIndex) {
		case _INDEX_V_CalledPartyNumber:
			CalledPartyNumber cpn = new CalledPartyNumber(parameterBody);
			this.setCalledPartyNumber(cpn);
			break;
		default:
			throw new IllegalArgumentException("Unrecognized parameter index for mandatory variable part: " + parameterIndex);
		}

	}

	/* (non-Javadoc)
	 * @see org.mobicents.isup.messages.ISUPMessage#decodeOptionalBody(byte[], byte)
	 */
	@Override
	protected void decodeOptionalBody(byte[] parameterBody, byte parameterCode) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		switch ( (int)parameterCode) {
case TransitNetworkSelection._PARAMETER_CODE:
			TransitNetworkSelection v = new TransitNetworkSelection(parameterBody);
			setTransitNetworkSelection(v);
			break;
case CallReference._PARAMETER_CODE:
	CallReference cr = new CallReference(parameterBody);
	this.setCallReference(cr);
	break;
case CallingPartyNumber._PARAMETER_CODE:
	CallingPartyNumber cpn = new CallingPartyNumber(parameterBody);
	this.setCallingPartyNumber(cpn);
	break;
case OptionalForwardCallIndicators._PARAMETER_CODE:
	OptionalForwardCallIndicators ofci = new OptionalForwardCallIndicators(parameterBody);
	this.setOptForwardCallIndicators(ofci);
	break;
case RedirectingNumber._PARAMETER_CODE:
	RedirectingNumber rn = new RedirectingNumber(parameterBody);
	this.setRedirectingNumber(rn);
break;
case RedirectionInformation._PARAMETER_CODE:
	RedirectionInformation ri = new RedirectionInformation(parameterBody);
	this.setRedirectionInformation(ri);
break;
case ClosedUserGroupInterlockCode._PARAMETER_CODE:
	ClosedUserGroupInterlockCode cugic = new ClosedUserGroupInterlockCode(parameterBody);
	this.setCUserGroupInterlockCode(cugic);
	break;
case ConnectionRequest._PARAMETER_CODE:
    ConnectionRequest cr2 = new ConnectionRequest(parameterBody);
    this.setConnectionRequest(cr2);
break;
case OriginalCalledNumber._PARAMETER_CODE:
	OriginalCalledNumber orn = new OriginalCalledNumber(parameterBody);
	this.setOriginalCalledNumber(orn);
break;
case UserToUserInformation._PARAMETER_CODE:
	UserToUserInformation u2ui = new UserToUserInformation(parameterBody);
	this.setU2UInformation(u2ui);
break;
case AccessTransport._PARAMETER_CODE:
    AccessTransport at = new AccessTransport(parameterBody);
    this.setAccessTransport(at);
break;
case UserServiceInformation._PARAMETER_CODE:
	UserServiceInformation usi = new UserServiceInformation(parameterBody);
	this.setUserServiceInformation(usi);
	break;
case UserToUserIndicators._PARAMETER_CODE:
	UserToUserIndicators utui = new UserToUserIndicators(parameterBody);
	this.setU2UIndicators(utui);
	break;
case GenericNumber._PARAMETER_CODE:
	GenericNumber gn = new GenericNumber(parameterBody);
	this.setGenericNumber(gn);
	break;
case PropagationDelayCounter._PARAMETER_CODE:
	PropagationDelayCounter pdc = new PropagationDelayCounter(parameterBody);
	this.setPropagationDelayCounter(pdc);
break;
case UserServiceInformationPrime._PARAMETER_CODE:
	UserServiceInformationPrime usip = new UserServiceInformationPrime(parameterBody);
	this.setUserServiceInformationPrime(usip);
break;
case NetworkSpecificFacility._PARAMETER_CODE:
	NetworkSpecificFacility nsf = new NetworkSpecificFacility(parameterBody);
	this.setNetworkSpecificFacility(nsf);
break;
case GenericDigits._PARAMETER_CODE:
	GenericDigits gd = new GenericDigits(parameterBody);
	this.setGenericDigits(gd);
break;
case OriginatingISCPointCode._PARAMETER_CODE:
	OriginatingISCPointCode vv = new OriginatingISCPointCode(parameterBody);
	this.setOriginatingISCPointCode(vv);
break;
case UserTeleserviceInformation._PARAMETER_CODE:
	UserTeleserviceInformation uti = new UserTeleserviceInformation(parameterBody);
	this.setUserTeleserviceInformation(uti);
break;
case RemoteOperations._PARAMETER_CODE:
	RemoteOperations ro = new RemoteOperations(parameterBody);
	this.setRemoteOperations(ro);
break;
case ParameterCompatibilityInformation._PARAMETER_CODE:
	ParameterCompatibilityInformation pci = new ParameterCompatibilityInformation(parameterBody);
	this.setParameterCompatibilityInformation(pci);
break;
case GenericNotificationIndicator._PARAMETER_CODE:
	GenericNotificationIndicator gni = new GenericNotificationIndicator(parameterBody);
	this.setGenericNotificationIndicator(gni);
break;
case ServiceActivation._PARAMETER_CODE:
	ServiceActivation sa = new ServiceActivation(parameterBody);
	this.setServiceActivation(sa);
break;
case GenericReference._PARAMETER_CODE:
	GenericReference gr = new GenericReference(parameterBody);
	this.setGenericReference(gr);
break;
case MLPPPrecedence._PARAMETER_CODE:
	MLPPPrecedence mlpp = new MLPPPrecedence(parameterBody);
	this.setMLPPPrecedence(mlpp);
	
case TransmissionMediumRequirement._PARAMETER_CODE:
	TransmissionMediumRequirement tmr = new TransmissionMediumRequirement(parameterBody);
	this.setTransmissionMediumRequirement(tmr);
case LocationNumber._PARAMETER_CODE:
	LocationNumber ln = new LocationNumber(parameterBody);
	this.setLocationNumber(ln);
case ForwardGVNS._PARAMETER_CODE:
	ForwardGVNS fgvns = new ForwardGVNS(parameterBody);
	this.setForwardGVNS(fgvns);
case CCSS._PARAMETER_CODE:
	CCSS ccss = new CCSS(parameterBody);
	this.setCCSS(ccss);
case MLPPPrecedence._PARAMETER_CODE:
	MLPPPrecedence mlpp = new MLPPPrecedence(parameterBody);
	this.setMLPPPrecedence(mlpp);
break;
			default:
				throw new IllegalArgumentException("Unrecognized parameter code for optional part: "+parameterCode);
		}
	}

	/* (non-Javadoc)
	 * @see org.mobicents.isup.messages.ISUPMessage#getNumberOfMandatoryVariableLengthParameters()
	 */
	@Override
	protected int getNumberOfMandatoryVariableLengthParameters() {
		
		return _MANDATORY_VAR_COUNT;
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
	 * @see org.mobicents.isup.messages.ISUPMessage#hasAllMandatoryParameters()
	 */
	@Override
	public boolean hasAllMandatoryParameters() {
		// TODO Auto-generated method stub
		return false;
	}

	public NatureOfConnectionIndicators getNatureOfConnectionIndicators() {
		return (NatureOfConnectionIndicators) super.f_Parameters.get(_INDEX_F_NatureOfConnectionIndicators);
	}

	public void setNatureOfConnectionIndicators(NatureOfConnectionIndicators v) {
		super.f_Parameters.put(_INDEX_F_NatureOfConnectionIndicators, v);
	}

	
	public ForwardCallIndicators getForwardCallIndicators() {
		return (ForwardCallIndicators) super.f_Parameters.get(_INDEX_F_ForwardCallIndicators);
	}

	public void setForwardCallIndicators(ForwardCallIndicators v) {
		super.f_Parameters.put(_INDEX_F_ForwardCallIndicators, v);
	}
	
	public CallingPartyCategory getCallingPartCategory() {
		return (CallingPartyCategory) super.f_Parameters.get(_INDEX_F_CallingPartyCategory);
	}

	public void setCallingPartCategory(CallingPartyCategory v) {
		super.f_Parameters.put(_INDEX_F_CallingPartyCategory, v);
	}
	
	public TransmissionMediumRequirement getTransmissionMediumRequirement() {
		return (TransmissionMediumRequirement) super.f_Parameters.get(_INDEX_V_CalledPartyNumber);
	}

	public void setTransmissionMediumRequirement(TransmissionMediumRequirement v) {
		super.f_Parameters.put(_INDEX_F_TransmissionMediumRequirement, v);
	}
	

	public CalledPartyNumber getCalledPartyNumber() {
		return (CalledPartyNumber) super.v_Parameters.get(_INDEX_V_CalledPartyNumber);
	}

	public void setCalledPartyNumber(CalledPartyNumber v) {
		super.v_Parameters.put(_INDEX_V_CalledPartyNumber, v);
	}

	
	public TransitNetworkSelection getTransitNetworkSelection() {
		return (TransitNetworkSelection) super.o_Parameters.get(_INDEX_O_TransitNetworkSelection);
	}

	public void setTransitNetworkSelection(TransitNetworkSelection v) {
		super.o_Parameters.put(_INDEX_O_TransitNetworkSelection, v);
	}
	
	public CallReference getCallReference() {
		return (CallReference) super.o_Parameters.get(_INDEX_O_CallReference);
	}

	public void setCallReference(CallReference v) {
		super.o_Parameters.put(_INDEX_O_CallReference, v);
	}
	
	public CallingPartyNumber getCallingPartyNumber() {
		return (CallingPartyNumber) super.o_Parameters.get(_INDEX_O_CallingPartyNumber);
	}

	public void setCallingPartyNumber(CallingPartyNumber v) {
		super.o_Parameters.put(_INDEX_O_CallingPartyNumber, v);
	}
	
	
	public OptionalForwardCallIndicators getOptForwardCallIndicators() {
		return (OptionalForwardCallIndicators) super.o_Parameters.get(_INDEX_O_OptionalForwardCallIndicators);
	}

	public void setOptForwardCallIndicators(OptionalForwardCallIndicators v) {
		super.o_Parameters.put(_INDEX_O_OptionalForwardCallIndicators, v);
	}
	
	
	
	public RedirectingNumber getRedirectingNumber() {
		return (RedirectingNumber) super.o_Parameters.get(_INDEX_O_RedirectingNumber);
	}

	public void setRedirectingNumber(RedirectingNumber v) {
		super.o_Parameters.put(_INDEX_O_RedirectingNumber, v);
	}
	
	public RedirectionInformation getRedirectionInformation() {
		return (RedirectionInformation) super.o_Parameters.get(_INDEX_O_RedirectionInformation);
	}

	public void setRedirectionInformation(RedirectionInformation v) {
		super.o_Parameters.put(_INDEX_O_RedirectionInformation, v);
	}
	
	public ClosedUserGroupInterlockCode getCUserGroupInterlockCode() {
		return (ClosedUserGroupInterlockCode) super.o_Parameters.get(_INDEX_O_ClosedUserGroupInterlockCode);
	}

	public void setCUserGroupInterlockCode(ClosedUserGroupInterlockCode v) {
		super.o_Parameters.put(_INDEX_O_ClosedUserGroupInterlockCode, v);
	}
	
	public ConnectionRequest getConnectionRequest() {
		return (ConnectionRequest) super.o_Parameters.get(_INDEX_O_ConnectionRequest);
	}

	public void setConnectionRequest(ConnectionRequest v) {
		super.o_Parameters.put(_INDEX_O_ConnectionRequest, v);
	}
	
	
	public OriginalCalledNumber getOriginalCalledNumber() {
		return (OriginalCalledNumber) super.o_Parameters.get(_INDEX_O_OriginalCalledNumber);
	}

	public void setOriginalCalledNumber(OriginalCalledNumber v) {
		super.o_Parameters.put(_INDEX_O_OriginalCalledNumber, v);
	}
	
	public UserToUserInformation getU2UInformation() {
		return (UserToUserInformation) super.o_Parameters.get(_INDEX_O_UserToUserInformation);
	}

	public void setU2UInformation(UserToUserInformation v) {
		super.o_Parameters.put(_INDEX_O_UserToUserInformation, v);
	}
	
	
	public UserServiceInformation getUserServiceInformation() {
		return (UserServiceInformation) super.o_Parameters.get(_INDEX_O_UserServiceInformation);
	}

	public void setUserServiceInformation(UserServiceInformation v) {
		super.o_Parameters.put(_INDEX_O_UserServiceInformation, v);
	}
	
	public NetworkSpecificFacility getNetworkSpecificFacility() {
		return (NetworkSpecificFacility) super.o_Parameters.get(_INDEX_O_NetworkSPecificFacility);
	}

	public void setNetworkSpecificFacility(NetworkSpecificFacility v) {
		super.o_Parameters.put(_INDEX_O_NetworkSPecificFacility, v);
	}
	
	public GenericDigits getGenericDigits() {
		return (GenericDigits) super.o_Parameters.get(_INDEX_O_GenericDigits);
	}

	public void setGenericDigits(GenericDigits v) {
		super.o_Parameters.put(_INDEX_O_GenericDigits, v);
	}
	
	public OriginatingISCPointCode getOriginatingISCPointCode() {
		return (OriginatingISCPointCode) super.o_Parameters.get(_INDEX_O_OriginatingISCPointCode);
	}

	public void setOriginatingISCPointCode(OriginatingISCPointCode v) {
		super.o_Parameters.put(_INDEX_O_OriginatingISCPointCode, v);
	}
	

	public UserTeleserviceInformation getUserTeleserviceInformation() {
		return (UserTeleserviceInformation) super.o_Parameters.get(_INDEX_O_UserTeleserviceInformation);
	}

	public void setUserTeleserviceInformation(UserTeleserviceInformation v) {
		super.o_Parameters.put(_INDEX_O_UserTeleserviceInformation, v);
	}
	
	public RemoteOperations getRemoteOperations() {
		return (RemoteOperations) super.o_Parameters.get(_INDEX_O_RemoteOperations);
	}

	public void setRemoteOperations(RemoteOperations v) {
		super.o_Parameters.put(_INDEX_O_RemoteOperations, v);
	}
	
	public ParameterCompatibilityInformation getParameterCompatibilityInformation() {
		return (ParameterCompatibilityInformation) super.o_Parameters.get(_INDEX_O_ParameterCompatibilityInformation);
	}

	public void setParameterCompatibilityInformation(ParameterCompatibilityInformation v) {
		super.o_Parameters.put(_INDEX_O_ParameterCompatibilityInformation, v);
	}
	
	public GenericNotificationIndicator getGenericNotificationIndicator() {
		return (GenericNotificationIndicator) super.o_Parameters.get(_INDEX_O_GenericNotificationIndicator);
	}

	public void setGenericNotificationIndicator(GenericNotificationIndicator v) {
		super.o_Parameters.put(_INDEX_O_GenericNotificationIndicator, v);
	}
	
	public ServiceActivation getServiceActivation() {
		return (ServiceActivation) super.o_Parameters.get(_INDEX_O_ServiceActivation);
	}

	public void setServiceActivation(ServiceActivation v) {
		super.o_Parameters.put(_INDEX_O_ServiceActivation, v);
	}
	//Not defined yet... FIXME: XXX
//	public GenericReference getGenericReference() {
//		return (GenericReference) super.o_Parameters.get(_INDEX_O_GenericReference);
//	}
//
//	public void setGenericReference(GenericReference v) {
//		super.o_Parameters.put(_INDEX_O_GenericReference, v);
//	}
	
	
	public MLPPPrecedence getMLPPPrecedence() {
		return (MLPPPrecedence) super.o_Parameters.get(_INDEX_O_MLPPPrecedence);
	}

	public void setMLPPPrecedence(MLPPPrecedence v) {
		super.o_Parameters.put(_INDEX_O_MLPPPrecedence, v);
	}
	
	public TransimissionMediumRequierementPrime getTransimissionMediumReqPrime() {
		return (TransimissionMediumRequierementPrime) super.o_Parameters.get(_INDEX_O_TransimissionMediumRequierementPrime);
	}

	public void setTransimissionMediumReqPrime(TransimissionMediumRequierementPrime v) {
		super.o_Parameters.put(_INDEX_O_TransimissionMediumRequierementPrime, v);
	}
	public LocationNumber getLocationNumber() {
		return (LocationNumber) super.o_Parameters.get(_INDEX_O_LocationNumber);
	}

	public void setLocationNumber(LocationNumber v) {
		super.o_Parameters.put(_INDEX_O_LocationNumber, v);
	}
	
	
	public ForwardGVNS getForwardGVNS() {
		return (ForwardGVNS) super.o_Parameters.get(_INDEX_O_ForwardGVNS);
	}

	public void setForwardGVNS(ForwardGVNS v) {
		super.o_Parameters.put(_INDEX_O_ForwardGVNS, v);
	}
	
//	public CCSS getCCSS() {
//		return (CCSS) super.o_Parameters.get(_INDEX_O_CCSS);
//	}
//
//	public void setCCSS(CCSS v) {
//		super.o_Parameters.put(_INDEX_O_CCSS, v);
//	}
	
	public NetworkManagementControls getNetworkManagementControls() {
		return (NetworkManagementControls) super.o_Parameters.get(_INDEX_O_NetworkManagementControls);
	}

	public void setNetworkManagementControls(NetworkManagementControls v) {
		super.o_Parameters.put(_INDEX_O_NetworkManagementControls, v);
	}

	


}

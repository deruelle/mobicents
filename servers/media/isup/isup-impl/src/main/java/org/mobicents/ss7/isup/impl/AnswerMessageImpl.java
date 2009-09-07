/**
 * Start time:23:55:28 2009-09-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.impl;

import java.util.TreeMap;

import org.mobicents.ss7.isup.ParameterRangeInvalidException;
import org.mobicents.ss7.isup.Utils;
import org.mobicents.ss7.isup.impl.message.parameter.AccessDeliveryInformationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.ApplicationTransportParameterImpl;
import org.mobicents.ss7.isup.impl.message.parameter.BackwardCallIndicatorsImpl;
import org.mobicents.ss7.isup.impl.message.parameter.BackwardGVNSImpl;
import org.mobicents.ss7.isup.impl.message.parameter.CallHistoryInformationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.CallReferenceImpl;
import org.mobicents.ss7.isup.impl.message.parameter.ConferenceTreatmentIndicatorsImpl;
import org.mobicents.ss7.isup.impl.message.parameter.ConnectedNumberImpl;
import org.mobicents.ss7.isup.impl.message.parameter.DisplayInformationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.EchoControlInformationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.GenericNotificationIndicatorImpl;
import org.mobicents.ss7.isup.impl.message.parameter.GenericNumberImpl;
import org.mobicents.ss7.isup.impl.message.parameter.MessageTypeImpl;
import org.mobicents.ss7.isup.impl.message.parameter.NetworkSpecificFacilityImpl;
import org.mobicents.ss7.isup.impl.message.parameter.OptionalBackwardCallIndicatorsImpl;
import org.mobicents.ss7.isup.impl.message.parameter.ParameterCompatibilityInformationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.PivotRoutingBackwardInformationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.RedirectStatusImpl;
import org.mobicents.ss7.isup.impl.message.parameter.RedirectionNumberImpl;
import org.mobicents.ss7.isup.impl.message.parameter.RedirectionNumberRestrictionImpl;
import org.mobicents.ss7.isup.impl.message.parameter.RemoteOperationsImpl;
import org.mobicents.ss7.isup.impl.message.parameter.ServiceActivationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.TransmissionMediumUsedImpl;
import org.mobicents.ss7.isup.impl.message.parameter.UserToUserIndicatorsImpl;
import org.mobicents.ss7.isup.impl.message.parameter.UserToUserInformationImpl;
import org.mobicents.ss7.isup.impl.message.parameter.accessTransport.AccessTransportImpl;
import org.mobicents.ss7.isup.message.AnswerMessage;
import org.mobicents.ss7.isup.message.parameter.AccessDeliveryInformation;
import org.mobicents.ss7.isup.message.parameter.ApplicationTransportParameter;
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.BackwardGVNS;
import org.mobicents.ss7.isup.message.parameter.CallHistoryInformation;
import org.mobicents.ss7.isup.message.parameter.CallReference;
import org.mobicents.ss7.isup.message.parameter.ConferenceTreatmentIndicators;
import org.mobicents.ss7.isup.message.parameter.ConnectedNumber;
import org.mobicents.ss7.isup.message.parameter.DisplayInformation;
import org.mobicents.ss7.isup.message.parameter.EchoControlInformation;
import org.mobicents.ss7.isup.message.parameter.GenericNotificationIndicator;
import org.mobicents.ss7.isup.message.parameter.GenericNumber;
import org.mobicents.ss7.isup.message.parameter.ISUPParameter;
import org.mobicents.ss7.isup.message.parameter.MessageType;
import org.mobicents.ss7.isup.message.parameter.NetworkSpecificFacility;
import org.mobicents.ss7.isup.message.parameter.OptionalBackwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.ParameterCompatibilityInformation;
import org.mobicents.ss7.isup.message.parameter.PivotRoutingBackwardInformation;
import org.mobicents.ss7.isup.message.parameter.RedirectStatus;
import org.mobicents.ss7.isup.message.parameter.RedirectionNumber;
import org.mobicents.ss7.isup.message.parameter.RedirectionNumberRestriction;
import org.mobicents.ss7.isup.message.parameter.RemoteOperations;
import org.mobicents.ss7.isup.message.parameter.ServiceActivation;
import org.mobicents.ss7.isup.message.parameter.TransmissionMediumUsed;
import org.mobicents.ss7.isup.message.parameter.UserToUserIndicators;
import org.mobicents.ss7.isup.message.parameter.UserToUserInformation;
import org.mobicents.ss7.isup.message.parameter.accessTransport.AccessTransport;

/**
 * Start time:23:55:28 2009-09-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class AnswerMessageImpl extends ISUPMessageImpl implements AnswerMessage {

	public static final MessageTypeImpl _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_ANM);

	protected static final int _INDEX_F_MessageType = 0;

	protected static final int _INDEX_O_BackwardCallIndicators = 0;
	protected static final int _INDEX_O_OptionalBackwardCallIndicators = 1;
	protected static final int _INDEX_O_CallReference = 2;
	protected static final int _INDEX_O_UserToUserIndicators = 3;
	protected static final int _INDEX_O_UserToUserInformation = 4;
	protected static final int _INDEX_O_ConnectedNumber = 5;
	protected static final int _INDEX_O_AccessTransport = 6;
	protected static final int _INDEX_O_AccessDeliveryInformation = 7;
	protected static final int _INDEX_O_GenericNotificationIndicator = 8;
	protected static final int _INDEX_O_ParameterCompatibilityInformation = 9;
	protected static final int _INDEX_O_BackwardGVNS = 10;
	protected static final int _INDEX_O_CallHistoryInformation = 11;
	protected static final int _INDEX_O_GenericNumber = 12;
	protected static final int _INDEX_O_TransmissionMediumUsed = 13;
	protected static final int _INDEX_O_NetworkSpecificFacility = 14;
	protected static final int _INDEX_O_RemoteOperations = 15;
	protected static final int _INDEX_O_RedirectionNumber = 16;
	protected static final int _INDEX_O_ServiceActivation = 17;
	protected static final int _INDEX_O_EchoControlInformation = 18;
	protected static final int _INDEX_O_RedirectionNumberRestriction = 19;
	protected static final int _INDEX_O_DisplayInformation = 20;
	protected static final int _INDEX_O_ConferenceTreatmentIndicators = 21;
	protected static final int _INDEX_O_ApplicationTransportParameter = 22;
	protected static final int _INDEX_O_PivotRoutingBackwardInformation = 23;
	protected static final int _INDEX_O_RedirectStatus = 24;
	protected static final int _INDEX_O_EndOfOptionalParameters = 25;

	
	 AnswerMessageImpl(Object source, byte[] b) throws ParameterRangeInvalidException {
		this(source);
		decodeElement(b);
		

	}
	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	 AnswerMessageImpl(Object source) {
		super(source);
		// FIXME: this is bad, we always fill this, we shouyld move that
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());

		super.o_Parameters.put(_INDEX_O_EndOfOptionalParameters, _END_OF_OPTIONAL_PARAMETERS);

		super.optionalCodes.add(BackwardCallIndicators._PARAMETER_CODE);
		super.optionalCodes.add(OptionalBackwardCallIndicators._PARAMETER_CODE);
		super.optionalCodes.add(CallReference._PARAMETER_CODE);
		super.optionalCodes.add(UserToUserIndicators._PARAMETER_CODE);
		super.optionalCodes.add(UserToUserInformation._PARAMETER_CODE);
		super.optionalCodes.add(ConnectedNumber._PARAMETER_CODE);
		super.optionalCodes.add(AccessTransport._PARAMETER_CODE);
		super.optionalCodes.add(AccessDeliveryInformation._PARAMETER_CODE);
		super.optionalCodes.add(GenericNotificationIndicator._PARAMETER_CODE);
		super.optionalCodes.add(ParameterCompatibilityInformation._PARAMETER_CODE);
		super.optionalCodes.add(BackwardGVNS._PARAMETER_CODE);
		super.optionalCodes.add(CallHistoryInformation._PARAMETER_CODE);
		super.optionalCodes.add(GenericNumber._PARAMETER_CODE);
		super.optionalCodes.add(TransmissionMediumUsed._PARAMETER_CODE);
		super.optionalCodes.add(NetworkSpecificFacility._PARAMETER_CODE);
		super.optionalCodes.add(RemoteOperations._PARAMETER_CODE);
		super.optionalCodes.add(RedirectionNumber._PARAMETER_CODE);
		super.optionalCodes.add(ServiceActivation._PARAMETER_CODE);
		super.optionalCodes.add(EchoControlInformation._PARAMETER_CODE);
		super.optionalCodes.add(RedirectionNumberRestriction._PARAMETER_CODE);
		super.optionalCodes.add(DisplayInformation._PARAMETER_CODE);
		super.optionalCodes.add(ConferenceTreatmentIndicators._PARAMETER_CODE);
		super.optionalCodes.add(ApplicationTransportParameter._PARAMETER_CODE);
		super.optionalCodes.add(PivotRoutingBackwardInformation._PARAMETER_CODE);
		super.optionalCodes.add(RedirectStatus._PARAMETER_CODE);


		super.optionalCodeToIndex.put(BackwardCallIndicators._PARAMETER_CODE, _INDEX_O_BackwardCallIndicators);
		super.optionalCodeToIndex.put(OptionalBackwardCallIndicators._PARAMETER_CODE, _INDEX_O_OptionalBackwardCallIndicators);
		super.optionalCodeToIndex.put(CallReference._PARAMETER_CODE, _INDEX_O_CallReference);
		super.optionalCodeToIndex.put(UserToUserIndicators._PARAMETER_CODE, _INDEX_O_UserToUserIndicators);
		super.optionalCodeToIndex.put(UserToUserInformation._PARAMETER_CODE, _INDEX_O_UserToUserInformation);
		super.optionalCodeToIndex.put(ConnectedNumber._PARAMETER_CODE, _INDEX_O_ConnectedNumber);
		super.optionalCodeToIndex.put(AccessTransport._PARAMETER_CODE, _INDEX_O_AccessTransport);
		super.optionalCodeToIndex.put(AccessDeliveryInformation._PARAMETER_CODE, _INDEX_O_AccessDeliveryInformation);
		super.optionalCodeToIndex.put(GenericNotificationIndicator._PARAMETER_CODE, _INDEX_O_GenericNotificationIndicator);
		super.optionalCodeToIndex.put(ParameterCompatibilityInformation._PARAMETER_CODE, _INDEX_O_ParameterCompatibilityInformation);
		super.optionalCodeToIndex.put(BackwardGVNS._PARAMETER_CODE, _INDEX_O_BackwardGVNS);
		super.optionalCodeToIndex.put(CallHistoryInformation._PARAMETER_CODE, _INDEX_O_CallHistoryInformation);
		super.optionalCodeToIndex.put(GenericNumber._PARAMETER_CODE, _INDEX_O_GenericNumber);
		super.optionalCodeToIndex.put(TransmissionMediumUsed._PARAMETER_CODE, _INDEX_O_TransmissionMediumUsed);
		super.optionalCodeToIndex.put(NetworkSpecificFacility._PARAMETER_CODE, _INDEX_O_NetworkSpecificFacility);
		super.optionalCodeToIndex.put(RemoteOperations._PARAMETER_CODE, _INDEX_O_RemoteOperations);
		super.optionalCodeToIndex.put(RedirectionNumber._PARAMETER_CODE, _INDEX_O_RedirectionNumber);
		super.optionalCodeToIndex.put(ServiceActivation._PARAMETER_CODE, _INDEX_O_ServiceActivation);
		super.optionalCodeToIndex.put(EchoControlInformation._PARAMETER_CODE, _INDEX_O_EchoControlInformation);
		super.optionalCodeToIndex.put(RedirectionNumberRestriction._PARAMETER_CODE, _INDEX_O_RedirectionNumberRestriction);
		super.optionalCodeToIndex.put(DisplayInformation._PARAMETER_CODE, _INDEX_O_DisplayInformation);
		super.optionalCodeToIndex.put(ConferenceTreatmentIndicators._PARAMETER_CODE, _INDEX_O_ConferenceTreatmentIndicators);
		super.optionalCodeToIndex.put(ApplicationTransportParameter._PARAMETER_CODE, _INDEX_O_ApplicationTransportParameter);
		super.optionalCodeToIndex.put(PivotRoutingBackwardInformation._PARAMETER_CODE, _INDEX_O_PivotRoutingBackwardInformation);
		super.optionalCodeToIndex.put(RedirectStatus._PARAMETER_CODE, _INDEX_O_RedirectStatus);

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.ss7.isup.ISUPMessageImpl#decodeMandatoryParameters(byte[],
	 * int)
	 */
	@Override
	protected int decodeMandatoryParameters(byte[] b, int index) throws ParameterRangeInvalidException {
		int localIndex = index;
		if (b.length - index > 1) {

			// Message Type
			if (b[index] != this._MESSAGE_CODE_ANM) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_ACM);
			}
			index++;
			
			// return 3;
			return index - localIndex;
		} else {
			throw new IllegalArgumentException("byte[] must have atleast two octets");
		}
	}

	@Override
	protected int decodeMandatoryVariableParameters(byte[] b, int index) throws ParameterRangeInvalidException {
		


		return 0;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.ss7.isup.ISUPMessageImpl#decodeMandatoryVariableBody(byte
	 * [], int)
	 */
	@Override
	protected void decodeMandatoryVariableBody(byte[] parameterBody, int parameterIndex) throws ParameterRangeInvalidException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageImpl#decodeOptionalBody(byte[],
	 * byte)
	 */
	@Override
	protected void decodeOptionalBody(byte[] parameterBody, byte parameterCode) throws ParameterRangeInvalidException {
		
		switch ((int) parameterCode) {
		
		case BackwardCallIndicators._PARAMETER_CODE:
			BackwardCallIndicators BCMI = new BackwardCallIndicatorsImpl(parameterBody);
			super.addParameter(BCMI);
			break;
		case OptionalBackwardCallIndicators._PARAMETER_CODE:
			OptionalBackwardCallIndicators OBCI = new OptionalBackwardCallIndicatorsImpl(parameterBody);
			super.addParameter(OBCI);
			break;
		case CallReference._PARAMETER_CODE:
			CallReference CR = new CallReferenceImpl(parameterBody);
			super.addParameter(CR);
			break;
		case UserToUserIndicators._PARAMETER_CODE:
			UserToUserIndicators U2UI = new UserToUserIndicatorsImpl(parameterBody);
			super.addParameter(U2UI);
			break;
		case UserToUserInformation._PARAMETER_CODE:
			UserToUserInformation U2UII = new UserToUserInformationImpl(parameterBody);
			super.addParameter(U2UII);
			break;
		case ConnectedNumber._PARAMETER_CODE:
			ConnectedNumber CN = new ConnectedNumberImpl(parameterBody);
			super.addParameter(CN);
			break;
		case AccessTransport._PARAMETER_CODE:
			AccessTransport AT = new AccessTransportImpl(parameterBody);
			super.addParameter(AT);
			break;
		case AccessDeliveryInformation._PARAMETER_CODE:
			AccessDeliveryInformation ADI = new AccessDeliveryInformationImpl(parameterBody);
			super.addParameter(ADI);
			break;
		case GenericNotificationIndicator._PARAMETER_CODE:
			GenericNotificationIndicator GNI = new GenericNotificationIndicatorImpl(parameterBody);
			super.addParameter(GNI);
			break;
		case ParameterCompatibilityInformation._PARAMETER_CODE:
			ParameterCompatibilityInformation PCI = new ParameterCompatibilityInformationImpl(parameterBody);
			super.addParameter(PCI);
			break;
		case BackwardGVNS._PARAMETER_CODE:
			BackwardGVNS BGVNS = new BackwardGVNSImpl(parameterBody);
			super.addParameter(BGVNS);
			break;
		case CallHistoryInformation._PARAMETER_CODE:
			CallHistoryInformation CHI = new CallHistoryInformationImpl(parameterBody);
			super.addParameter(CHI);
			break;
		case GenericNumber._PARAMETER_CODE:
			GenericNumber GN = new GenericNumberImpl(parameterBody);
			super.addParameter(GN);
			break;
		case TransmissionMediumUsed._PARAMETER_CODE:
			TransmissionMediumUsed TMU = new TransmissionMediumUsedImpl(parameterBody);
			super.addParameter(TMU);
			break;
		case NetworkSpecificFacility._PARAMETER_CODE:
			NetworkSpecificFacility NSF = new NetworkSpecificFacilityImpl(parameterBody);
			super.addParameter(NSF);
			break;
		case RemoteOperations._PARAMETER_CODE:
			RemoteOperations RO = new RemoteOperationsImpl(parameterBody);
			super.addParameter(RO);
			break;
		case RedirectionNumber._PARAMETER_CODE:
			RedirectionNumber RN = new RedirectionNumberImpl(parameterBody);
			super.addParameter(RN);
			break;
		case ServiceActivation._PARAMETER_CODE:
			ServiceActivation SA = new ServiceActivationImpl(parameterBody);
			super.addParameter(SA);
			break;
		case EchoControlInformation._PARAMETER_CODE:
			EchoControlInformation ECI = new EchoControlInformationImpl(parameterBody);
			super.addParameter(ECI);
			break;
		case RedirectionNumberRestriction._PARAMETER_CODE:
			RedirectionNumberRestriction RNR = new RedirectionNumberRestrictionImpl(parameterBody);
			super.addParameter(RNR);
			break;
		case DisplayInformation._PARAMETER_CODE:
			DisplayInformation DI = new DisplayInformationImpl(parameterBody);
			super.addParameter(DI);
			break;
		case ConferenceTreatmentIndicators._PARAMETER_CODE:
			ConferenceTreatmentIndicators CTI = new ConferenceTreatmentIndicatorsImpl(parameterBody);
			super.addParameter(CTI);
			break;
		case ApplicationTransportParameter._PARAMETER_CODE:
			ApplicationTransportParameter ATP = new ApplicationTransportParameterImpl(parameterBody);
			super.addParameter(ATP);
			break;
		case PivotRoutingBackwardInformation._PARAMETER_CODE:
			PivotRoutingBackwardInformation PRBI = new PivotRoutingBackwardInformationImpl(parameterBody);
			super.addParameter(PRBI);
			break;
		case RedirectStatus._PARAMETER_CODE:
			RedirectStatus RS = new RedirectStatusImpl(parameterBody);
			super.addParameter(RS);
			break;
		default:
			throw new IllegalArgumentException("Unrecognized parameter code for optional part: " + parameterCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageImpl#getMessageType()
	 */
	@Override
	public MessageType getMessageType() {
		return this._MESSAGE_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.ss7.isup.ISUPMessageImpl#
	 * getNumberOfMandatoryVariableLengthParameters()
	 */
	@Override
	protected int getNumberOfMandatoryVariableLengthParameters() {

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageImpl#hasAllMandatoryParameters()
	 */
	@Override
	public boolean hasAllMandatoryParameters() {

		return true;
	}
	

	/* (non-Javadoc)
	 * @see org.mobicents.ss7.isup.impl.ISUPMessageImpl#optionalPartIsPossible()
	 */
	@Override
	protected boolean optionalPartIsPossible() {
		
		return true;
	}
}

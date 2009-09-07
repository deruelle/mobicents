/**
 * Start time:23:56:30 2009-09-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.impl;

import java.util.TreeMap;

import org.mobicents.ss7.isup.ParameterRangeInvalidException;
import org.mobicents.ss7.isup.impl.message.parameter.*;
import org.mobicents.ss7.isup.impl.message.parameter.accessTransport.AccessTransportImpl;
import org.mobicents.ss7.isup.message.CallProgressMessage;
import org.mobicents.ss7.isup.message.parameter.AccessDeliveryInformation;
import org.mobicents.ss7.isup.message.parameter.ApplicationTransportParameter;
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.BackwardGVNS;
import org.mobicents.ss7.isup.message.parameter.CCNRPossibleIndicator;
import org.mobicents.ss7.isup.message.parameter.CallDiversionInformation;
import org.mobicents.ss7.isup.message.parameter.CallHistoryInformation;
import org.mobicents.ss7.isup.message.parameter.CallReference;
import org.mobicents.ss7.isup.message.parameter.CallTransferNumber;
import org.mobicents.ss7.isup.message.parameter.CauseIndicators;
import org.mobicents.ss7.isup.message.parameter.ConferenceTreatmentIndicators;
import org.mobicents.ss7.isup.message.parameter.ConnectedNumber;
import org.mobicents.ss7.isup.message.parameter.EchoControlInformation;
import org.mobicents.ss7.isup.message.parameter.EventInformation;
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
import org.mobicents.ss7.isup.message.parameter.UIDActionIndicators;
import org.mobicents.ss7.isup.message.parameter.UserToUserIndicators;
import org.mobicents.ss7.isup.message.parameter.UserToUserInformation;
import org.mobicents.ss7.isup.message.parameter.accessTransport.AccessTransport;

/**
 * Start time:23:56:30 2009-09-06<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class CallProgressMessageImpl extends ISUPMessageImpl implements CallProgressMessage {

	public static final MessageTypeImpl _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_CPG);

	protected static final int _INDEX_F_MessageType = 0;
	protected static final int _INDEX_F_EventInformation = 1;

	protected static final int _INDEX_O_CauseIndicators = 0;
	protected static final int _INDEX_O_CallReference = 1;
	protected static final int _INDEX_O_BackwardCallIndicators = 2;
	protected static final int _INDEX_O_OptionalBackwardCallIndicators = 3;
	protected static final int _INDEX_O_AccessTransport = 4;
	protected static final int _INDEX_O_UserToUserIndicators = 5;
	protected static final int _INDEX_O_RedirectionNumber = 6;
	protected static final int _INDEX_O_UserToUserInformation = 7;
	protected static final int _INDEX_O_GenericNotificationIndicator = 8;
	protected static final int _INDEX_O_NetworkSpecificFacility = 9;
	protected static final int _INDEX_O_RemoteOperations = 10;
	protected static final int _INDEX_O_TransmissionMediumUsed = 11;
	protected static final int _INDEX_O_AccessDeliveryInformation = 12;
	protected static final int _INDEX_O_ParameterCompatibilityInformation = 13;
	protected static final int _INDEX_O_CallDiversionInformation = 14;
	protected static final int _INDEX_O_ServiceActivation = 15;
	protected static final int _INDEX_O_RedirectionNumberRestriction = 16;
	protected static final int _INDEX_O_CallTransferNumber = 17;
	protected static final int _INDEX_O_EchoControlInformation = 18;
	protected static final int _INDEX_O_ConnectedNumber = 19;
	protected static final int _INDEX_O_BackwardGVNS = 20;
	protected static final int _INDEX_O_GenericNumber = 21;
	protected static final int _INDEX_O_CallHistoryInformation = 22;
	protected static final int _INDEX_O_ConferenceTreatmentIndicators = 23;
	protected static final int _INDEX_O_UIDActionIndicators = 24;
	protected static final int _INDEX_O_ApplicationTransportParameter = 25;
	protected static final int _INDEX_O_CCNRPossibleIndicator = 26;
	protected static final int _INDEX_O_PivotRoutingBackwardInformation = 27;
	protected static final int _INDEX_O_RedirectStatus = 28;
	protected static final int _INDEX_O_EndOfOptionalParameters = 29;

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	CallProgressMessageImpl(Object source) {
		super(source);
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());

		super.o_Parameters.put(_INDEX_O_EndOfOptionalParameters, _END_OF_OPTIONAL_PARAMETERS);

		super.mandatoryCodes.add(EventInformation._PARAMETER_CODE);
		super.mandatoryCodeToIndex.put(EventInformation._PARAMETER_CODE, _INDEX_F_EventInformation);

		super.optionalCodes.add(_INDEX_O_CauseIndicators);
		super.optionalCodes.add(_INDEX_O_CallReference);
		super.optionalCodes.add(_INDEX_O_BackwardCallIndicators);
		super.optionalCodes.add(_INDEX_O_OptionalBackwardCallIndicators);
		super.optionalCodes.add(_INDEX_O_AccessTransport);
		super.optionalCodes.add(_INDEX_O_UserToUserIndicators);
		super.optionalCodes.add(_INDEX_O_RedirectionNumber);
		super.optionalCodes.add(_INDEX_O_UserToUserInformation);
		super.optionalCodes.add(_INDEX_O_GenericNotificationIndicator);
		super.optionalCodes.add(_INDEX_O_NetworkSpecificFacility);
		super.optionalCodes.add(_INDEX_O_RemoteOperations);
		super.optionalCodes.add(_INDEX_O_TransmissionMediumUsed);
		super.optionalCodes.add(_INDEX_O_AccessDeliveryInformation);
		super.optionalCodes.add(_INDEX_O_ParameterCompatibilityInformation);
		super.optionalCodes.add(_INDEX_O_CallDiversionInformation);
		super.optionalCodes.add(_INDEX_O_ServiceActivation);
		super.optionalCodes.add(_INDEX_O_RedirectionNumberRestriction);
		super.optionalCodes.add(_INDEX_O_CallTransferNumber);
		super.optionalCodes.add(_INDEX_O_EchoControlInformation);
		super.optionalCodes.add(_INDEX_O_ConnectedNumber);
		super.optionalCodes.add(_INDEX_O_BackwardGVNS);
		super.optionalCodes.add(_INDEX_O_GenericNumber);
		super.optionalCodes.add(_INDEX_O_CallHistoryInformation);
		super.optionalCodes.add(_INDEX_O_ConferenceTreatmentIndicators);
		super.optionalCodes.add(_INDEX_O_UIDActionIndicators);
		super.optionalCodes.add(_INDEX_O_ApplicationTransportParameter);
		super.optionalCodes.add(_INDEX_O_CCNRPossibleIndicator);
		super.optionalCodes.add(_INDEX_O_PivotRoutingBackwardInformation);
		super.optionalCodes.add(_INDEX_O_RedirectStatus);

		super.optionalCodeToIndex.put(CauseIndicators._PARAMETER_CODE, _INDEX_O_CauseIndicators);
		super.optionalCodeToIndex.put(CallReference._PARAMETER_CODE, _INDEX_O_CallReference);
		super.optionalCodeToIndex.put(BackwardCallIndicators._PARAMETER_CODE, _INDEX_O_BackwardCallIndicators);
		super.optionalCodeToIndex.put(OptionalBackwardCallIndicators._PARAMETER_CODE, _INDEX_O_OptionalBackwardCallIndicators);
		super.optionalCodeToIndex.put(AccessTransport._PARAMETER_CODE, _INDEX_O_AccessTransport);
		super.optionalCodeToIndex.put(UserToUserIndicators._PARAMETER_CODE, _INDEX_O_UserToUserIndicators);
		super.optionalCodeToIndex.put(RedirectionNumber._PARAMETER_CODE, _INDEX_O_RedirectionNumber);
		super.optionalCodeToIndex.put(UserToUserInformation._PARAMETER_CODE, _INDEX_O_UserToUserInformation);
		super.optionalCodeToIndex.put(GenericNotificationIndicator._PARAMETER_CODE, _INDEX_O_GenericNotificationIndicator);
		super.optionalCodeToIndex.put(NetworkSpecificFacility._PARAMETER_CODE, _INDEX_O_NetworkSpecificFacility);
		super.optionalCodeToIndex.put(RemoteOperations._PARAMETER_CODE, _INDEX_O_RemoteOperations);
		super.optionalCodeToIndex.put(TransmissionMediumUsed._PARAMETER_CODE, _INDEX_O_TransmissionMediumUsed);
		super.optionalCodeToIndex.put(AccessDeliveryInformation._PARAMETER_CODE, _INDEX_O_AccessDeliveryInformation);
		super.optionalCodeToIndex.put(ParameterCompatibilityInformation._PARAMETER_CODE, _INDEX_O_ParameterCompatibilityInformation);
		super.optionalCodeToIndex.put(CallDiversionInformation._PARAMETER_CODE, _INDEX_O_CallDiversionInformation);
		super.optionalCodeToIndex.put(ServiceActivation._PARAMETER_CODE, _INDEX_O_ServiceActivation);
		super.optionalCodeToIndex.put(RedirectionNumberRestriction._PARAMETER_CODE, _INDEX_O_RedirectionNumberRestriction);
		super.optionalCodeToIndex.put(CallTransferNumber._PARAMETER_CODE, _INDEX_O_CallTransferNumber);
		super.optionalCodeToIndex.put(EchoControlInformation._PARAMETER_CODE, _INDEX_O_EchoControlInformation);
		super.optionalCodeToIndex.put(ConnectedNumber._PARAMETER_CODE, _INDEX_O_ConnectedNumber);
		super.optionalCodeToIndex.put(BackwardGVNS._PARAMETER_CODE, _INDEX_O_BackwardGVNS);
		super.optionalCodeToIndex.put(GenericNumber._PARAMETER_CODE, _INDEX_O_GenericNumber);
		super.optionalCodeToIndex.put(CallHistoryInformation._PARAMETER_CODE, _INDEX_O_CallHistoryInformation);
		super.optionalCodeToIndex.put(ConferenceTreatmentIndicators._PARAMETER_CODE, _INDEX_O_ConferenceTreatmentIndicators);
		super.optionalCodeToIndex.put(UIDActionIndicators._PARAMETER_CODE, _INDEX_O_UIDActionIndicators);
		super.optionalCodeToIndex.put(ApplicationTransportParameter._PARAMETER_CODE, _INDEX_O_ApplicationTransportParameter);
		super.optionalCodeToIndex.put(CCNRPossibleIndicator._PARAMETER_CODE, _INDEX_O_CCNRPossibleIndicator);
		super.optionalCodeToIndex.put(PivotRoutingBackwardInformation._PARAMETER_CODE, _INDEX_O_PivotRoutingBackwardInformation);
		super.optionalCodeToIndex.put(RedirectStatus._PARAMETER_CODE, _INDEX_O_RedirectStatus);

	}

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 * @throws ParameterRangeInvalidException
	 */
	CallProgressMessageImpl(Object source, byte[] b) throws ParameterRangeInvalidException {
		this(source);
		decodeElement(b);
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

		if (b.length - index > 1) {
			int localIndex = index;
			// Message Type
			if (b[index] != this._MESSAGE_CODE_CPG) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_CPG);
			}
			index++;

			try {
				byte[] eventInformation = new byte[1];
				eventInformation[0] = b[index++];

				EventInformation _ei = new EventInformationImpl(eventInformation);
				this.addParameter(_ei);
			} catch (Exception e) {
				// AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse NatureOfConnectionIndicators due to: ", e);
			}

			return index - localIndex;
		} else {
			throw new ParameterRangeInvalidException("byte[] must have two octets");
		}
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
		case CauseIndicators._PARAMETER_CODE:
			break;

		case CallReference._PARAMETER_CODE:
			CallReference CR = new CallReferenceImpl(parameterBody);
			this.addParameter(CR);
			break;
		case BackwardCallIndicators._PARAMETER_CODE:
			BackwardCallIndicators BCMI = new BackwardCallIndicatorsImpl(parameterBody);
			super.addParameter(BCMI);
			break;
		case OptionalBackwardCallIndicators._PARAMETER_CODE:
			OptionalBackwardCallIndicators OBCI = new OptionalBackwardCallIndicatorsImpl(parameterBody);
			super.addParameter(OBCI);
			break;
		case AccessTransport._PARAMETER_CODE:
			AccessTransport AT = new AccessTransportImpl(parameterBody);
			super.addParameter(AT);
			break;
		case UserToUserIndicators._PARAMETER_CODE:
			UserToUserIndicators U2UI = new UserToUserIndicatorsImpl(parameterBody);
			super.addParameter(U2UI);
			break;
		case RedirectionNumber._PARAMETER_CODE:
			RedirectionNumber RN = new RedirectionNumberImpl(parameterBody);
			super.addParameter(RN);
			break;
		case UserToUserInformation._PARAMETER_CODE:
			UserToUserInformation U2UII = new UserToUserInformationImpl(parameterBody);
			super.addParameter(U2UII);
			break;
		case GenericNotificationIndicator._PARAMETER_CODE:
			GenericNotificationIndicator GNI = new GenericNotificationIndicatorImpl(parameterBody);
			super.addParameter(GNI);
			break;
		case NetworkSpecificFacility._PARAMETER_CODE:
			NetworkSpecificFacility NSF = new NetworkSpecificFacilityImpl(parameterBody);
			super.addParameter(NSF);
			break;
		case RemoteOperations._PARAMETER_CODE:
			RemoteOperations RO = new RemoteOperationsImpl(parameterBody);
			super.addParameter(RO);
			break;
		case TransmissionMediumUsed._PARAMETER_CODE:
			TransmissionMediumUsed TMU = new TransmissionMediumUsedImpl(parameterBody);
			super.addParameter(TMU);
			break;
		case AccessDeliveryInformation._PARAMETER_CODE:
			AccessDeliveryInformation ADI = new AccessDeliveryInformationImpl(parameterBody);
			super.addParameter(ADI);
			break;
		case ParameterCompatibilityInformation._PARAMETER_CODE:
			ParameterCompatibilityInformation PCI = new ParameterCompatibilityInformationImpl(parameterBody);
			super.addParameter(PCI);
			break;
		case CallDiversionInformation._PARAMETER_CODE:
			CallDiversionInformation CDI = new CallDiversionInformationImpl(parameterBody);
			super.addParameter(CDI);
			break;
		case ServiceActivation._PARAMETER_CODE:
			ServiceActivation SA = new ServiceActivationImpl(parameterBody);
			super.addParameter(SA);
			break;
		case RedirectionNumberRestriction._PARAMETER_CODE:
			RedirectionNumberRestriction RNR = new RedirectionNumberRestrictionImpl(parameterBody);
			super.addParameter(RNR);
			break;
		case CallTransferNumber._PARAMETER_CODE:
			CallTransferNumber CTR = new CallTransferNumberImpl(parameterBody);
			super.addParameter(CTR);
			break;
		case EchoControlInformation._PARAMETER_CODE:
			EchoControlInformation ECI = new EchoControlInformationImpl(parameterBody);
			super.addParameter(ECI);
			break;
		case ConnectedNumber._PARAMETER_CODE:
			ConnectedNumber CN = new ConnectedNumberImpl(parameterBody);
			super.addParameter(CN);
			break;
		case BackwardGVNS._PARAMETER_CODE:
			BackwardGVNS BGVNS = new BackwardGVNSImpl(parameterBody);
			super.addParameter(BGVNS);
			break;
		case GenericNumber._PARAMETER_CODE:
			GenericNumber GN = new GenericNumberImpl(parameterBody);
			super.addParameter(GN);
			break;
		case CallHistoryInformation._PARAMETER_CODE:
			CallHistoryInformation CHI = new CallHistoryInformationImpl(parameterBody);
			super.addParameter(CHI);
			break;
		case ConferenceTreatmentIndicators._PARAMETER_CODE:
			ConferenceTreatmentIndicators CTI = new ConferenceTreatmentIndicatorsImpl(parameterBody);
			super.addParameter(CTI);
			break;
		case UIDActionIndicators._PARAMETER_CODE:
			UIDActionIndicators UIDA = new UIDActionIndicatorsImpl(parameterBody);
			break;
		case ApplicationTransportParameter._PARAMETER_CODE:
			ApplicationTransportParameter ATP = new ApplicationTransportParameterImpl(parameterBody);
			super.addParameter(ATP);
			break;
		case CCNRPossibleIndicator._PARAMETER_CODE:
			CCNRPossibleIndicator CCNR = new CCNRPossibleIndicatorImpl(parameterBody);
			super.addParameter(CCNR);
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
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageImpl#hasAllMandatoryParameters()
	 */
	@Override
	public boolean hasAllMandatoryParameters() {
		// TODO Auto-generated method stub
		return false;
	}

}

/**
 * Start time:00:07:25 2009-09-07<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.impl;

import java.util.TreeMap;

import org.mobicents.ss7.isup.ParameterRangeInvalidException;
import org.mobicents.ss7.isup.impl.message.parameter.CircuitGroupSuperVisionMessageTypeImpl;
import org.mobicents.ss7.isup.impl.message.parameter.MessageTypeImpl;
import org.mobicents.ss7.isup.impl.message.parameter.RangeAndStatusImpl;
import org.mobicents.ss7.isup.message.CircuitGroupBlockingMessage;
import org.mobicents.ss7.isup.message.parameter.AccessDeliveryInformation;
import org.mobicents.ss7.isup.message.parameter.ApplicationTransportParameter;
import org.mobicents.ss7.isup.message.parameter.BackwardCallIndicators;
import org.mobicents.ss7.isup.message.parameter.BackwardGVNS;
import org.mobicents.ss7.isup.message.parameter.CallHistoryInformation;
import org.mobicents.ss7.isup.message.parameter.CallReference;
import org.mobicents.ss7.isup.message.parameter.CircuitGroupSuperVisionMessageType;
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
import org.mobicents.ss7.isup.message.parameter.RangeAndStatus;
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
 * Start time:00:07:25 2009-09-07<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class CircuitGroupBlockingMessageImpl extends ISUPMessageImpl implements CircuitGroupBlockingMessage {

	public static final MessageType _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_CGB);

	protected static final int _INDEX_F_MessageType = 0;
	protected static final int _INDEX_F_CircuitGroupSupervisionMessageType = 1;

	protected static final int _INDEX_V_RangeAndStatus = 0;

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	CircuitGroupBlockingMessageImpl(Object source, byte[] b) throws ParameterRangeInvalidException {
		this(source);
		decodeElement(b);

	}

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	CircuitGroupBlockingMessageImpl(Object source) {
		super(source);
		// FIXME: this is bad, we always fill this, we shouyld move that
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());

		super.mandatoryCodes.add(CircuitGroupSuperVisionMessageType._PARAMETER_CODE);
		super.mandatoryCodeToIndex.put(CircuitGroupSuperVisionMessageType._PARAMETER_CODE, _INDEX_F_CircuitGroupSupervisionMessageType);

		super.mandatoryVariableCodes.add(RangeAndStatus._PARAMETER_CODE);
		super.mandatoryVariableCodeToIndex.put(RangeAndStatus._PARAMETER_CODE, _INDEX_V_RangeAndStatus);

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
		if (b.length - index > 2) {

			// Message Type
			if (b[index] != this._MESSAGE_CODE_CGB) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_CGB);
			}
			index++;
			CircuitGroupSuperVisionMessageType cgsvmt = new CircuitGroupSuperVisionMessageTypeImpl(new byte[] { b[index] });
			super.addParameter(cgsvmt);
			index++;
			return index - localIndex;
		} else {
			throw new IllegalArgumentException("byte[] must have atleast two octets");
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
		switch (parameterIndex) {
		case _INDEX_V_RangeAndStatus:
			RangeAndStatus ras = new RangeAndStatusImpl(parameterBody);
			this.addParameter(ras);
			break;
		default:
			throw new IllegalArgumentException("Unrecognized parameter index for mandatory variable part, index: " + parameterIndex);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageImpl#decodeOptionalBody(byte[],
	 * byte)
	 */
	@Override
	protected void decodeOptionalBody(byte[] parameterBody, byte parameterCode) throws ParameterRangeInvalidException {
		throw new ParameterRangeInvalidException("This message does not support optional parameters");

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

		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageImpl#hasAllMandatoryParameters()
	 */
	@Override
	public boolean hasAllMandatoryParameters() {
		return super.f_Parameters.get(_INDEX_F_CircuitGroupSupervisionMessageType) != null && super.v_Parameters.get(_INDEX_V_RangeAndStatus) != null;
	}

	@Override
	protected boolean optionalPartIsPossible() {

		return false;
	}

}

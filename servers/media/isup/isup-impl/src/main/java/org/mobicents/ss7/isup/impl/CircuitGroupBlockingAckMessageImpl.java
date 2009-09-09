/**
 * Start time:00:08:25 2009-09-07<br>
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
import org.mobicents.ss7.isup.message.CircuitGroupBlockingAckMessage;
import org.mobicents.ss7.isup.message.parameter.CircuitGroupSuperVisionMessageType;
import org.mobicents.ss7.isup.message.parameter.ISUPParameter;
import org.mobicents.ss7.isup.message.parameter.MessageType;
import org.mobicents.ss7.isup.message.parameter.RangeAndStatus;

/**
 * Start time:00:08:25 2009-09-07<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public class CircuitGroupBlockingAckMessageImpl extends ISUPMessageImpl implements CircuitGroupBlockingAckMessage {

	public static final MessageType _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_CGBA);
	private static final int _MANDATORY_VAR_COUNT = 1;
	
	protected static final int _INDEX_F_MessageType = 0;
	protected static final int _INDEX_F_CircuitGroupSupervisionMessageType = 1;

	protected static final int _INDEX_V_RangeAndStatus = 0;

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	CircuitGroupBlockingAckMessageImpl(Object source, byte[] b) throws ParameterRangeInvalidException {
		this(source);
		decodeElement(b);

	}

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	CircuitGroupBlockingAckMessageImpl(Object source) {
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
			if (b[index] != this._MESSAGE_CODE_CGBA) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_CGBA);
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

		return _MANDATORY_VAR_COUNT;
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

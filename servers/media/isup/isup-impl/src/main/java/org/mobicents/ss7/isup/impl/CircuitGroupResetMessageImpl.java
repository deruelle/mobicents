/**
 * Start time:00:09:01 2009-09-07<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.impl;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mobicents.ss7.isup.ParameterRangeInvalidException;
import org.mobicents.ss7.isup.impl.message.parameter.CircuitIdentificationCodeImpl;
import org.mobicents.ss7.isup.impl.message.parameter.MessageTypeImpl;
import org.mobicents.ss7.isup.impl.message.parameter.RangeAndStatusImpl;
import org.mobicents.ss7.isup.message.CircuitGroupResetMessage;
import org.mobicents.ss7.isup.message.parameter.ISUPParameter;
import org.mobicents.ss7.isup.message.parameter.MessageType;
import org.mobicents.ss7.isup.message.parameter.RangeAndStatus;

/**
 * Start time:00:09:01 2009-09-07<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class CircuitGroupResetMessageImpl extends ISUPMessageImpl implements CircuitGroupResetMessage {

	public static final MessageType _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_GRS);
	private static final int _MANDATORY_VAR_COUNT = 1;
	
	static final int _INDEX_F_MessageType = 0;
	static final int _INDEX_V_RangeAndStatus = 0;

	CircuitGroupResetMessageImpl(Object source, byte[] b, Set<Integer> mandatoryCodes, Set<Integer> mandatoryVariableCodes, Set<Integer> optionalCodes, Map<Integer, Integer> mandatoryCode2Index,
			Map<Integer, Integer> mandatoryVariableCode2Index, Map<Integer, Integer> optionalCode2Index) throws ParameterRangeInvalidException {
		this(source, mandatoryCodes, mandatoryVariableCodes, optionalCodes, mandatoryCode2Index, mandatoryVariableCode2Index, optionalCode2Index);
		decodeElement(b);

	}

	CircuitGroupResetMessageImpl(Object source, Set<Integer> mandatoryCodes, Set<Integer> mandatoryVariableCodes, Set<Integer> optionalCodes, Map<Integer, Integer> mandatoryCode2Index,
			Map<Integer, Integer> mandatoryVariableCode2Index, Map<Integer, Integer> optionalCode2Index) {
		super(source, mandatoryCodes, mandatoryVariableCodes, optionalCodes, mandatoryCode2Index, mandatoryVariableCode2Index, optionalCode2Index);

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());

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

			try {
				byte[] cic = new byte[2];
				cic[0] = b[index++];
				cic[1] = b[index++];
				super.cic = new CircuitIdentificationCodeImpl();
				super.cic.decodeElement(cic);

			} catch (Exception e) {
				// AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse CircuitIdentificationCode due to: ", e);
			}
			try {
				// Message Type
				if (b[index] != this._MESSAGE_CODE_GRS) {
					throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_GRS);
				}
			} catch (Exception e) {
				// AIOOBE or IllegalArg
				throw new ParameterRangeInvalidException("Failed to parse MessageCode due to: ", e);
			}
			index++;

			return index - localIndex;
		} else {
			throw new IllegalArgumentException("byte[] must have atleast three octets");
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
			// FIXME: this should have only one byte!!!?
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
		return super.v_Parameters.get(_INDEX_V_RangeAndStatus) != null;
	}

	@Override
	protected boolean optionalPartIsPossible() {

		return false;
	}

}

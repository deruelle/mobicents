/**
 * Start time:00:09:10 2009-09-07<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.impl;

import java.util.TreeMap;

import org.mobicents.ss7.isup.ParameterRangeInvalidException;
import org.mobicents.ss7.isup.impl.message.parameter.CircuitStateIndicatorImpl;
import org.mobicents.ss7.isup.impl.message.parameter.MessageTypeImpl;
import org.mobicents.ss7.isup.impl.message.parameter.RangeAndStatusImpl;
import org.mobicents.ss7.isup.message.CircuitGroupQueryResponseMessage;
import org.mobicents.ss7.isup.message.parameter.CircuitStateIndicator;
import org.mobicents.ss7.isup.message.parameter.ISUPParameter;
import org.mobicents.ss7.isup.message.parameter.MessageType;
import org.mobicents.ss7.isup.message.parameter.RangeAndStatus;

/**
 * Start time:00:09:10 2009-09-07<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public class CircuitGroupQueryResponseMessageImpl extends ISUPMessageImpl implements CircuitGroupQueryResponseMessage {

	public static final MessageType _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_CQR);

	protected static final int _INDEX_F_MessageType = 0;
	protected static final int _INDEX_V_RangeAndStatus = 0;
	protected static final int _INDEX_V_CircuitStateIndicator = 1;
	

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	CircuitGroupQueryResponseMessageImpl(Object source, byte[] b) throws ParameterRangeInvalidException {
		this(source);
		decodeElement(b);

	}

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	CircuitGroupQueryResponseMessageImpl(Object source) {
		super(source);
		// FIXME: this is bad, we always fill this, we shouyld move that
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());

		

		super.mandatoryVariableCodes.add(RangeAndStatus._PARAMETER_CODE);
		super.mandatoryVariableCodes.add(CircuitStateIndicator._PARAMETER_CODE);
		super.mandatoryVariableCodeToIndex.put(RangeAndStatus._PARAMETER_CODE, _INDEX_V_RangeAndStatus);
		super.mandatoryVariableCodeToIndex.put(CircuitStateIndicator._PARAMETER_CODE, _INDEX_V_CircuitStateIndicator);

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
			if (b[index] != this._MESSAGE_CODE_CQR) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_CQR);
			}
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
			//FIXME: this should have only one byte!!!?
			RangeAndStatus ras = new RangeAndStatusImpl(parameterBody);
			this.addParameter(ras);
			break;
		case _INDEX_V_CircuitStateIndicator:
			//FIXME: this should have only one byte!!!?
			CircuitStateIndicator csi = new CircuitStateIndicatorImpl(parameterBody);
			this.addParameter(csi);
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

		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.ss7.isup.ISUPMessageImpl#hasAllMandatoryParameters()
	 */
	@Override
	public boolean hasAllMandatoryParameters() {
		return  super.v_Parameters.get(_INDEX_V_RangeAndStatus) != null;
	}

	@Override
	protected boolean optionalPartIsPossible() {

		return false;
	}

}

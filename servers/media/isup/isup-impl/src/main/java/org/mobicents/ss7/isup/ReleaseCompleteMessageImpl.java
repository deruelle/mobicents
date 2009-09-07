/**
 * Start time:08:20:34 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.ss7.isup;

import java.util.TreeMap;

import org.mobicents.ss7.isup.message.parameter.CauseIndicatorsImpl;
import org.mobicents.ss7.isup.message.parameter.MessageTypeImpl;
import org.mobicents.ss7.isup.message.ReleaseCompleteMessage;
import org.mobicents.ss7.isup.message.parameter.CauseIndicators;
import org.mobicents.ss7.isup.message.parameter.ISUPParameter;

/**
 * Start time:08:20:34 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
class ReleaseCompleteMessageImpl extends ISUPMessageImpl implements ReleaseCompleteMessage {

	public static final MessageTypeImpl _MESSAGE_TYPE = new MessageTypeImpl(_MESSAGE_CODE_RLC);
	private static final int _MANDATORY_VAR_COUNT = 0;

	// mandatory fixed L
	protected static final int _INDEX_F_MessageType = 0;
	// mandatory variable L
	// optional O
	protected static final int _INDEX_O_CauseIndicators = 0;
	protected static final int _INDEX_O_EndOfOptionalParameters = 1;

	/**
	 * 
	 * @param source
	 * @throws ParameterRangeInvalidException
	 */
	ReleaseCompleteMessageImpl(Object source, byte[] b) throws ParameterRangeInvalidException {
		this(source);

		decodeElement(b);

	}

	/**
	 * @throws ParameterRangeInvalidException
	 * 
	 */
	ReleaseCompleteMessageImpl(Object source) {

		super(source);
		super.f_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.v_Parameters = new TreeMap<Integer, ISUPParameter>();
		super.o_Parameters = new TreeMap<Integer, ISUPParameter>();

		super.f_Parameters.put(_INDEX_F_MessageType, this.getMessageType());
		super.o_Parameters.put(_INDEX_O_EndOfOptionalParameters, _END_OF_OPTIONAL_PARAMETERS);

		super.optionalCodes.add(CauseIndicators._PARAMETER_CODE);
		super.optionalCodeToIndex.put(CauseIndicators._PARAMETER_CODE, _INDEX_O_CauseIndicators);
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
			if (b[index] != this._MESSAGE_CODE_RLC) {
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE_RLC + " it is: " + b[index] + ", index:" + index);
			}
			index++;

			return index - localIndex;
		} else {
			throw new ParameterRangeInvalidException("byte[] must have atleast two octets");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.messages.ISUPMessage#decodeMandatoryVariableBody(byte
	 * [], int)
	 */
	@Override
	protected void decodeMandatoryVariableBody(byte[] parameterBody, int parameterIndex) throws ParameterRangeInvalidException {
		throw new ParameterRangeInvalidException("This message has no mandatory parameters, unknown parameter index: " + parameterIndex + ", body: " + Utils.toHex(parameterBody));

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
		case _INDEX_O_CauseIndicators:
			CauseIndicatorsImpl cpn = new CauseIndicatorsImpl(parameterBody);
			this.setCauseIndicators(cpn);
			break;
		default:
			throw new ParameterRangeInvalidException("Unrecognized parameter index for optional part: " + parameterCode);
		}

	}

	public CauseIndicators getCauseIndicators() {
		return (CauseIndicatorsImpl) super.v_Parameters.get(_INDEX_O_CauseIndicators);
	}

	public void setCauseIndicators(CauseIndicators v) {
		super.v_Parameters.put(_INDEX_O_CauseIndicators, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.messages.ISUPMessage#getMessageType()
	 */
	@Override
	public MessageTypeImpl getMessageType() {
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

		return true;
	}

}

/**
 * Start time:08:20:34 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages;

import java.util.TreeMap;

import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.Utils;
import org.mobicents.isup.parameters.CallingPartyCategory;
import org.mobicents.isup.parameters.CauseIndicators;
import org.mobicents.isup.parameters.ForwardCallIndicators;
import org.mobicents.isup.parameters.ISUPParameter;
import org.mobicents.isup.parameters.MessageType;
import org.mobicents.isup.parameters.NatureOfConnectionIndicators;
import org.mobicents.isup.parameters.TransmissionMediumRequirement;

/**
 * Start time:08:20:34 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ReleaseCompleteMessage extends ISUPMessage {

	public static final int _MESSAGE_CODE = 0x10;
	public static final MessageType _MESSAGE_TYPE = new MessageType(_MESSAGE_CODE);
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
	public ReleaseCompleteMessage(Object source, byte[] b) throws ParameterRangeInvalidException {
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
	public ReleaseCompleteMessage() {
		// TODO Auto-generated constructor stub
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
				throw new ParameterRangeInvalidException("Message code is not: " + this._MESSAGE_CODE+" it is: "+b[index]+", index:"+index);
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
			CauseIndicators cpn = new CauseIndicators(parameterBody);
			this.setCauseIndicators(cpn);
			break;
		default:
			throw new ParameterRangeInvalidException("Unrecognized parameter index for optional part: " + parameterCode);
		}

	}

	public CauseIndicators getCauseIndicators() {
		return (CauseIndicators) super.v_Parameters.get(_INDEX_O_CauseIndicators);
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
		
		return true;
	}

}

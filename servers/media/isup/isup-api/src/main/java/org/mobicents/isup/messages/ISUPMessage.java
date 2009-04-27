/**
 * Start time:14:09:04 2009-04-20<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.EndOfOptionalParameters;
import org.mobicents.isup.parameters.ISUPParameter;
import org.mobicents.isup.parameters.MessageType;
import org.mobicents.isup.parameters.SignalingPointCode;

/**
 * Start time:14:09:04 2009-04-20<br>
 * Project: mobicents-isup-stack<br>
 * This is super message class for all messages that we have. It defines some
 * methods that need to be implemented
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
abstract class ISUPMessage implements ISUPComponent {

	/**
	 * To use one when encoding, created, possibly when decoding
	 */
	protected static final EndOfOptionalParameters _END_OF_OPTIONAL_PARAMETERS = new EndOfOptionalParameters();

	protected static final Logger logger = Logger.getLogger(ISUPMessage.class);
	/**
	 * F = mandatory fixed length parameter;<br>
	 * for type F parameters: the length, in octets, of the parameter content;
	 */
	protected Map<Integer,ISUPParameter> f_Parameters = null;
	/**
	 * V = mandatory variable length parameter;<br>
	 * for type V parameters: the length, in octets, of the length indicator and
	 * of the parameter content. The minimum and the maximum length are
	 * indicated;
	 */
	protected Map<Integer,ISUPParameter> v_Parameters = null;
	/**
	 * O = optional parameter of fixed or variable length; for type O
	 * parameters: the length, in octets, of the parameter name, length
	 * indicator and parameter content. For variable length parameters the
	 * minimum and maximum length is indicated.
	 */
	protected Map<Integer,ISUPParameter> o_Parameters = null;
	
	protected SignalingPointCode destinationPointCode, originatingPointCode;

	protected Object source = null;
	protected byte signalingLinkSelection = 0;
	protected int circuitIdentificationCode = 0;
	protected byte congestionPriority = 0;

	public ISUPMessage(Object source, byte[] b) {
		super();
		this.source = source;

	}

	/**
	 * 
	 * @param source
	 *            - source of this event which is either the Listener or the
	 *            Provider
	 * @param dpc
	 *            - destination point code in the event
	 * @param opc
	 *            - origination point code in the event
	 * @param sls
	 *            - signaling link selection in the event
	 * @param cic
	 *            - circuit identification code in the event
	 * @param congestionPriority
	 *            - priority of the ISUP message
	 * @throws ParameterRangeInvalidException
	 *             - thrown when value is out of range
	 */
	protected ISUPMessage(java.lang.Object source, SignalingPointCode dpc, SignalingPointCode opc, byte sls, int cic, byte congestionPriority) throws ParameterRangeInvalidException {

		if (source == null || dpc == null || opc == null) {
			throw new ParameterRangeInvalidException("Parameters must not be null");
		}
		this.source = source;
		this.signalingLinkSelection = sls;
		this.circuitIdentificationCode = cic;
		this.congestionPriority = congestionPriority;
		this.destinationPointCode = dpc;
		this.originatingPointCode = opc;
		prepareMessage();
	}

	/**
	 * @return <ul>
	 *         <li><b>true</b> - if all requried parameters are set</li>
	 *         <li><b>false</b> - otherwise</li>
	 *         </ul>
	 */
	public abstract boolean hasAllMandatoryParameters();

	/**
	 * Returns message code. See Q.763 Table 4. It simply return value of static
	 * constant - _MESSAGE_TYPE, where value of parameter is value _MESSAGE_CODE
	 * 
	 * @return
	 */
	public abstract MessageType getMessageType();

	/**
	 * Gets the destination point code. The destination point code is a
	 * signaling point code, the format for the signaling point code is
	 * described in IsupUserAddress class. Refer to Signaling Point Code for a
	 * full description of destination point code.
	 * 
	 * @return the destination point code in an ISUP event
	 */
	public SignalingPointCode getDestinationPointCode() {
		return destinationPointCode;
	}

	/**
	 * Gets the origination point code. The origination point code is a
	 * signaling point code, the format for the signaling point code is
	 * described in IsupUserAddress class. Refer to Signaling Point Code for a
	 * full description of origination point code.
	 * 
	 * @return the origination point code in an ISUP event
	 */
	public SignalingPointCode getOriginatingPointCode() {
		return originatingPointCode;
	}

	public Object getSource() {
		return source;
	}

	/**
	 * Gets the signaling link selection field in the ISUP event. The SLS has a
	 * range of 0-15 in ITU and 0-127 in ANSI.
	 * 
	 * @return the Signaling Link Selection in an ISUP event.
	 */
	public byte getSignalingLinkSelection() {
		return signalingLinkSelection;
	}

	/**
	 * Gets the Circuit Identification Code Circuit Identification Code
	 * identifies a voice circuit between two nodes in the SS7 network. Refer to
	 * CIC range for a full description.
	 * 
	 * @return
	 */
	public int getCircuitIdentificationCode() {
		return circuitIdentificationCode;
	}

	/**
	 * Gets the congestion priority value of the message. The congestion
	 * priority indicates the priority with which the ISUP message is to be
	 * given to MTP3 in the MTP TRANSFER primitive call. The congestion priority
	 * field will be used by MTP3 during congestion control procedures wherein
	 * only those User Part Messages that have greater congestion priority than
	 * the congestion level reported for a link/signaling point code, are sent
	 * to the network, while the ISUP messages with a priority less than the
	 * congestion level are discarded by MTP3. The congestion control procedure
	 * is only specified for national networks and it is an optional procedure.
	 * Hence this field may be used only when the Network Indicator indicates a
	 * national network. i.e. when the network indicator is 2 or 3. For
	 * international network indicator values, congestion priority field is to
	 * be coded as 0. This field also does not have any signficance at the
	 * incoming end. So the priority field in an IsupEvent from Provider to
	 * Listener has no relevance.
	 * 
	 * @return the congestion priority of the message being sent to MTP3. The
	 *         value ranges from 0 to 3, 0 being the lowest priority and 3 the
	 *         highest.
	 */
	public byte getCongestionPriority() {
		return congestionPriority;
	}

	/**
	 * Sets the destination point code. The destination point code is a
	 * signaling point code, the format of the signaling point is described in
	 * the IsupUserAddress class. Refer to Signaling Point Code for a full
	 * description of destination point code.
	 * 
	 * @throws ParameterRangeInvalidException
	 * 
	 * @param destinationPointCode
	 */
	public void setDestinationPointCode(SignalingPointCode destinationPointCode) throws ParameterRangeInvalidException {
		this.destinationPointCode = destinationPointCode;
	}

	/**
	 * Sets the origination point code. The origination point code is a
	 * signaling point code, the format of the signaling point is described in
	 * the IsupUserAddress class. Refer to Signaling Point Code for a full
	 * description of origination point code.
	 * 
	 * @param originatingPointCode
	 *            - the origination point code in an ISUP event
	 * @throws ParameterRangeInvalidException
	 */
	public void setOriginatingPointCode(SignalingPointCode originatingPointCode) throws ParameterRangeInvalidException {
		this.originatingPointCode = originatingPointCode;
	}

	/**
	 * Sets the signaling link selection field in the ISUP event. The SLS has a
	 * range of 0-15 in ITU and 0-31 in ANSI isls the SLS value in the ISUP
	 * event
	 * 
	 * @param signalingLinkSelection
	 * @throws ParameterRangeInvalidException
	 */
	public void setSignalingLinkSelection(byte signalingLinkSelection) throws ParameterRangeInvalidException {
		this.signalingLinkSelection = signalingLinkSelection;
	}

	/**
	 * Gets the Circuit Identification Code Circuit Identification Code
	 * identifies a voice circuit between two nodes in the SS7 network. Refer to
	 * CIC range for a full description.
	 * 
	 * @param circuitIdentificationCode
	 * @throws ParameterRangeInvalidException
	 */
	public void setCircuitIdentificationCode(int circuitIdentificationCode) throws ParameterRangeInvalidException {
		this.circuitIdentificationCode = circuitIdentificationCode;
	}

	/**
	 * sets the congestion priority value of the ISUP message being sent to
	 * MTP3. Refer to getMessagePriority for more detail on congestion priority
	 * field of IsupEvent class.
	 * 
	 * @param congestionPriority
	 *            - congestion priority of the ISUP message. Value ranges from 0
	 *            to 3, 0 being the lowest priority and 3 the highest.
	 * @throws ParameterRangeInvalidException
	 */
	public void setCongestionPriority(byte congestionPriority) throws ParameterRangeInvalidException {
		this.congestionPriority = congestionPriority;
	}

	protected void prepareMessage() {
		if (this.o_Parameters.size() > 0)
			this.o_Parameters.put(this.o_Parameters.size(), new EndOfOptionalParameters());

	}

	public byte[] encodeElement() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// akward :)
		this.encodeElement(bos);
		return bos.toByteArray();
	}

	public int encodeElement(ByteArrayOutputStream bos) throws IOException {

		// FIME: add encoding of routing part

		//bos.write(this.circuitIdentificationCode);
		
		boolean optionalPresent = this.o_Parameters.size() > 1;
		this.encodeMandatoryParameters(f_Parameters, bos);
		this.encodeMandatoryVariableParameters(v_Parameters, bos, optionalPresent);
		if (optionalPresent) {
			this.encodeOptionalParameters(o_Parameters, bos);
		}

		return bos.size();
	}

	// NOTE: those methods are more or less generic.
	protected void encodeMandatoryParameters(Map<Integer,ISUPParameter> parameters, ByteArrayOutputStream bos) throws IOException {
		// 1.5 Mandatory fixed part
		// Those parameters that are mandatory and of fixed length for a
		// particular message type will be
		// contained in the mandatory fixed part. The position, length and order
		// of the parameters is uniquely
		// defined by the message type; thus, the names of the parameters and
		// the length indicators are not
		// included in the message.
		// FIXME: check if there are params of vadiable length here.
		for (ISUPParameter p : parameters.values())
		{
			System.err.println("Encode: "+p);
			p.encodeElement(bos);
		}
	}

	/**
	 * takes care of endoding parameters - poniters and actual parameters.
	 * 
	 * @param parameters
	 *            - list of parameters
	 * @param bos
	 *            - output
	 * @param isOptionalPartPresent
	 *            - if <b>true</b> this will encode pointer to point for start
	 *            of optional part, otherwise it will encode this octet as zeros
	 * @throws IOException
	 */
	protected void encodeMandatoryVariableParameters(Map<Integer,ISUPParameter> parameters, ByteArrayOutputStream bos, boolean isOptionalPartPresent) throws IOException {
		byte[] pointers = new byte[parameters.size() + 1];
		if (parameters != null && parameters.size() == 0) {
			// This happens if there is no variable mandatory part :)
			if (isOptionalPartPresent) {
				pointers = new byte[] { 0x01 };
			} else {
				pointers = new byte[] { 0x00 };
			}
			// Nothing else goes there.
			bos.write(pointers);
			
		} else {
			ByteArrayOutputStream parametersBodyBOS = new ByteArrayOutputStream();
			byte lastParameterLength = 0;
			byte currentParameterLength = 0;
			for (int index = 0; index < parameters.size(); index++) {
				ISUPParameter p = parameters.get(index);
				byte[] body = p.encodeElement();
				currentParameterLength = (byte) body.length;
				if (body.length > 255) {
					// FIXME: is this check valid?
					throw new IOException("Length of body must not be greater than one octet - 255 ");
				}
				if (index == 0) {
					lastParameterLength = currentParameterLength;

					// FIXME: add check here?
					pointers[index] = (byte) (parameters.size() + 1);
				} else {
					pointers[index] = (byte) (pointers[index - 1] + lastParameterLength);
				}

				parametersBodyBOS.write(currentParameterLength);
				parametersBodyBOS.write(body);
			}

			if (!isOptionalPartPresent) {
				pointers = new byte[] { 0x00 };
			} else {
				pointers[pointers.length - 1] = (byte) (pointers[pointers.length - 2] + lastParameterLength);
			}

			bos.write(pointers);
			bos.write(parametersBodyBOS.toByteArray());

		}

	}

	/**
	 * This method must be called ONLY in case there are optional params. This
	 * implies ISUPMessage.o_Parameters.size()>1 !!!
	 * 
	 * @param parameters
	 * @param bos
	 * @throws IOException
	 */
	protected void encodeOptionalParameters(Map<Integer,ISUPParameter> parameters, ByteArrayOutputStream bos) throws IOException {

		// NOTE: parameters MUST have as last endOfOptionalParametersParameter
		for (ISUPParameter p : parameters.values()) {
			System.err.println("ENCODE O: "+p);
			if(p==null)
				continue;
			byte[] b = p.encodeElement();
			// FIXME: this can be slow, maybe we shoudl remove that, and code
			// this explicitly?
			if (b.length > 255) {
				throw new IOException("Parameter length is over 255: " + p);
			}
			if (!(p instanceof EndOfOptionalParameters)) {
				bos.write(p.getCode());

				bos.write(b.length);
			}
			bos.write(b);
		}

	}

	public int decodeElement(byte[] b) throws IllegalArgumentException {
		int index = 0;
		// FIME: add decoding of routing part

		// this.circuitIdentificationCode = b[index++];
		// This is for message type code
		// index++;
		index += this.decodeMandatoryParameters(b, index);
		index += this.decodeMandatoryVariableParameters(b, index);
		index += this.decodeOptionalParameters(b, index);

		return index;
	}

	protected abstract int decodeMandatoryParameters(byte[] b, int index) throws IllegalArgumentException;

	protected abstract int decodeMandatoryVariableParameters(byte[] b, int index) throws IllegalArgumentException;

	// FIXME: this possibly could be generic?
	protected abstract int decodeOptionalParameters(byte[] b, int index) throws IllegalArgumentException;
}

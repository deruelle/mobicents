package org.mobicents.ss7.isup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mobicents.ss7.isup.message.ISUPMessage;

import org.mobicents.ss7.isup.message.parameter.EndOfOptionalParametersImpl;
import org.mobicents.ss7.isup.message.parameter.ISUPParameter;
import org.mobicents.ss7.isup.message.parameter.MessageType;

/**
 * Start time:14:09:04 2009-04-20<br>
 * Project: mobicents-isup-stack<br>
 * This is super message class for all messages that we have. It defines some
 * methods that need to be implemented
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
abstract class ISUPMessageImpl implements ISUPMessage {

	/**
	 * To use one when encoding, created, possibly when decoding
	 */
	protected static final EndOfOptionalParametersImpl _END_OF_OPTIONAL_PARAMETERS = new EndOfOptionalParametersImpl();

	protected static final Logger logger = Logger.getLogger(ISUPMessageImpl.class);
	/**
	 * F = mandatory fixed length parameter;<br>
	 * for type F parameters: the length, in octets, of the parameter content;
	 */
	protected Map<Integer, ISUPParameter> f_Parameters;
	/**
	 * V = mandatory variable length parameter;<br>
	 * for type V parameters: the length, in octets, of the length indicator and
	 * of the parameter content. The minimum and the maximum length are
	 * indicated;
	 */
	protected Map<Integer, ISUPParameter> v_Parameters;
	/**
	 * O = optional parameter of fixed or variable length; for type O
	 * parameters: the length, in octets, of the parameter name, length
	 * indicator and parameter content. For variable length parameters the
	 * minimum and maximum length is indicated.
	 */
	protected Map<Integer, ISUPParameter> o_Parameters;

	// magic
	protected Set<Integer> mandatoryCodes;
	protected Set<Integer> mandatoryVariableCodes;
	protected Set<Integer> optionalCodes;

	protected Map<Integer, Integer> mandatoryCodeToIndex;
	protected Map<Integer, Integer> mandatoryVariableCodeToIndex;
	protected Map<Integer, Integer> optionalCodeToIndex;

	protected Object source;

	public ISUPMessageImpl(Object source)  {
		super();
		this.source = source;

		mandatoryCodes = new HashSet<Integer>();
		mandatoryVariableCodes = new HashSet<Integer>();
		optionalCodes = new HashSet<Integer>();
		mandatoryCodeToIndex = new HashMap<Integer, Integer>();
		mandatoryVariableCodeToIndex = new HashMap<Integer, Integer>();
		optionalCodeToIndex = new HashMap<Integer, Integer>();

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
	 */
	protected ISUPMessageImpl() {
		mandatoryCodes = new HashSet<Integer>();
		mandatoryVariableCodes = new HashSet<Integer>();
		optionalCodes = new HashSet<Integer>();
		mandatoryCodeToIndex = new HashMap<Integer, Integer>();
		mandatoryVariableCodeToIndex = new HashMap<Integer, Integer>();
		optionalCodeToIndex = new HashMap<Integer, Integer>();

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

	// FIXME: above will be changed

	protected void prepareMessage() {
		if (this.o_Parameters.size() > 0)
			this.o_Parameters.put(this.o_Parameters.size(), new EndOfOptionalParametersImpl());

	}

	// ///////////////
	// TX MESSAGE //
	// ///////////////

	public ISUPTransaction getTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	public TransactionKey generateTransactionKey() {
		return null;
	}

	// ////////////////
	// CODE SECTION //
	// ////////////////
	public byte[] encodeElement() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// akward :)
		this.encodeElement(bos);
		return bos.toByteArray();
	}

	public int encodeElement(ByteArrayOutputStream bos) throws IOException {

		// bos.write(this.circuitIdentificationCode);

		boolean optionalPresent = this.o_Parameters.size() > 1;
		this.encodeMandatoryParameters(f_Parameters, bos);
		this.encodeMandatoryVariableParameters(v_Parameters, bos, optionalPresent);
		if (optionalPresent) {
			this.encodeOptionalParameters(o_Parameters, bos);
		}

		return bos.size();
	}

	// NOTE: those methods are more or less generic.
	protected void encodeMandatoryParameters(Map<Integer, ISUPParameter> parameters, ByteArrayOutputStream bos) throws IOException {
		// 1.5 Mandatory fixed part
		// Those parameters that are mandatory and of fixed length for a
		// particular message type will be
		// contained in the mandatory fixed part. The position, length and order
		// of the parameters is uniquely
		// defined by the message type; thus, the names of the parameters and
		// the length indicators are not
		// included in the message.

		for (ISUPParameter p : parameters.values()) {
			// System.err.println("ENCODE F: "+p.getCode()+"---> "+Utils.toHex(p.encodeElement()));
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
	protected void encodeMandatoryVariableParameters(Map<Integer, ISUPParameter> parameters, ByteArrayOutputStream bos, boolean isOptionalPartPresent) throws IOException {
		// bos.write(new byte[4]);
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
				// System.err.println("ENCODE V: "+p.getCode()+"---> "+Utils.toHex(body));
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
				// pointers = new byte[] { 0x00 };
			} else {
				pointers[pointers.length - 1] = (byte) (pointers[pointers.length - 2] + lastParameterLength);
			}
			// System.err.println("V POINTER: "+Utils.toHex(pointers));
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
	protected void encodeOptionalParameters(Map<Integer, ISUPParameter> parameters, ByteArrayOutputStream bos) throws IOException {

		// NOTE: parameters MUST have as last endOfOptionalParametersParameter
		for (ISUPParameter p : parameters.values()) {

			if (p == null)
				continue;

			byte[] b = p.encodeElement();
			// System.err.println("ENCODE O: "+p.getCode()+"---> "+Utils.toHex(b));
			// FIXME: this can be slow, maybe we shoudl remove that, and code
			// this explicitly?
			if (b.length > 255) {
				throw new IOException("Parameter length is over 255: " + p);
			}
			if (!(p instanceof EndOfOptionalParametersImpl)) {
				bos.write(p.getCode());

				bos.write(b.length);
			}
			bos.write(b);
		}

	}

	public int decodeElement(byte[] b) throws ParameterRangeInvalidException {
		int index = 0;
		index += this.decodeMandatoryParameters(b, index);
		index += this.decodeMandatoryVariableParameters(b, index);
		index += this.decodeOptionalParameters(b, index);

		return index;
	}

	// Unfortunelty this cant be generic, can it?
	protected abstract int decodeMandatoryParameters(byte[] b, int index) throws ParameterRangeInvalidException;

	protected int decodeMandatoryVariableParameters(byte[] b, int index) throws ParameterRangeInvalidException {
		int readCount = 0;
		int optionalOffset = 0;

		if (b.length - index > 0) {

			byte extPIndex = -1;
			try {
				int count = getNumberOfMandatoryVariableLengthParameters();
				for (int parameterIndex = 0; parameterIndex < count; parameterIndex++) {
					int parameterLengthIndex = b[index + readCount] + index;

					int parameterLength = b[parameterLengthIndex];
					byte[] parameterBody = new byte[parameterLength];
					System.arraycopy(b, parameterLengthIndex + 1, parameterBody, 0, parameterLength);
					decodeMandatoryVariableBody(parameterBody, parameterIndex);
					readCount++;
				}

				optionalOffset = b[index + readCount];
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				throw new ParameterRangeInvalidException("Failed to read parameter, to few octets in buffer, parameter index: " + extPIndex, aioobe);
			} catch (IllegalArgumentException e) {
				throw new ParameterRangeInvalidException("Failed to parse, paramet index: " + extPIndex, e);
			}
		} else {
			throw new ParameterRangeInvalidException("To few bytes to decode mandatory variable part. There should be atleast on byte to indicate optional part.");
		}

		return readCount + optionalOffset;
	}

	protected int decodeOptionalParameters(byte[] b, int index) throws ParameterRangeInvalidException {

		int localIndex = index;

		int readCount = 0;
		// if not, there are no params.
		if (b.length - index > 0) {
			// let it rip :)
			boolean readParameter = true;
			while (readParameter) {
				if (b.length - localIndex > 0 && b[localIndex] != 0) {
					readParameter = true;
				} else {
					readParameter = false;
					continue;
				}
				byte extPCode = -1;
				try {

					byte parameterCode = b[localIndex++];
					extPCode = parameterCode;
					byte parameterLength = b[localIndex++];
					byte[] parameterBody = new byte[parameterLength];
					// This is bad, we will change this

					System.arraycopy(b, localIndex, parameterBody, 0, parameterLength);
					localIndex += parameterLength;
					readCount += 2 + parameterLength;

					decodeOptionalBody(parameterBody, parameterCode);

					if (b.length - localIndex > 0 && b[localIndex] != 0) {
						readParameter = true;
					} else {
						readParameter = false;
					}

				} catch (ArrayIndexOutOfBoundsException aioobe) {
					throw new ParameterRangeInvalidException("Failed to read parameter, to few octets in buffer", aioobe);
				} catch (IllegalArgumentException e) {
					throw new ParameterRangeInvalidException("Failed to parse paramet: " + extPCode, e);
				}
			}
		}

		return readCount;
	}

	/**
	 * @param parameterBody
	 * @param parameterIndex
	 */
	protected abstract void decodeMandatoryVariableBody(byte[] parameterBody, int parameterIndex) throws ParameterRangeInvalidException;

	protected abstract void decodeOptionalBody(byte[] parameterBody, byte parameterCode) throws ParameterRangeInvalidException;

	protected abstract int getNumberOfMandatoryVariableLengthParameters();

	// ////////////////////////
	// PARAM HANDLE SECTION //
	// ////////////////////////

	// Some thing Oleg wants :)
	public void addParameter(ISUPParameter param) throws ParameterRangeInvalidException {
		if (param == null) {
			throw new IllegalArgumentException("Argument must not be null");
		}
		int paramCode = param.getCode();
		if (this.mandatoryCodes.contains(paramCode)) {
			int index = this.mandatoryCodeToIndex.get(paramCode);
			this.f_Parameters.put(index, param);
		}

		if (this.mandatoryVariableCodes.contains(paramCode)) {
			int index = this.mandatoryVariableCodeToIndex.get(paramCode);
			this.v_Parameters.put(index, param);
		}
		if (this.optionalCodes.contains(paramCode)) {
			int index = this.optionalCodeToIndex.get(paramCode);
			this.o_Parameters.put(index, param);
		}
		throw new ParameterRangeInvalidException("Parameter with code: " + paramCode + " is not defined in any type: mandatory, mandatory variable or optional");
	}

	public ISUPParameter getParameter(int parameterCode) throws ParameterRangeInvalidException {

		if (this.mandatoryCodes.contains(parameterCode)) {
			int index = this.mandatoryCodeToIndex.get(parameterCode);
			return this.f_Parameters.get(index);
		}

		if (this.mandatoryVariableCodes.contains(parameterCode)) {
			int index = this.mandatoryVariableCodeToIndex.get(parameterCode);
			return this.v_Parameters.get(index);
		}
		if (this.optionalCodes.contains(parameterCode)) {
			int index = this.optionalCodeToIndex.get(parameterCode);
			return this.o_Parameters.get(index);
		}

		throw new ParameterRangeInvalidException("Parameter with code: " + parameterCode + " is not defined in any type: mandatory, mandatory variable or optional");
	}

	public void removeParameter(int parameterCode) throws ParameterRangeInvalidException {
		if (this.mandatoryCodes.contains(parameterCode)) {
			int index = this.mandatoryCodeToIndex.get(parameterCode);
			this.f_Parameters.remove(index);
		}

		if (this.mandatoryVariableCodes.contains(parameterCode)) {
			int index = this.mandatoryVariableCodeToIndex.get(parameterCode);
			this.v_Parameters.remove(index);
		}
		if (this.optionalCodes.contains(parameterCode)) {
			int index = this.optionalCodeToIndex.get(parameterCode);
			this.o_Parameters.remove(index);
		}
		throw new ParameterRangeInvalidException("Parameter with code: " + parameterCode + " is not defined in any type: mandatory, mandatory variable or optional");
	}
}

/**
 * Start time:08:55:07 2009-08-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.mobicents.ss7.isup.ISUPComponent;
import org.mobicents.ss7.isup.ParameterRangeInvalidException;
import org.mobicents.ss7.isup.TransactionKey;
import org.mobicents.ss7.isup.message.parameter.ISUPParameter;
import org.mobicents.ss7.isup.message.parameter.MessageType;
import org.mobicents.ss7.isup.ISUPTransaction;

/**
 * Start time:08:55:07 2009-08-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public interface ISUPMessage extends ISUPComponent {
	// ///////////
	// STATICS //
	// ///////////
	public static final int _MESSAGE_CODE_ACM = 0x06;
	public static final int _MESSAGE_CODE_IAM = 0x01;
	public static final int _MESSAGE_CODE_RLC = 0x10;
	public static final int _MESSAGE_CODE_REL = 0x0C;

	/**
	 * @return <ul>
	 *         <li><b>true</b> - if all requried parameters are set</li>
	 *         <li><b>false</b> - otherwise</li>
	 *         </ul>
	 */
	public boolean hasAllMandatoryParameters();

	/**
	 * Returns message code. See Q.763 Table 4. It simply return value of static
	 * constant - _MESSAGE_TYPE, where value of parameter is value _MESSAGE_CODE
	 * 
	 * @return
	 */
	public MessageType getMessageType();

	// FIXME: above will be changed

	public byte[] encodeElement() throws IOException;

	public int encodeElement(ByteArrayOutputStream bos) throws IOException;

	public int decodeElement(byte[] b) throws ParameterRangeInvalidException;

	public void addParameter(ISUPParameter param) throws ParameterRangeInvalidException;

	public ISUPParameter getParameter(int parameterCode) throws ParameterRangeInvalidException;

	public void removeParameter(int parameterCode) throws ParameterRangeInvalidException;

	public ISUPTransaction getTransaction();

	/**
	 * Generates TX key for fast matching, messages must have some part static,
	 * so it can be used to match tx, other than that there is no way to match
	 * incoming response to transaction
	 * 
	 * @return
	 */
	public TransactionKey generateTransactionKey();

}

/**
 * Start time:09:25:10 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.isup.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.mobicents.isup.ISUPComponent;
import org.mobicents.isup.ParameterRangeInvalidException;
import org.mobicents.isup.parameters.ISUPParameter;
import org.mobicents.isup.parameters.MessageType;

/**
 * Start time:09:25:10 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface ISUPMessage extends ISUPComponent {

	
	/////////////
	// STATICS //
	/////////////
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
	
}

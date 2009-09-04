/**
 * Start time:08:52:48 2009-08-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.ss7.isup;

import java.io.IOException;


import org.mobicents.ss7.SS7Provider;
import org.mobicents.ss7.isup.message.ISUPMessage;

/**
 * Start time:08:52:48 2009-08-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface ISUPProvider {
	/**
	 * Stateles messages send. No state is maintained.
	 * @param msg
	 * @throws ParameterRangeInvalidException
	 * @throws IOException
	 */
	public void sendMessage(ISUPMessage msg) throws ParameterRangeInvalidException, IOException;
	/**
	 * Send message with use of session, it will allow us to receive timeout in case of bad behaviour.
	 * @param msg
	 * @throws ParameterRangeInvalidException
	 * @throws IOException
	 */
	public void sendMessage(ISUPTransaction msg) throws ParameterRangeInvalidException, IOException;
	//For mtp?
	public SS7Provider getTransportProvider();
	
	public void addListener(ISUPListener listener);
	public void removeListener(ISUPListener listener);
	
	public ISUPMessageFactory getMessageFactory();
	public ISUPClientTransaction createClientTransaction(ISUPMessage msg) throws   TransactionAlredyExistsException, IllegalArgumentException; 
	public ISUPServerTransaction createServerTransaction(ISUPMessage msg) throws   TransactionAlredyExistsException, IllegalArgumentException; 
}

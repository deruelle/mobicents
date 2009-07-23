/**
 * Start time:09:05:34 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.isup;

import java.io.IOException;

import org.mobicents.isup.messages.ISUPMessage;


/**
 * Start time:09:05:34 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ISUPProvider {

	
	
	//FIXME: Oleg more methods here?
	public void sendMessage(ISUPMessage msg) throws ParameterRangeInvalidException, IOException;
	
	//For mtp?
	public Object getTransportProvider();
	
	public void addListener(ISUPListener listener);
	public void removeListener(ISUPListener listener);
	
	
	public ISUPMessage createMessage(int commandCode);
}

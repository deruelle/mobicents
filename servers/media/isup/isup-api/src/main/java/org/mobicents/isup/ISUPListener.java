/**
 * Start time:09:05:54 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.isup;

import org.mobicents.isup.messages.ISUPMessage;


/**
 * Start time:09:05:54 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>

 */
public interface ISUPListener {

	public void onMessage(ISUPMessage msg);
	public void onMessageTimeout(ISUPMessage msg);
	
	
}

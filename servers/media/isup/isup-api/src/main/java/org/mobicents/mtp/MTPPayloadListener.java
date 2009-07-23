/**
 * Start time:09:14:00 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.mtp;

/**
 * Start time:09:14:00 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface MTPPayloadListener {

	public void dataTransfered(byte[] conveyedMessage);
	//FIXME: this should only pass some pointer?
	public void dataTimedOut(byte[] conveyedMessage);
}

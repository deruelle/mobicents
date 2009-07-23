/**
 * Start time:09:13:45 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * 
 */
package org.mobicents.mtp;

/**
 * Start time:09:13:45 2009-07-18<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface MTPProvider {

	/**
	 * @param encoded
	 */
	void sendData(byte[] encoded);

}

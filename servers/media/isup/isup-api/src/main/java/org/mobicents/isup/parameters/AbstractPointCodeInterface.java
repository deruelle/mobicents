/**
 * Start time:13:40:22 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.parameters;

/**
 * Start time:13:40:22 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public interface AbstractPointCodeInterface extends ISUPParameter {
	public int getSignalingPointCode();

	public void setSignalingPointCode(int signalingPointCode) ;
}

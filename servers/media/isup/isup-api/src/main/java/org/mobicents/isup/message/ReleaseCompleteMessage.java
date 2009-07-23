/**
 * Start time:10:02:24 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.isup.messages;

import org.mobicents.isup.parameters.CauseIndicators;

/**
 * Start time:10:02:24 2009-07-23<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public interface ReleaseCompleteMessage extends ISUPMessage {
	public CauseIndicators getCauseIndicators();

	public void setCauseIndicators(CauseIndicators v);
}

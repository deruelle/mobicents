/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 * Applications that have registered for notification from Endpoint for
 * particular event like DTMF should implement MsNotificationListener
 * 
 * @author Oleg Kulikov
 */
public interface MsNotificationListener extends Serializable {
	public void update(MsNotifyEvent evt);
}

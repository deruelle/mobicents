/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 *
 * @author Oleg Kulikov
 */
public interface MsNotificationListener extends Serializable {
    public void update(MsNotifyEvent evt);
}

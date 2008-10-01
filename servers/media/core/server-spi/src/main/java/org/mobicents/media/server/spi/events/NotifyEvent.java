/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.spi.events;

import java.io.Serializable;

/**
 *
 * @author Oleg Kulikov
 */
public interface NotifyEvent extends Serializable {
    public String getEventID();
}

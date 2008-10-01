/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.announcement;

import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnEventImpl implements NotifyEvent {

    private String eventID;
    
    public AnnEventImpl(String eventID) {
        this.eventID = eventID;
    }
    
    public String getEventID() {
        return eventID;
    }

}

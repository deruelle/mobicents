/*
 * EventTrigger.java
 *
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.server.impl;

import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class EventTrigger implements NotificationListener {
    
    private BaseEndpoint endpoint;
    private int eventID;
    private NotificationListener listener;
    private boolean persistent;
    /**
     * Creates a new instance of EventTrigger
     */
    public EventTrigger(BaseEndpoint endpoint, 
            int eventID,       
            NotificationListener listener,
            boolean persistent) {
        this.eventID = eventID;
        this.listener = listener;
        this.persistent = persistent;
    }

    public int getEventID() {
        return eventID;
    }

    public NotificationListener getNotifiedEntity() {
        return listener;
    }

    public void update(NotifyEvent event) {
        if (event.getID() == eventID) {
            listener.update(event);
            if (!persistent) {
                endpoint.removeNotifyListener(this);
            }
        }
    }
    
}

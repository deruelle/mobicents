/*
 * Signal.java
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

import java.io.Serializable;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class Signal implements Serializable {
    
    private NotificationListener listener;
    
    /** Creates a new instance of Signal */
    public Signal(NotificationListener listener) {
        this.listener = listener;
    }
    
    public abstract void start();
    public abstract void stop();
    
    public NotificationListener getListener() {
        return listener;
    }
    
    public void sendEvent(NotifyEvent evt) {
        new Thread(new EventSender(evt)).start();
    }
    
    private class EventSender implements Runnable {
        private NotifyEvent evt;
        
        public EventSender(NotifyEvent evt) {
            this.evt = evt;
        }
        
        public void run() {
            listener.update(evt);
        }
    }
}

package org.mobicents.media.server.spi.events;

import org.mobicents.media.server.spi.NotificationListener;

public abstract class AbstractRequestedSignal implements RequestedSignal {

    private NotificationListener listener;
    
    public void setHandler(NotificationListener listener) {
        this.listener = listener;
    }

    public NotificationListener getHandler() {
        return listener;
    }

}

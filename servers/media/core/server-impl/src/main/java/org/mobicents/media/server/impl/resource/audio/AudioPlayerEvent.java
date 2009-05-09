/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.audio;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class AudioPlayerEvent implements NotifyEvent {

    public final static int STARTED = 1;
    public final static int END_OF_MEDIA = 2;
    public final static int FAILED = 3;
    
    private Endpoint endpoint;
    private Connection connection;
    private int resourceType;
    private int eventID;

    public AudioPlayerEvent(AudioPlayerImpl player, int eventID) {
        this.endpoint = player.getEndpoint();
        this.connection = player.getConnection();
        this.resourceType = player.getResourceType();
        this.eventID = eventID;
    }
        
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public Connection getConnection() {
        return connection;
    }

    public int getResourceID() {
        return resourceType;
    }

    public int getEventID() {
        return eventID;
    }

}

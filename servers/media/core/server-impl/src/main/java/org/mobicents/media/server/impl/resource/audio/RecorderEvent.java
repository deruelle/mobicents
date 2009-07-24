package org.mobicents.media.server.impl.resource.audio;

import org.mobicents.media.Component;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author amit bhayani
 * 
 */
public class RecorderEvent implements NotifyEvent {

	public final static int DURATION_OVER = 1;
	public final static int STOPPED = 2;
	public final static int FAILED = 3;

	private Endpoint endpoint;
	private Connection connection;
	private String resourceName;
	private int eventID;

	public RecorderEvent(RecorderImpl recorder, int eventID) {
		this.endpoint = recorder.getEndpoint();
		this.connection = recorder.getConnection();
		this.resourceName = recorder.getName();
		this.eventID = eventID;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public Endpoint getEndpoint() {
		return this.endpoint;
	}

	public int getEventID() {
		return this.eventID;
	}


    public String getResourceName() {
        return resourceName;
    }

    public Component getSource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

package org.mobicents.media.server.impl.events.connection.parameters;

import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.spi.events.connection.parameters.ConnectionParametersEvent;

public class ConnectionParametersGenerator extends AbstractSource {

	public ConnectionParametersGenerator() {
		super("ConnectionParametersGenerator");

	}

	public Format[] getFormats() {
		// TODO Auto-generated method stub
		return null;
	}

	public void start() {

	}

	public void stop() {

	}

	public void generateParametersEvent(BaseConnection connection) {
		
		ConnectionParametersEvent params = new ConnectionParametersEvent(
				connection.getOctetsSent(), connection.getOctetsReceived(),
				connection.getPacketsReceived(), connection.getPacketsSent(),
				connection.getPacketsLost(), connection.getInterArrivalJitter());
		
		//FIXME: Oleg this is not rfc, but this is the spot to reset stats.
		//connection.setGatherStats(false);
		//connection.setGatherStats(true);
		
		
		super.sendEvent(params);
	}

}

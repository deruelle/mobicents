package org.mobicents.media.server.impl.fft;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.impl.common.MediaResourceType;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;

public class DFTSineSignal extends Signal {
	
	private static Logger logger=Logger.getLogger(DFTSineSignal.class);
	
	public static final int DFT_SINE_SIGNAL=12345678;
	
	
	

	private BaseEndpoint endpoint;
	private BaseConnection connection;

	public DFTSineSignal(NotificationListener listener, BaseEndpoint endpoint,
			BaseConnection connection) {
		super(listener);
		this.endpoint = endpoint;
		this.connection = connection;
	}

	@Override
	public void start() {

		// FIXME: HERE WE SHOULD USE MIXER
		try {

			Collection<BaseConnection> list = endpoint.getConnections();
			for (BaseConnection connection : list) {
				DFTSineSource resource = (DFTSineSource) endpoint.getResource(
						MediaResourceType.AUDIO_SOURCE, connection.getId());
				resource.start();
			}


		} catch (Exception e) {
			e.printStackTrace();
			NotifyEvent report = new NotifyEvent(endpoint, EventID.FAIL,
					EventCause.FACILITY_FAILURE, e.getMessage());
			this.sendEvent(report);

		}
	}

	@Override
	public void stop() {
		
		// FIXME: HERE WE SHOULD USE MIXER
		try {

			Collection<BaseConnection> list = endpoint.getConnections();
			for (BaseConnection connection : list) {
				DFTSineSource resource = (DFTSineSource) endpoint.getResource(
						MediaResourceType.AUDIO_SOURCE, connection.getId());
				resource.stop();
			}


		} catch (Exception e) {
			e.printStackTrace();
			NotifyEvent report = new NotifyEvent(endpoint, EventID.FAIL,
					EventCause.FACILITY_FAILURE, e.getMessage());
			this.sendEvent(report);

		}

	}
	
	
	

}

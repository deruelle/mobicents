package org.mobicents.media.server.ctrl.mgcp.evt.ivr;

import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;

/**
 * 
 * @author amit bhayani
 *
 */
public class Recorder extends SignalGenerator {

	private org.mobicents.media.server.spi.resource.Recorder recorder;
	private String url;

	public Recorder(String resourceName, String url) {
		super(resourceName, url);
		this.url = url;
	}

	@Override
	public void cancel() {
		this.recorder.stop();
	}

	@Override
	protected boolean doVerify(Connection connection) {
		this.recorder = (org.mobicents.media.server.spi.resource.Recorder) connection.getComponent(getResourceName());
		return this.recorder != null;
	}

	@Override
	protected boolean doVerify(Endpoint endpoint) {
		this.recorder = (org.mobicents.media.server.spi.resource.Recorder) endpoint.getComponent(getResourceName());
		return this.recorder != null;
	}

	@Override
	public void start(Request request) {
		recorder.start(this.url);
	}

}

package org.mobicents.media.server.ctrl.mgcp.evt.dtmf;

import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;

/**
 * 
 * @author amit bhayani
 * 
 */
public class DtmfGenerator extends SignalGenerator {

	private String digit = null;

	private org.mobicents.media.server.spi.resource.DtmfGenerator dtmfGenerator = null;

	public DtmfGenerator(String resourceName, String digit) {
		super(resourceName, digit);
		this.digit = digit;
	}

	@Override
	public void cancel() {
		// Do nothing
	}

	@Override
	protected boolean doVerify(Connection connection) {
		dtmfGenerator = (org.mobicents.media.server.spi.resource.DtmfGenerator) connection
				.getComponent(getResourceName(), Connection.CHANNEL_TX);
		return dtmfGenerator != null;
	}

	@Override
	protected boolean doVerify(Endpoint endpoint) {
		dtmfGenerator = (org.mobicents.media.server.spi.resource.DtmfGenerator) endpoint
				.getComponent(getResourceName());
		return dtmfGenerator != null;
	}

	@Override
	public void start(Request request) {
		dtmfGenerator.setDigit(this.digit);
		dtmfGenerator.start();
	}

}

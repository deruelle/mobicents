package org.mobicents.media.server.ctrl.mgcp.evt.ann;

import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.resource.Rfc2833Generator;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Rfc2833DtmfGenerator extends SignalGenerator {

	private String digit = null;
	private int duration = Rfc2833Generator.RFC2833_GENERATOR_DURATION;
	private int volume = Rfc2833Generator.RFC2833_GENERATOR_VOLUME;

	private Rfc2833Generator rfc2833Generator = null;

	public Rfc2833DtmfGenerator(String resourceName, String digit) {
		super(resourceName, digit);
		this.digit = digit;
	}

	public Rfc2833DtmfGenerator(String resourceName, String digit, int duration, int volume) {
		this(resourceName, digit);
		this.volume = volume;
		this.duration = duration;
	}

	@Override
	public void cancel() {
		// Do nothing
	}

	@Override
	protected boolean doVerify(Connection connection) {
		rfc2833Generator = (Rfc2833Generator) connection.getComponent(getResourceName());
		return rfc2833Generator != null;
	}

	@Override
	protected boolean doVerify(Endpoint endpoint) {
		rfc2833Generator = (Rfc2833Generator) endpoint.getComponent(getResourceName());
		return rfc2833Generator != null;
	}

	@Override
	public void start(Request request) {
		rfc2833Generator.setDigit(this.digit);
		rfc2833Generator.setVolume(this.volume);
		rfc2833Generator.setDuration(this.duration);
		rfc2833Generator.start();
	}

}

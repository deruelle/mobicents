package org.mobicents.media.server.ctrl.mgcp.evt.tone;

import java.util.ArrayList;
import java.util.List;

import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.ctrl.mgcp.evt.SignalGenerator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.resource.FrequencyBean;

/**
 * 
 * @author amit.bhayani
 *
 */
public class MultiFreqToneGenerator extends SignalGenerator {

	private org.mobicents.media.server.spi.resource.MultiFreqToneGenerator mulFreqGen;
	private String params;

	public MultiFreqToneGenerator(String resourceName, String params) {
		super(resourceName, params);
		this.params = params;
	}

	@Override
	public void cancel() {
		this.mulFreqGen.stop();
		this.mulFreqGen = null;
	}

	@Override
	protected boolean doVerify(Connection connection) {
		this.mulFreqGen = (org.mobicents.media.server.spi.resource.MultiFreqToneGenerator) connection.getComponent(
				getResourceName(), Connection.CHANNEL_TX);
		return this.mulFreqGen != null;
	}

	@Override
	protected boolean doVerify(Endpoint endpoint) {
		this.mulFreqGen = (org.mobicents.media.server.spi.resource.MultiFreqToneGenerator) endpoint
				.getComponent(getResourceName());
		return this.mulFreqGen != null;
	}

	@Override
	public void start(Request request) {
		List<FrequencyBean> freqBean = decodeParams();
		this.mulFreqGen.setFreqBeanList(freqBean);
		this.mulFreqGen.setVolume(0);
		this.mulFreqGen.start();
	}

	private List<FrequencyBean> decodeParams() {
		List<FrequencyBean> listFreqtemp = new ArrayList<FrequencyBean>();
		String[] freqData = this.params.split("-");
		int count = 0;
		FrequencyBean freqbean;
		while (count < freqData.length) {
			int lowFreq = Integer.parseInt(freqData[count++]);
			int highFreq = Integer.parseInt(freqData[count++]);
			int duration = Integer.parseInt(freqData[count++]);
			freqbean = new FrequencyBean(lowFreq, highFreq, duration);
			listFreqtemp.add(freqbean);
		}

		return listFreqtemp;

	}

}

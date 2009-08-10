package org.mobicents.media.server.ctrl.mgcp.evt.tone;

import jain.protocol.ip.mgcp.message.parms.RequestedAction;

import org.mobicents.media.server.ctrl.mgcp.evt.EventDetector;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.resource.FrequencyBean;

/**
 * 
 * @author amit.bhayani
 *
 */
public class MultiFreqToneDetector extends EventDetector {

	public MultiFreqToneDetector(String pkgName, String eventName, String resourceName, int eventID, String params,
			RequestedAction[] actions) {
		super(pkgName, eventName, resourceName, eventID, params, actions);
	}

	@Override
	public void performAction(NotifyEvent event, RequestedAction action) {
		if (!event.getSource().getName().matches(this.getResourceName())) {
			return;
		}

		if (event.getEventID() != this.getEventID()) {
			return;
		}

		// @TODO implement action selector
		getRequest().sendNotify(this.getEventName());
	}

	@Override
	public void start() {
		FrequencyBean freqBean = this.decodeParams();
		((org.mobicents.media.server.spi.resource.MultiFreqToneDetector) this.component).setFreqBean(freqBean);
		super.start();
	}

	private FrequencyBean decodeParams() {

		String[] freqData = this.params.split("-");
		int lowFreq = Integer.parseInt(freqData[0]);
		int highFreq = Integer.parseInt(freqData[1]);
		int duration = Integer.parseInt(freqData[2]);
		FrequencyBean freqbean = new FrequencyBean(lowFreq, highFreq, duration);

		return freqbean;
	}

}

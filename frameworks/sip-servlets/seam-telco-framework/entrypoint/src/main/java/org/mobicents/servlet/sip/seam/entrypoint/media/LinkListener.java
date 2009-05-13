package org.mobicents.servlet.sip.seam.entrypoint.media;

import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;
import org.jboss.seam.core.Events;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.servlet.sip.seam.entrypoint.SeamEntrypointUtils;

/**
 * The default link listener that delivers events to Seam.
 * 
 * @author vralev
 *
 */
public class LinkListener implements MsLinkListener {

	private static Logger log = Logger.getLogger(LinkListener.class);
	
	private SipSession sipSession;
	
	public LinkListener(SipSession sipSession) {
		this.sipSession = sipSession;
	}
	
	private void postEvent(String eventName, MsLinkEvent event) {
		if(log.isDebugEnabled()) {
			log.debug("Before posting Event from listener: " 
					+ eventName + ", session=" + sipSession.toString());
		}
		try {
			SeamEntrypointUtils.beginEvent(sipSession);
			Events.instance().raiseEvent(eventName, event);
			SeamEntrypointUtils.endEvent();
		} catch (Throwable t) {
			log.error("Error delivering event " + eventName + 
					", session=" + sipSession.toString(), t);
			SeamEntrypointUtils.beginEvent(sipSession);
			Events.instance().raiseEvent("org.mobicents.media.unhandledException", t);
			SeamEntrypointUtils.endEvent();
		}
		if(log.isDebugEnabled()) {
			log.debug("After posting Event from listener: " 
					+ eventName + ", session=" + sipSession.toString());
		}
	}
	
	public void linkConnected(MsLinkEvent arg0) {
		postEvent("preLinkConnected", arg0);
	}

	public void linkCreated(MsLinkEvent arg0) {
		postEvent("linkCreated", arg0);
	}

	public void linkDisconnected(MsLinkEvent arg0) {
		postEvent("linkDisconnected", arg0);
	}

	public void linkFailed(MsLinkEvent arg0) {
		postEvent("linkFailed", arg0);
	}

	public void modeFullDuplex(MsLinkEvent arg0) {
		postEvent("modeFullDuplex", arg0);
	}

	public void modeHalfDuplex(MsLinkEvent arg0) {
		postEvent("modeHalfDuplex", arg0);
	}

}

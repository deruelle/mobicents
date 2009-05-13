package org.mobicents.servlet.sip.seam.entrypoint.media;

import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;
import org.jboss.seam.core.Events;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.servlet.sip.seam.entrypoint.SeamEntrypointUtils;

/**
 * Default connection listener that relays events to Seam.
 * 
 * @author vralev
 *
 */
public class ConnectionListener implements MsConnectionListener {

	private static Logger log = Logger.getLogger(ConnectionListener.class);
	private SipSession sipSession;
	
	public ConnectionListener(SipSession sipSession) {
		this.sipSession = sipSession;
	}
	
	private void postEvent(String eventName, MsConnectionEvent event) {
		if(log.isDebugEnabled()) {
			log.debug("Before posting Event from listener: " 
					+ eventName + ", session=" + sipSession.toString());
		}
		log.info("Before posting Event from listener: " 
				+ eventName + ", session=" + sipSession.toString());
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
	
	public void connectionCreated(MsConnectionEvent arg0) {
		postEvent("connectionCreated", arg0);
	}

	public void connectionDisconnected(MsConnectionEvent arg0) {
		postEvent("connectionDisconnected", arg0);
	}

	public void connectionFailed(MsConnectionEvent arg0) {
		postEvent("connectionFailed", arg0);
	}

	public void connectionHalfOpen(MsConnectionEvent arg0) {
		postEvent("preConnectionHalfOpen", arg0);
	}

	public void connectionModeRecvOnly(MsConnectionEvent arg0) {
		postEvent("connectionModeRecvOnly", arg0);
	}

	public void connectionModeSendOnly(MsConnectionEvent arg0) {
		postEvent("connectionModeSendOnly", arg0);
	}

	public void connectionModeSendRecv(MsConnectionEvent arg0) {
		postEvent("connectionModeSendRecv", arg0);
	}

	public void connectionOpen(MsConnectionEvent arg0) {
		postEvent("connectionOpen", arg0);
	}

}

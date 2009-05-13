package org.mobicents.servlet.sip.seam.entrypoint.media;


import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;
import org.jboss.seam.core.Events;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsNotificationListener;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsSession;
import org.mobicents.servlet.sip.seam.entrypoint.SeamEntrypointUtils;

/**
 * The default media notification listener. It just relays the event to Seam.
 * 
 * @author vralev
 *
 */
public class NotificationListener implements MsNotificationListener {
	private static Logger log = Logger.getLogger(NotificationListener.class);
	
	private SipSession sipSession;
	private MsEndpoint endpoint;
	private Object link;
	private MsSession msSession;
	
	private void postEvent(String eventName, MediaEvent event) {
		if(log.isDebugEnabled()) {
			log.debug("Before posting Event from listener: " 
					+ eventName + ", session=" + sipSession.toString());
		}
		try {
			Thread.currentThread().setContextClassLoader(SipSession.class.getClassLoader());
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
	
	public NotificationListener(SipSession sipSession, MsSession msSession, MsEndpoint endpoint, Object link) {
		this.sipSession = sipSession;
		this.endpoint = endpoint;
		this.link = link;
		this.msSession = msSession;
		
	}
	
	public void update(MsNotifyEvent arg0) {
		postEvent("mediaEvent", new MediaEvent(sipSession, endpoint, arg0, link));
	}

}

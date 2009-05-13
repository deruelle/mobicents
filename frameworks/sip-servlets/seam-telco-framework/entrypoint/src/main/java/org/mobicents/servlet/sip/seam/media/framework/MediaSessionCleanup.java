package org.mobicents.servlet.sip.seam.media.framework;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.sip.SipSession;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.dtmf.MsDtmfNotifyEvent;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.mscontrol.events.pkg.MsAudio;
import org.mobicents.servlet.sip.seam.entrypoint.media.MediaEvent;

/**
 * This class simply cleans up any media connections/links that are not explicitly cleaned up from
 * the application as a failsafe.
 * 
 * @author vralev
 *
 */

@Name("mediaSessionCleanup")
@Scope(ScopeType.STATELESS)
public class MediaSessionCleanup {
	@In(required=false) MediaSessionStore mediaSessionStore;
	@Logger Log log;
	
	/**
	 * You can use this method to simulate some event, BUT THIS IS NOT RECOMMENDED!
	 */
	@Observer("sipSessionDestroyed")
	public void doSipSessionDestroyed(SipSession sipSession) {
		if(mediaSessionStore != null) {
			if(mediaSessionStore.getMsConnection() != null) {
				mediaSessionStore.getMsConnection().release();
			}
			if(mediaSessionStore.getMsLink() != null) {
				mediaSessionStore.getMsLink().release();
			}
		}
	}
	
}

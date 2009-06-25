package org.mobicents.servlet.sip.seam.entrypoint;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSessionEvent;
import javax.servlet.sip.SipSessionListener;
import javax.servlet.sip.SipSessionsUtil;
import javax.servlet.sip.TimerService;

import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Init;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.mock.MockHttpServletRequest;
import org.jboss.seam.mock.MockHttpSession;
import org.mobicents.servlet.sip.seam.entrypoint.media.MsProviderContainer;

public class SeamEntryPointServlet extends javax.servlet.sip.SipServlet implements SipSessionListener {
	private static LogProvider log = Logging.getLogProvider(SeamEntryPointServlet.class);
	
	public void sessionCreated(final SipSessionEvent arg0) {
		Thread t = new Thread() {
			@Override
			public void run() {
				arg0.getSession().setAttribute("msSession", MsProviderContainer.msProvider.createSession());
				arg0.getSession().setAttribute("sipSession", arg0.getSession());
				Lifecycle.beginSession(new SipSeamSessionMap(arg0.getSession()));
				SeamEntrypointUtils.beginEvent(arg0.getSession());
				Contexts.getSessionContext().set("sipSession", arg0.getSession());
				Contexts.getSessionContext().set("msSession", MsProviderContainer.msProvider.createSession());
				Contexts.getApplicationContext().set("eventFactory", MsProviderContainer.msProvider.getEventFactory());
				
				Events.instance().raiseEvent("sipSessionCreated", arg0.getSession());
				SeamEntrypointUtils.endEvent();
				log.info("SEAM SIP SESSION CREATED IN NEW THREAD");
			}
			
		};
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			log.error("Error joining a thread for session creation", e);
		}

	}

	public void sessionDestroyed(final SipSessionEvent arg0) {
		SeamEntrypointUtils.beginEvent(arg0.getSession());
		Events.instance().raiseEvent("sipSessionDestroyed", arg0.getSession());
		SeamEntrypointUtils.endEvent();
		Lifecycle.endSession(new SipSeamSessionMap(arg0.getSession()));
		log.info("SEAM SIP SESSION DESTROYED");
	}

	public void sessionReadyToInvalidate(SipSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	protected void doRequest(SipServletRequest request) throws ServletException,
			IOException {
		ServletContext ctx = request.getSession().getServletContext();
		Init init = (Init) ctx.getAttribute( Seam.getComponentName(Init.class) );
		if ( init!=null && init.isDebug())
		{
			MockHttpSession session = new MockHttpSession(ctx);
			MockHttpServletRequest r = new MockHttpServletRequest(session);

			try {
				new Initialization(ctx).redeploy(r);
			} catch (Exception e) {
				log.error("Unexpected exception while movking the servlet context", e);
			}
		}
		String dtmf = checkForSipDtmf(request);
		SeamEntrypointUtils.beginEvent(request);
		Contexts.getApplicationContext().set("sipFactory", (SipFactory) getServletContext().getAttribute(
				SIP_FACTORY));
		Contexts.getApplicationContext().set("sipSessionsUtil", (SipSessionsUtil) getServletContext().getAttribute(
				SIP_SESSIONS_UTIL));
		Contexts.getApplicationContext().set("timerService", (TimerService) getServletContext().getAttribute(
				TIMER_SERVICE));
		Events.instance().raiseEvent(request.getMethod().toUpperCase(), request);
		if(dtmf != null) {
			Events.instance().raiseEvent("DTMF", dtmf);
		}
		SeamEntrypointUtils.endEvent();
	}
	
	private String checkForSipDtmf(SipServletRequest request) {
		// seek for DTMF in the message http://www.voip-info.org/wiki/view/SIP+Info+DTMF

		try{
			if(request.getMethod().equalsIgnoreCase("INFO")) {
				String contentType = request.getContentType();
				if(contentType != null) {
					if("application/dtmf".equalsIgnoreCase(contentType.trim())) {
						String messageContent = new String( (byte[]) request.getContent());
						log.debug("Detected application/dtmf");
						return messageContent.trim();
					} else if("application/dtmf-relay".equalsIgnoreCase(contentType.trim())) {
						String messageContent = new String( (byte[]) request.getContent());
						int signalIndex = messageContent.indexOf("Signal=");
						log.debug("Detected application/dtmf-relay");
						if(messageContent != null && messageContent.length() > 0 && signalIndex != -1) {
							String signal = messageContent.substring("Signal=".length(),
									"Signal=".length() + 1).trim();
							return signal;
						}
					}
				}
			}
		} catch(Exception e) {
			log.error("Can not parse DTMF", e);
		}

		return null;
	}

	@Override
	protected void doResponse(SipServletResponse response) throws ServletException,
			IOException {
		SeamEntrypointUtils.beginEvent(response);
		Contexts.getApplicationContext().set("sipFactory", (SipFactory) getServletContext().getAttribute(
				SIP_FACTORY));
		Contexts.getApplicationContext().set("sipSessionsUtil", (SipSessionsUtil) getServletContext().getAttribute(
				SIP_SESSIONS_UTIL));
		Contexts.getApplicationContext().set("timerService", (TimerService) getServletContext().getAttribute(
				TIMER_SERVICE));
		Events.instance().raiseEvent("RESPONSE", response);
		SeamEntrypointUtils.endEvent();
	}
}

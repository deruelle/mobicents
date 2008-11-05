package org.mobicents.mscontrol.impl;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkEventCause;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsLinkState;

public class ZMsLinkImplStateMachineTest extends AbstractTest {
	private static Logger logger = Logger.getLogger(ZMsLinkImplStateMachineTest.class);

	public ZMsLinkImplStateMachineTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZMsLinkImplStateMachineTest.class);
	}

	public void testLinkCreated() {
		msProvider.addLinkListener(new MsLinkListener() {

			public void linkConnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkConnected called. Expected testPassed = false";
				testPassed = false;
			}

			public void linkCreated(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkCreated called. Expected testPassed = true";
				testPassed = true;
				assertEquals(MsLinkEventCause.NORMAL, evt.getCause());
				assertEquals(MsLinkMode.FULL_DUPLEX, evt.getSource().getMode());

			}

			public void linkDisconnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkDisconnected called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkFailed(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void modeFullDuplex(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.modeFullDuplex called. Expected testPassed = false";
				testPassed = false;
				
			}

			public void modeHalfDuplex(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.modeHalfDuplex called. Expected testPassed = false";
				testPassed = false;
				
			}
		});
		
		msSession = msProvider.createSession();
		msLink = msSession.createLink(MsLinkMode.FULL_DUPLEX);
		assertNotNull(msLink);
		assertEquals(MsLinkState.IDLE, msLink.getState());
	}
	
	public void testLinkConnected() {
		msProvider.addLinkListener(new MsLinkListener() {

			public void linkConnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkConnected called. Expected testPassed = true";
				testPassed = true;
				assertEquals(MsLinkEventCause.NORMAL, evt.getCause());
			}

			public void linkCreated(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkCreated called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkDisconnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkDisconnected called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkFailed(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void modeFullDuplex(MsLinkEvent evt) {
				testPassed = false;
				
			}

			public void modeHalfDuplex(MsLinkEvent evt) {
				testPassed = false;
				
			}
		});
		
		msSession = msProvider.createSession();
		msLink = msSession.createLink(MsLinkMode.FULL_DUPLEX);
		assertNotNull(msLink);
		assertEquals(MsLinkState.IDLE, msLink.getState());
		
		msLink.join("media/trunk/Announcement/$", "media/trunk/Conference/$");
	}	
	
	public void testLinkDisconnected() {
		msProvider.addLinkListener(new MsLinkListener() {

			public void linkConnected(MsLinkEvent evt) {
				message = "testLinkDisconnected : MsLinkListenerImpl.linkConnected called. Expected testPassed = false";
				testPassed = false;
				
				//Let us disconnect the link here
				msLink.release();
			}

			public void linkCreated(MsLinkEvent evt) {
				message = "testLinkDisconnected : MsLinkListenerImpl.linkCreated called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkDisconnected(MsLinkEvent evt) {
				message = "testLinkDisconnected : MsLinkListenerImpl.linkDisconnected called. Expected testPassed = true";
				testPassed = true;
				logger.info("linkDisconnected. Cause = "+evt.getCause());
				assertEquals(MsLinkEventCause.NORMAL, evt.getCause());
			}

			public void linkFailed(MsLinkEvent evt) {
				message = "testLinkDisconnected : MsLinkListenerImpl.linkFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void modeFullDuplex(MsLinkEvent evt) {
				testPassed = false;
				
			}

			public void modeHalfDuplex(MsLinkEvent evt) {
				testPassed = false;
				
			}
		});
		
		msSession = msProvider.createSession();
		msLink = msSession.createLink(MsLinkMode.FULL_DUPLEX);
		assertNotNull(msLink);
		assertEquals(MsLinkState.IDLE, msLink.getState());
		
		msLink.join("media/trunk/Announcement/$", "media/trunk/Conference/$");
	}		
	
	public void testLinkModeHalfDuplex() {
		msProvider.addLinkListener(new MsLinkListener() {

			public void linkConnected(MsLinkEvent evt) {
				message = "testLinkModeHalfDuplex : MsLinkListenerImpl.linkConnected called. Expected testPassed = false";
				testPassed = false;
				
				//Let us change the MsLinkMode here
				evt.getSource().setMode(MsLinkMode.HALF_DUPLEX);
			}

			public void linkCreated(MsLinkEvent evt) {
				message = "testLinkModeHalfDuplex : MsLinkListenerImpl.linkCreated called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkDisconnected(MsLinkEvent evt) {
				message = "testLinkModeHalfDuplex : MsLinkListenerImpl.linkDisconnected called. Expected testPassed = false";
				testPassed = false;
			}

			public void linkFailed(MsLinkEvent evt) {
				message = "testLinkModeHalfDuplex : MsLinkListenerImpl.linkFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void modeFullDuplex(MsLinkEvent evt) {
				message = "testLinkModeHalfDuplex : MsLinkListenerImpl.modeFullDuplex called. Expected testPassed = false";
				testPassed = false;
				
			}

			public void modeHalfDuplex(MsLinkEvent evt) {
				message = "testLinkModeHalfDuplex : MsLinkListenerImpl.modeHalfDuplex called. Expected testPassed = true";
				testPassed = true;
				assertEquals(MsLinkMode.HALF_DUPLEX, evt.getSource().getMode());
				
			}
		});
		
		msSession = msProvider.createSession();
		msLink = msSession.createLink(MsLinkMode.FULL_DUPLEX);
		assertNotNull(msLink);
		assertEquals(MsLinkState.IDLE, msLink.getState());
		
		msLink.join("media/trunk/Announcement/$", "media/trunk/Conference/$");
	}	
	
	public void testLinkModeFullDuplex() {
		msProvider.addLinkListener(new MsLinkListener() {

			public void linkConnected(MsLinkEvent evt) {
				message = "testLinkModeFullDuplex : MsLinkListenerImpl.linkConnected called. Expected testPassed = false";
				testPassed = false;
				
				//Let us change the MsLinkMode here
				evt.getSource().setMode(MsLinkMode.FULL_DUPLEX);
			}

			public void linkCreated(MsLinkEvent evt) {
				message = "testLinkModeFullDuplex : MsLinkListenerImpl.linkCreated called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkDisconnected(MsLinkEvent evt) {
				message = "testLinkModeFullDuplex : MsLinkListenerImpl.linkDisconnected called. Expected testPassed = false";
				testPassed = false;
			}

			public void linkFailed(MsLinkEvent evt) {
				message = "testLinkModeFullDuplex : MsLinkListenerImpl.linkFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void modeFullDuplex(MsLinkEvent evt) {
				message = "testLinkModeFullDuplex : MsLinkListenerImpl.modeFullDuplex called. Expected testPassed = true";
				testPassed = true;
				assertEquals(MsLinkMode.FULL_DUPLEX, evt.getSource().getMode());
				
			}

			public void modeHalfDuplex(MsLinkEvent evt) {
				message = "testLinkModeFullDuplex : MsLinkListenerImpl.modeHalfDuplex called. Expected testPassed = false";
				testPassed = false;				
			}
		});
		
		msSession = msProvider.createSession();
		msLink = msSession.createLink(MsLinkMode.HALF_DUPLEX);
		assertNotNull(msLink);
		assertEquals(MsLinkState.IDLE, msLink.getState());
		
		msLink.join("media/trunk/Announcement/$", "media/trunk/Conference/$");
	}	
	
	public void testLinkFailedEndpointUnknown() {
		msProvider.addLinkListener(new MsLinkListener() {

			public void linkConnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkConnected called. Expected testPassed = false";
				testPassed = false;				
			}

			public void linkCreated(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void linkDisconnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkDisconnected called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkFailed(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkFailed called. Expected testPassed = true";
				testPassed = true;
				assertEquals(MsLinkEventCause.ENDPOINT_UNKNOWN, evt.getCause());
				logger.info("testLinkFailedEndpointUnknown : linkFailed");
			}

			public void modeFullDuplex(MsLinkEvent evt) {
				testPassed = false;
				
			}

			public void modeHalfDuplex(MsLinkEvent evt) {
				testPassed = false;
				
			}
		});
		
		msSession = msProvider.createSession();
		msLink = msSession.createLink(MsLinkMode.FULL_DUPLEX);
		assertNotNull(msLink);
		assertEquals(MsLinkState.IDLE, msLink.getState());
		
		msLink.join("media/trunk/Announcement/$", "blabla/media/trunk/Conference/$");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<MsLink> links = msSession.getLinks();
		assertEquals(0, links.size());
	}	
}

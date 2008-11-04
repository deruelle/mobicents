package org.mobicents.mscontrol.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
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
}

package org.mobicents.mscontrol.impl;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsConnectionListener;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkEventCause;
import org.mobicents.mscontrol.MsLinkListener;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.mscontrol.MsLinkState;
import org.mobicents.mscontrol.MsSessionEvent;
import org.mobicents.mscontrol.MsSessionListener;

/**
 * 
 * @author amit bhayani
 * 
 */
public class ZMsProviderImplTest extends AbstractTest {

	private static Logger logger = Logger.getLogger(ZMsProviderImplTest.class);

	public ZMsProviderImplTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZMsProviderImplTest.class);
	}

	public void testSessionListener() {
		testPassed = true;
		ProviderMsSessionListener providerMsSessionListener = new ProviderMsSessionListener();
		msProvider.addSessionListener(providerMsSessionListener);
		msProvider.removeSessionListener(providerMsSessionListener);
		msSession = msProvider.createSession();
		assertNotNull(msSession);

	}

	public void testConnectionListener() {
		testPassed = true;
		ProviderMsConnectionListener providerMsConnectionListener = new ProviderMsConnectionListener();
		msProvider.addConnectionListener(providerMsConnectionListener);
		msProvider.removeConnectionListener(providerMsConnectionListener);
		msSession = msProvider.createSession();
		assertNotNull(msSession);
		msConnection = msSession.createNetworkConnection("endpoint1");
		assertNotNull(msConnection);
	}

	public void testLinkListener() {
		testPassed = true;
		ProviderMsLinkListener providerMsLinkListener = new ProviderMsLinkListener();
		msProvider.addLinkListener(providerMsLinkListener);
		msProvider.removeLinkListener(providerMsLinkListener);
		msSession = msProvider.createSession();
		assertNotNull(msSession);
		msLink = msSession.createLink(MsLinkMode.FULL_DUPLEX);
		assertNotNull(msLink);
	}

	public void testGetMsConnection() {
		testPassed = true;
		msSession = msProvider.createSession();
		assertNotNull(msSession);
		msConnection = msSession.createNetworkConnection("media/trunk/Announcement/$");
		msConnection.addConnectionListener(new MsConnectionListener() {

			public void connectionCreated(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionFailed(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				testPassed = true;
				message = "testGetConnection : MsConnectionListenerImpl.connectionHalfOpen called. Expected testPassed = true";
				String connectionId = event.getConnection().getId();
				MsConnection connectionTmp = msProvider.getMsConnection(connectionId);
				assertNotNull(connectionTmp);
				assertEquals(connectionId, connectionTmp.getId());
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testGetConnection : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;

			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;

			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;

			}
		});
		assertNotNull(msConnection);
		msConnection.modify("$", null);
	}

	/**
	 * Test the getMsConnections method of MsProvider. Passing the actual CONF
	 * Endpoint name will fetch all three connections of endpoint
	 */
	public void testGetMsConnections() {
		testPassed = true;
		msSession = msProvider.createSession();
		assertNotNull(msSession);

		msConnection = msSession.createNetworkConnection("media/trunk/Conference/$");
		assertNotNull(msConnection);
		msConnection.addConnectionListener(new MsConnectionListener() {
			MsConnection msConnection2 = null;
			MsConnection msConnection3 = null;

			public void connectionCreated(MsConnectionEvent event) {
				message = "testGetMsConnections : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionDisconnected(MsConnectionEvent event) {
				message = "testGetMsConnections : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
				testPassed = false;
				
				 logger.info("Cleaning connection msConnection2 " + msConnection2.getId());
				// Clean up of other two connections
				msConnection2.release();

				logger.info("Cleaning connection msConnection3 " + msConnection3.getId());
				msConnection3.release();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				 }

			}

			public void connectionFailed(MsConnectionEvent event) {
				logger.warn("Connection Failed " + event.getConnection().getId() + " For end point "
						+ event.getConnection().getEndpoint().getLocalName());
				message = "testGetMsConnections : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionHalfOpen(MsConnectionEvent event) {
				testPassed = false;
				message = "testGetMsConnections : MsConnectionListenerImpl.connectionHalfOpen called. Expected testPassed = false";
				msEndpoint = event.getConnection().getEndpoint();
				assertNotNull(msEndpoint);

				msConnection2 = msSession.createNetworkConnection(msEndpoint.getLocalName());
				assertNotNull(msConnection2);
				msConnection2.modify("$", null);

				msConnection3 = msSession.createNetworkConnection(msEndpoint.getLocalName());
				assertNotNull(msConnection3);
				msConnection3.addConnectionListener(new MsConnectionListener() {

					public void connectionCreated(MsConnectionEvent event) {
						message = "testGetMsConnections : MsConnectionListenerImpl.connectionCreated called. Expected testPassed = false";
						testPassed = false;
					}

					public void connectionDisconnected(MsConnectionEvent event) {
						message = "testGetMsConnections : MsConnectionListenerImpl.connectionDisconnected called. Expected testPassed = false";
						testPassed = false;
					}

					public void connectionFailed(MsConnectionEvent event) {
						message = "testGetMsConnections : MsConnectionListenerImpl.connectionFailed called. Expected testPassed = false";
						testPassed = false;
					}

					public void connectionHalfOpen(MsConnectionEvent event) {
						message = "testGetMsConnections : MsConnectionListenerImpl.connectionHalfOpen called. Expected testPassed = true";
						List<MsConnection> list = msProvider.getMsConnections(msEndpoint.getLocalName());
						assertNotNull(list);

						for (MsConnection c : list) {
							assertNotNull(c);
							logger.info("The connection ID = " + c.getId());
						}
						testPassed = true;
					}

					public void connectionOpen(MsConnectionEvent event) {
						message = "testGetMsConnections : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
						testPassed = false;
					}

					public void connectionModeRecvOnly(MsConnectionEvent event) {
						testPassed = false;

					}

					public void connectionModeSendOnly(MsConnectionEvent event) {
						testPassed = false;

					}

					public void connectionModeSendRecv(MsConnectionEvent event) {
						testPassed = false;

					}
				});
				msConnection3.modify("$", null);
			}

			public void connectionOpen(MsConnectionEvent event) {
				message = "testGetMsConnections : MsConnectionListenerImpl.connectionOpen called. Expected testPassed = false";
				testPassed = false;
			}

			public void connectionModeRecvOnly(MsConnectionEvent event) {
				testPassed = false;

			}

			public void connectionModeSendOnly(MsConnectionEvent event) {
				testPassed = false;

			}

			public void connectionModeSendRecv(MsConnectionEvent event) {
				testPassed = false;

			}
		});
		msConnection.modify("$", null);
	}

	public void testGetMsLinks() {
		msSession = msProvider.createSession();
		msLink = msSession.createLink(MsLinkMode.FULL_DUPLEX);
		msLink.addLinkListener(new MsLinkListener() {

			MsLink msLink2 = null;
			MsLink msLink3 = null;

			public void linkConnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkConnected called. Expected testPassed = false";
				testPassed = false;
				assertEquals(MsLinkEventCause.NORMAL, evt.getCause());

				MsEndpoint[] msEndpoints = evt.getSource().getEndpoints();
				msEndpoint = msEndpoints[1];

				logger.info("Conference endpoint name = " + msEndpoint.getLocalName());

				msLink2 = msSession.createLink(MsLinkMode.FULL_DUPLEX);
				msLink2.join("media/trunk/Announcement/$", msEndpoint.getLocalName());

				msLink3 = msSession.createLink(MsLinkMode.FULL_DUPLEX);
				msLink3.addLinkListener(new MsLinkListener() {

					public void linkConnected(MsLinkEvent evt) {

						List<MsLink> listLinks = msProvider.getMsLinks(msEndpoint.getLocalName());
						for (MsLink c : listLinks) {
							assertNotNull(c);
							logger.info("The Link ID = " + c.getId());
						}
						testPassed = true;
					}

					public void linkCreated(MsLinkEvent evt) {
						testPassed = false;

					}

					public void linkDisconnected(MsLinkEvent evt) {
						testPassed = false;
					}

					public void linkFailed(MsLinkEvent evt) {
						testPassed = false;

					}

					public void modeFullDuplex(MsLinkEvent evt) {
						testPassed = false;

					}

					public void modeHalfDuplex(MsLinkEvent evt) {
						testPassed = false;

					}

				});
				msLink3.join("media/trunk/Announcement/$", msEndpoint.getLocalName());

			}

			public void linkCreated(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkCreated called. Expected testPassed = false";
				testPassed = false;

			}

			public void linkDisconnected(MsLinkEvent evt) {
				message = "testLinkCreated : MsLinkListenerImpl.linkDisconnected called. Expected testPassed = false";
				testPassed = false;

				logger.info("Releasing MsLink2");
				msLink2.release();
				logger.info("Releasing MsLink3");
				msLink3.release();

				// Let us sleep for 2 more secs for cleaning
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

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
		assertNotNull(msLink);
		assertEquals(MsLinkState.IDLE, msLink.getState());

		msLink.join("media/trunk/Announcement/$", "media/trunk/Conference/$");
	}

	private class ProviderMsSessionListener implements MsSessionListener {
		public void sessionActive(MsSessionEvent evt) {
			message = "testSessionListener : ProviderMsSessionListener.sessionActive called. Expected testPassed = false";
			testPassed = false;

		}

		public void sessionCreated(MsSessionEvent evt) {
			message = "testSessionListener : ProviderMsSessionListener.sessionCreated called. Expected testPassed = false";
			testPassed = false;
		}

		public void sessionInvalid(MsSessionEvent evt) {
			message = "testSessionListener : ProviderMsSessionListener.sessionInvalid called. Expected testPassed = false";
			testPassed = false;

		}

	}

	private class ProviderMsConnectionListener implements MsConnectionListener {

		public void connectionCreated(MsConnectionEvent event) {
			message = "testConnectionListener : ProviderMsConnectionListener.connectionCreated called. Expected testPassed = false";
			testPassed = false;

		}

		public void connectionDisconnected(MsConnectionEvent event) {
			message = "testConnectionListener : ProviderMsConnectionListener.connectionDisconnected called. Expected testPassed = false";
			testPassed = false;

		}

		public void connectionFailed(MsConnectionEvent event) {
			message = "testConnectionListener : ProviderMsConnectionListener.connectionFailed called. Expected testPassed = false";
			testPassed = false;

		}

		public void connectionHalfOpen(MsConnectionEvent event) {
			message = "testConnectionListener : ProviderMsConnectionListener.connectionHalfOpen called. Expected testPassed = false";
			testPassed = false;

		}

		public void connectionOpen(MsConnectionEvent event) {
			message = "testConnectionListener : ProviderMsConnectionListener.connectionOpen called. Expected testPassed = false";
			testPassed = false;

		}

		public void connectionModeRecvOnly(MsConnectionEvent event) {
			testPassed = false;

		}

		public void connectionModeSendOnly(MsConnectionEvent event) {
			testPassed = false;

		}

		public void connectionModeSendRecv(MsConnectionEvent event) {
			testPassed = false;

		}

	}

	private class ProviderMsLinkListener implements MsLinkListener {

		public void linkConnected(MsLinkEvent evt) {
			message = "testConnectionListener : ProviderMsLinkListener.linkConnected called. Expected testPassed = false";
			testPassed = false;

		}

		public void linkCreated(MsLinkEvent evt) {
			message = "testConnectionListener : ProviderMsLinkListener.linkCreated called. Expected testPassed = false";
			testPassed = false;
		}

		public void linkDisconnected(MsLinkEvent evt) {
			message = "testConnectionListener : ProviderMsLinkListener.linkDisconnected called. Expected testPassed = false";
			testPassed = false;
		}

		public void linkFailed(MsLinkEvent evt) {
			message = "testConnectionListener : ProviderMsLinkListener.linkFailed called. Expected testPassed = false";
			testPassed = false;
		}

		public void modeFullDuplex(MsLinkEvent evt) {
			testPassed = false;

		}

		public void modeHalfDuplex(MsLinkEvent evt) {
			testPassed = false;

		}

	}
}
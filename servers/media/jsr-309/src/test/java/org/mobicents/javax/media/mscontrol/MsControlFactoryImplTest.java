package org.mobicents.javax.media.mscontrol;

import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import javax.media.mscontrol.MediaConfig;
import javax.media.mscontrol.MediaConfigException;
import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MsControlException;
import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.Parameters;
import javax.media.mscontrol.SupportedFeatures;
import javax.media.mscontrol.mediagroup.MediaGroup;

import junit.framework.TestCase;

import org.mobicents.javax.media.mscontrol.resource.ExtendedParameter;
import org.mobicents.jsr309.mgcp.MgcpStackFactory;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MsControlFactoryImplTest extends TestCase {

	private MsControlFactory msControlFactory = null;
	private javax.media.mscontrol.spi.Driver driver = null;

	public void setUp() throws Exception {

		driver = new org.mobicents.javax.media.mscontrol.spi.DriverImpl();
		msControlFactory = driver.getFactory(null);

	}

	public void tearDown() throws Exception {
		MgcpStackFactory mgcpStackFactory = MgcpStackFactory.getInstance();
		mgcpStackFactory.clearMgcpStackProvider(null);

	}

	public void testMediaSessionFactoryImpl() {
		assertNotNull(msControlFactory);
	}

	public void testCreateParameters() {
		Parameters parameters = msControlFactory.createParameters();
		assertNotNull(parameters);

	}

	public void testcreateMediaSession() throws MsControlException {
		MediaSession mediaSession = msControlFactory.createMediaSession();
		assertNotNull(mediaSession);

		Object o = new Object();
		mediaSession.setAttribute("ATT1", o);

		Object o1 = mediaSession.getAttribute("ATT1");

		assertEquals(o, o1);
	}

	public void testPropertyChange() throws Exception {
		// Property key for the Unique MGCP stack name for this application
		String MGCP_STACK_NAME = "mgcp.stack.name";

		// Property key for the IP address where CA MGCP Stack (SIP Servlet
		// Container) is bound
		String MGCP_STACK_IP = "mgcp.stack.ip";

		// Property key for the port where CA MGCP Stack is bound
		String MGCP_STACK_PORT = "mgcp.stack.port";

		// Property key for the IP address where MGW MGCP Stack (MMS) is bound
		String MGCP_PEER_IP = "mgcp.stack.peer.ip";

		// Property key for the port where MGW MGCP Stack is bound
		String MGCP_PEER_PORT = "mgcp.stack.peer.port";

		String PEER_ADDRESS = "127.0.0.1";
		String LOCAL_ADDRESS = "127.0.0.1";

		String CA_PORT = "4727";
		String MGW_PORT = "5427";

		Properties property = new Properties();
		property.put(MGCP_STACK_NAME, "Test");
		property.put(MGCP_PEER_IP, PEER_ADDRESS);
		property.put(MGCP_PEER_PORT, MGW_PORT);

		property.put(MGCP_STACK_IP, LOCAL_ADDRESS);
		property.put(MGCP_STACK_PORT, CA_PORT);

		MsControlFactory msControlFactorytemp = driver.getFactory(property);
		assertNotNull(msControlFactorytemp);

		MgcpStackFactory mgcpStackFactory = MgcpStackFactory.getInstance();
		mgcpStackFactory.clearMgcpStackProvider(property);

	}

	public void testMediaConfigCreation() throws MediaConfigException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resource-container><parameter><key>ENDPOINT_LOCAL_NAME</key><value>/lla/laa/laa</value></parameter><player><signal><mgcp-event>ann</mgcp-event><mgcp-package>A</mgcp-package><on-endpoint>true</on-endpoint></signal><event><mgcp-event>oc</mgcp-event><mgcp-package>A</mgcp-package><on-endpoint>true</on-endpoint><media-event>PLAY_COMPLETED</media-event></event><event><mgcp-event>of</mgcp-event><mgcp-package>A</mgcp-package><on-endpoint>true</on-endpoint><media-event>PLAY_COMPLETED</media-event></event></player><signal-detector><signal><mgcp-event>dtmf0</mgcp-event><mgcp-package>D</mgcp-package><on-endpoint>false</on-endpoint></signal><event><mgcp-event>dtmf0</mgcp-event><mgcp-package>D</mgcp-package><on-endpoint>true</on-endpoint><media-event>SIGNAL_DETECTED</media-event></event></signal-detector></resource-container>";
		Reader xmlDoc = new StringReader(xml);

		MediaConfigImpl config = (MediaConfigImpl) msControlFactory.getMediaConfig(xmlDoc);
		assertNotNull(config);
	}

	public void testMediaConfigClone() throws MediaConfigException {
		MediaConfig playerConfig = msControlFactory.getMediaConfig(MediaGroup.PLAYER_RECORDER_SIGNALDETECTOR);
		assertNotNull(playerConfig);

		SupportedFeatures supFet = playerConfig.getSupportedFeatures();
		assertNotNull(supFet);

		Parameters pNew = msControlFactory.createParameters();

		assertNotNull(pNew);
		pNew.put(ExtendedParameter.ENDPOINT_LOCAL_NAME, "/thisis/new/endpointname/1");

		MediaConfigImpl clone = (MediaConfigImpl) playerConfig.createCustomizedClone(pNew);
		assertNotNull(clone);

		assertEquals("/thisis/new/endpointname/1", (String) clone.getParameters().get(
				ExtendedParameter.ENDPOINT_LOCAL_NAME));

		assertTrue(clone.isPlayer());
		assertTrue(clone.isRecorder());
		assertTrue(clone.isSignaldetector());
		assertFalse(clone.isSignalgenerator());

	}

}

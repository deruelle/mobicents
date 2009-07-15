package org.mobicents.javax.media.mscontrol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.media.mscontrol.MsControlFactory;
import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.SupportedFeatures;
import javax.media.mscontrol.mediagroup.MediaGroup;
import javax.media.mscontrol.mixer.MediaMixer;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.mobicents.javax.media.mscontrol.resource.ExtendedParameter;
import org.xml.sax.SAXException;

public class XMLParserTest extends TestCase {

	XMLParser parser = null;
	private MsControlFactory msControlFactory = null;
	private javax.media.mscontrol.spi.Driver driver = null;

	public XMLParserTest() {
		super();
	}

	public XMLParserTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		parser = new XMLParser();

		driver = new org.mobicents.javax.media.mscontrol.spi.DriverImpl();
		msControlFactory = driver.getFactory(null);
	}

	public void tearDown() throws Exception {

	}

	public void testParsing() throws ParserConfigurationException, SAXException, IOException {
		// String xml = "<?xml version=\"1.0\"
		// encoding=\"UTF-8\"?><resource-container><parameter><key>ENDPOINT_LOCAL_NAME</key><value>/lla/laa/laa</value></parameter><parameter><key>ON_ENDPOINT</key><value>false</value></parameter></resource-container>";
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resource-container><parameter><key>ENDPOINT_LOCAL_NAME</key><value>/lla/laa/laa</value></parameter><player><signal><mgcp-event>ann</mgcp-event><mgcp-package>A</mgcp-package><on-endpoint>true</on-endpoint></signal><event><mgcp-event>oc</mgcp-event><mgcp-package>A</mgcp-package><on-endpoint>true</on-endpoint><media-event>PLAY_COMPLETED</media-event></event><event><mgcp-event>of</mgcp-event><mgcp-package>A</mgcp-package><on-endpoint>true</on-endpoint><media-event>PLAY_COMPLETED</media-event></event></player><signal-detector><signal><mgcp-event>dtmf0</mgcp-event><mgcp-package>D</mgcp-package><on-endpoint>false</on-endpoint></signal><event><mgcp-event>dtmf0</mgcp-event><mgcp-package>D</mgcp-package><on-endpoint>true</on-endpoint><media-event>SIGNAL_DETECTED</media-event></event></signal-detector></resource-container>";
		InputStream stream = new ByteArrayInputStream(xml.getBytes());

		MediaConfigImpl config = parser.parse(null, stream);

		assertNotNull(config);

		SupportedFeatures features = config.getSupportedFeatures();

		assertNotNull(features);

		Set<Parameter> params = features.getSupportedParameters();

		assertEquals(1, params.size());
		assertEquals(ExtendedParameter.ENDPOINT_LOCAL_NAME, params.iterator().next());

		assertEquals("/lla/laa/laa", (String) config.getParameters().get(ExtendedParameter.ENDPOINT_LOCAL_NAME));

		// test player
		assertTrue(config.isPlayer());
		assertEquals(1, config.getPlayerGeneFactList().size());
		assertEquals(2, config.getPlayerDetFactList().size());

		// test signal detector
		assertTrue(config.isSignaldetector());
		assertEquals(1, config.getSigDeteEveGeneFactList().size());
		assertEquals(1, config.getSigDeteEveDetFactList().size());

		assertFalse(config.isSignalgenerator());

		assertFalse(config.isRecorder());

	}

	public void testMarshall() {
		MediaConfigImpl mediaConfig = MsControlFactoryImpl.configVsMediaConfigMap
				.get(MediaGroup.PLAYER_RECORDER_SIGNALDETECTOR);
		String serializedXml = parser.serialize(mediaConfig);
		
		System.out.println("\n"+ "The xml for MediaGroup.PLAYER_RECORDER_SIGNALDETECTOR \n"+ serializedXml);

		assertNotNull(serializedXml);

		mediaConfig = MsControlFactoryImpl.configVsMediaConfigMap.get(MediaMixer.AUDIO);
		serializedXml = parser.serialize(mediaConfig);
		
		System.out.println("\n"+ "The xml for MediaMixer.AUDIO \n"+serializedXml);

		assertNotNull(serializedXml);

	}

}

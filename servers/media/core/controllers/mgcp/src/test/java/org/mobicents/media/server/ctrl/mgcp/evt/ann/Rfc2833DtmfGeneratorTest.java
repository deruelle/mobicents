package org.mobicents.media.server.ctrl.mgcp.evt.ann;

import static org.junit.Assert.assertEquals;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.ctrl.mgcp.MgcpController;
import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.dtmf.Rfc2833GeneratorFactory;
import org.mobicents.media.server.impl.rtp.RtpHeader;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.resource.Rfc2833Detector;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Rfc2833DtmfGeneratorTest {

	private Timer timer;
	private EndpointImpl sender;
	private EndpointImpl receiver;

	private TestSinkFactory sinkFactory;

	private Rfc2833GeneratorFactory rfc2833Factory;

	private ChannelFactory channelFactory;

	private Semaphore semaphore;

	private MgcpController controller;

	private int count;
	private boolean dtmfReceived = false;
	private boolean end = false;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		dtmfReceived = false;
		end = false;

		semaphore = new Semaphore(0);

		timer = new TimerImpl();
		rfc2833Factory = new Rfc2833GeneratorFactory();
		rfc2833Factory.setName("RFC2833.Detector");

		channelFactory = new ChannelFactory();
		channelFactory.start();

		sender = new EndpointImpl("test/announcement/sender");
		sender.setTimer(timer);

		sender.setSourceFactory(rfc2833Factory);
		sender.setTxChannelFactory(channelFactory);
		sender.start();

		sinkFactory = new TestSinkFactory();

		receiver = new EndpointImpl("test/announcement/receiver");
		receiver.setTimer(timer);

		receiver.setSinkFactory(sinkFactory);
		receiver.setRxChannelFactory(channelFactory);

		receiver.start();

	}

	@Test
	public void testSignal() throws Exception {
		Connection rxConnection = receiver.createLocalConnection(ConnectionMode.RECV_ONLY);
		Connection txConnection = sender.createLocalConnection(ConnectionMode.SEND_ONLY);

		txConnection.setOtherParty(rxConnection);

		RequestIdentifier id = new RequestIdentifier("1");
		NotifiedEntity ne = new NotifiedEntity("localhost");

		Rfc2833DtmfGeneratorFactory factory = new Rfc2833DtmfGeneratorFactory();
		factory.setResourceID("RFC2833.Detector");

		Rfc2833DtmfGenerator signal = (Rfc2833DtmfGenerator) factory.getInstance(controller, "9");

		Request request = new Request(controller, id, sender, ne);

		signal.doVerify(sender);
		signal.start(request);

		System.out.println("Started");
		semaphore.tryAcquire(5, TimeUnit.SECONDS);
		assertEquals(4, count);
		assertEquals(true, dtmfReceived);
		assertEquals(true, end);

		receiver.deleteConnection(rxConnection.getId());
		sender.deleteConnection(txConnection.getId());
	}

	@After
	public void tearDown() {
	}

	private class TestSinkFactory implements ComponentFactory {

		public Component newInstance(Endpoint endpoint) {
			return new TestSink("test-sink");
		}

	}

	private class TestSink extends AbstractSink {

		public TestSink(String name) {
			super(name);
		}

		public Format[] getFormats() {
			return new Format[] { Rfc2833Detector.DTMF };
		}

		public boolean isAcceptable(Format format) {
			return true;
		}

		public void receive(Buffer buffer) {
			count++;

			RtpHeader rtpHeader = (RtpHeader) buffer.getHeader();
			byte[] data = (byte[]) buffer.getData();
			if (rtpHeader.getMarker()) {
				String digit = Rfc2833Detector.TONE[data[0]];
				if ("9".equals(digit)) {
					dtmfReceived = true;
				}
			}
			end = (data[1] & 0x80) != 0;
		}

	}
}

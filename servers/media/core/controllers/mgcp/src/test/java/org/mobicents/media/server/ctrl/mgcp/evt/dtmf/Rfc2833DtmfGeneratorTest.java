package org.mobicents.media.server.ctrl.mgcp.evt.dtmf;

import java.io.IOException;
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
import org.mobicents.media.server.ConnectionFactory;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.ctrl.mgcp.MgcpController;
import org.mobicents.media.server.ctrl.mgcp.Request;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.dtmf.Rfc2833GeneratorFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

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

                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setRxChannelFactory(channelFactory);
                connectionFactory.setTxChannelFactory(channelFactory);
                
		sender.setSourceFactory(rfc2833Factory);
		sender.setConnectionFactory(connectionFactory);
		sender.start();

		sinkFactory = new TestSinkFactory();

		receiver = new EndpointImpl("test/announcement/receiver");
		receiver.setTimer(timer);

		receiver.setSinkFactory(sinkFactory);
		receiver.setConnectionFactory(connectionFactory);

		receiver.start();

	}

	@Test
	public void testSignal() throws Exception {
		Connection rxConnection = receiver.createLocalConnection(ConnectionMode.RECV_ONLY);
		Connection txConnection = sender.createLocalConnection(ConnectionMode.SEND_ONLY);

		txConnection.setOtherParty(rxConnection);

		RequestIdentifier id = new RequestIdentifier("1");
		NotifiedEntity ne = new NotifiedEntity("localhost");

		DtmfGeneratorFactory factory = new DtmfGeneratorFactory();
		factory.setResourceName("RFC2833.Detector");
                factory.setDigit("6");

		DtmfGenerator signal = (DtmfGenerator) factory.getInstance(controller, "");

		Request request = new Request(controller, id, null, sender, ne);

		signal.doVerify(sender);
		signal.start(request);

		System.out.println("Started");
		semaphore.tryAcquire(5, TimeUnit.SECONDS);
//		assertEquals(6, count);
		assertEquals(true, dtmfReceived);

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
			return new Format[] { AVProfile.DTMF };
		}

		public boolean isAcceptable(Format format) {
			return true;
		}

		public void receive(Buffer buffer) {
			count++;
                        System.out.println("======== Recv");
			byte[] data = (byte[]) buffer.getData();
				String digit = "6";
//				String digit = DtmfDetector.TONE[data[0]];
				if ("6".equals(digit)) {
					dtmfReceived = true;
				}
			end = (data[1] & 0x80) != 0;
		}

        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;
import org.mobicents.media.server.local.management.EndpointLocalManagement;

import static org.junit.Assert.*;

import org.mobicents.media.server.spi.ConnectionListener;
import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.ConnectionState;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpConnectionImplTest {

    public final static int TEST_DURATION = 20;
    private int count;
    private ArrayList packets;
    private ConnectionListener cListener=new HollowConnectionListener();
    public RtpConnectionImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        packets = new ArrayList();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setState method, of class RtpConnectionImpl.
     */
    @Test
    public void testSetState() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            RtpConnectionImpl con = new RtpConnectionImpl(enp, ConnectionMode.SEND_RECV);
            assertEquals(ConnectionState.HALF_OPEN, con.getState());

            con.setState(ConnectionState.OPEN);
            assertEquals(ConnectionState.OPEN, con.getState());

            con.setState(ConnectionState.CLOSED);
            assertEquals(ConnectionState.CLOSED, con.getState());
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of getLocalDescriptor method, of class RtpConnectionImpl.
     */
    @Test
    public void testGetLocalDescriptor() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            RtpConnectionImpl con = new RtpConnectionImpl(enp, ConnectionMode.SEND_RECV);
            String sdp = con.getLocalDescriptor();

            if (sdp.indexOf("v=0") == -1) {
                fail("v=0--missing");
            }

            if (sdp.indexOf("o=MediaServer") == -1) {
                fail("o=MediaServer missing");
            }

            if (sdp.indexOf("RTP/AVP 0 8") == -1) {
                fail("RTP/AVP 0 8");
            }

            if (sdp.indexOf("a=rtpmap:0 pcmu/8000") == -1) {
                fail("a=rtpmap:0 pcmu/8000 missing");
            }

            if (sdp.indexOf("a=rtpmap:8 pcma/8000") == -1) {
                fail("a=rtpmap:8 pcma/8000 missing");
            }
            System.out.println(sdp);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of setRemoteDescriptor method, of class RtpConnectionImpl.
     */
    @Test
    public void testSetRemoteDescriptor() throws Exception {
        String sdp =
                "v=0\n" +
                "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
                "s=session\n" +
                "c=IN IP4 192.168.1.2\n" +
                "t=0 0\n" +
                "m=audio 64535 RTP/AVP 0 8\n" +
                "a=rtpmap:0 pcmu/8000\n" +
                "a=rtpmap:8 pcma/8000";


        TestEndpoint enp = new TestEndpoint("test");
        try {
            RtpConnectionImpl con = new RtpConnectionImpl(enp, ConnectionMode.SEND_RECV);
            con.setRemoteDescriptor(sdp);
            assertEquals(ConnectionState.OPEN, con.getState());
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test of setOtherParty method, of class LocalConnectionImpl.
     */
    @Test
    public void testJoin() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");

        RtpConnectionImpl con1 = null;
        RtpConnectionImpl con2 = null;

        try {
            con1 = new RtpConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new RtpConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }

        try {
            String sdp1 = con1.getLocalDescriptor();
            String sdp2 = con1.getLocalDescriptor();

            con1.setRemoteDescriptor(sdp2);
            con2.setRemoteDescriptor(sdp1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(ConnectionState.OPEN, con1.getState());
        assertEquals(ConnectionState.OPEN, con2.getState());
    }

    /**
     * Test of setOtherParty method, of class LocalConnectionImpl.
     */
    @Test
    @SuppressWarnings("static-access")
    public void testTransmission() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");

        RtpConnectionImpl con1 = null;
        RtpConnectionImpl con2 = null;

        try {
            con1 = new RtpConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new RtpConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }

        try {
            String sdp1 = con1.getLocalDescriptor();
            String sdp2 = con2.getLocalDescriptor();

            con1.setRemoteDescriptor(sdp2);
            con2.setRemoteDescriptor(sdp1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertEquals(ConnectionState.OPEN, con1.getState());
        assertEquals(ConnectionState.OPEN, con2.getState());

        Source src = new Source();
        con1.getMux().connect(src);
        con1.getMux().getOutput().start();
        
        Sink sink = new Sink();
        con2.getDemux().connect(sink);
        con2.getDemux().start();
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        //this.checkSeq();

    }

    private void checkSeq() {
        if (packets.size() == 0) {
            fail("Empty buffer");
        }
        for (int i = 0; i < packets.size() - 1; i++) {
            Buffer b1 = (Buffer) packets.get(i);
            Buffer b2 = (Buffer) packets.get(i + 1);
            if ((b2.getSequenceNumber() - b1.getSequenceNumber()) != 1) {
                fail("Wrong seq");
            }
        }
    }

    /**
     * Test of close method, of class RtpConnectionImpl.
     */
    @Test
    public void testClose() {
        String sdp =
                "v=0\n" +
                "o=MediaServer 5334424 5334424 IN IP4 192.168.1.2\n" +
                "s=session\n" +
                "c=IN IP4 192.168.1.2\n" +
                "t=0 0\n" +
                "m=audio 64535 RTP/AVP 0 8\n" +
                "a=rtpmap:0 pcmu/8000\n" +
                "a=rtpmap:8 pcma/8000";


        TestEndpoint enp = new TestEndpoint("test");
        try {
            RtpConnectionImpl con = new RtpConnectionImpl(enp, ConnectionMode.SEND_RECV);
            con.setRemoteDescriptor(sdp);

            assertEquals(ConnectionState.OPEN, con.getState());

            con.close();
            assertEquals(ConnectionState.CLOSED, con.getState());
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    public class TestEndpoint extends BaseEndpoint {

        public TestEndpoint(String localName) {
            super(localName);
        }

        @Override
        public HashMap initMediaSources() {
            return new HashMap();
        }

        @Override
        public HashMap initMediaSinks() {
            return new HashMap();
        }

		public String[] getEndpointNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public EndpointLocalManagement[] getEndpoints() {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getSupportedPackages() {
			// TODO Auto-generated method stub
			return null;
		}
    }

    private class Source extends AbstractSource implements Runnable {

        private Timer timer = new Timer();
        private int seq;
        private AudioFormat f = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

        public Source() {
        	super("rtpConnectionImplTest.Source");
            timer.setListener(this);
        }

        public void start() {
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        public void run() {
            Buffer buffer = new Buffer();
            buffer.setData(new byte[320]);
            buffer.setOffset(0);
            buffer.setLength(320);
            buffer.setDuration(Quartz.HEART_BEAT);
            buffer.setSequenceNumber(seq);
            buffer.setTimeStamp(seq * Quartz.HEART_BEAT);
            buffer.setFormat(f);

            if (sink != null) {
                sink.receive(buffer);
                count++;
            }
        }

        public Format[] getFormats() {
            return new Format[]{f};
        }
    }

    private class Sink extends AbstractSink {
    	
    	public Sink(){
    		super("RtpConnectionImplTest.Sink");
    	}

        private AudioFormat f = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

        public void receive(Buffer buffer) {
            packets.add(buffer);
        }

        public Format[] getFormats() {
            return new Format[]{f};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(f);
        }
    }
}

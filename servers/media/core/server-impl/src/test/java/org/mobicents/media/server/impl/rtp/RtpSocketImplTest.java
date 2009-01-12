/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.rtp;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import static org.junit.Assert.*;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;
import org.mobicents.media.server.impl.clock.TimerTask;
import org.mobicents.media.server.impl.enp.ann.AnnEndpointImpl;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpSocketImplTest {

    private final static int TEST_DURATION = 20;
    private final static double ERRORS = 5;
    private int MAX_ERRORS;
    private RtpSocketImpl serverSocket;
    private RtpSocketImpl clientSocket;
    private int period = 20;
    private int jitter = 40;
    private InetAddress localAddress;
    private AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW,8000, 8,1);
    private ArrayList packets = new ArrayList();
    private int errorCount;
    private BaseEndpoint endpoint;
    
    public RtpSocketImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {        
        int ps = 1000 / Quartz.HEART_BEAT;
        MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);
        try {
            endpoint = new AnnEndpointImpl("test");
            endpoint.start();
            
            localAddress = InetAddress.getLocalHost();
        } catch (Exception e) {
        }
        HashMap<Integer,Format> rtpMap = new HashMap();
        rtpMap.put(8, PCMA);
        
        serverSocket = new RtpSocketImpl(endpoint, period, jitter, rtpMap);
        clientSocket = new RtpSocketImpl(endpoint, period, jitter, rtpMap);
    }

    @After
    public void tearDown() {
        serverSocket.close();
        clientSocket.close();
        endpoint.stop();
    }

    /**
     * Test of init method, of class RtpSocketImpl.
     */
    @Test
    public void init() throws Exception {
        serverSocket.init(localAddress, 2828, 2828);
        int port = serverSocket.getPort();
        assertEquals(2828, port);

        try {
            port = clientSocket.init(localAddress, 2828, 2828);
            fail("Unexpected resource");
        } catch (SocketException e) {
        }
        serverSocket.close();
    }

    @Test
    @SuppressWarnings("static-access")
    public void close() throws Exception {
        try {
            Thread.currentThread().sleep(500);
        } catch (Exception e) {
        }
        serverSocket.init(localAddress, 2828, 2828);
        serverSocket.close();

        try {
            serverSocket.init(localAddress, 2828, 2828);
            serverSocket.close();
        } catch (SocketException e) {
            fail("Previuos socket was not closed");
        }
    }


    /**
     * Test of getPayloadType method, of class RtpSocketImpl.
     */
    @Test
    public void getPayloadType() {
        //serverSocket.addFormat(1, new AudioFormat("Test"));
        if (!serverSocket.getRtpMap().containsKey(8)) {
            fail("Expected payload number: 8");
        }

        Format f = (Format) serverSocket.getRtpMap().get(8);
        if (!f.equals(PCMA)) {
            fail("Expected format :" + PCMA);
        }

        int pt = serverSocket.getPayloadType(PCMA);
        assertEquals(8, pt);
    }

    @Test
    @SuppressWarnings("static-access")
    public void testTransmission() throws Exception {
        int p1 = serverSocket.init(localAddress, 1024, 65535);
        int p2 = clientSocket.init(localAddress, 1024, 65535);

//        serverSocket.addFormat(1, PCMA);
//        clientSocket.addFormat(1, PCMA);

        serverSocket.setPeer(localAddress, p2);
        clientSocket.setPeer(localAddress, p1);


        serverSocket.getReceiveStream().connect(new Receiver());

        Sender sender = new Sender();
        clientSocket.getSendStream().connect(sender);
        sender.start();
        
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        sender.stop();
        
        serverSocket.close();
        clientSocket.close();

        int k = 0;
        for (int i = 0; i < packets.size(); i++) {
            if (packets.get(i) != null) {
                k = i;
                break;
            }
        }
        
        long exp = 1;
        for (int i = k; i < packets.size(); i++) {
            Buffer buffer = (Buffer) packets.get(i);
            String s = new String((byte[]) buffer.getData(), buffer.getOffset(), buffer.getLength());
            int seq = 0;
            try {
                seq = Integer.parseInt(s);
            } catch (Exception e) {
                errorCount++;
                continue;
            }
            if (exp != seq) {
                exp = seq + 1;
                errorCount++;
            } else {
                exp++;
            }
        }

        if (errorCount > MAX_ERRORS) {
            fail("Too many errors: " + errorCount + ", max=" + MAX_ERRORS);
        }
        System.out.println("Total errors: " + errorCount + ", max=" + MAX_ERRORS);        
    }

    private class Sender extends AbstractSource implements TimerTask {

        private int seq = 0;
        private Timer timer = new Timer();
        
        public Sender() {
        	super("RtpSocketImplTest.Source");
            timer.setListener(this);
        }
        
        public void run() {
            seq++;
            byte[] data = Integer.toString(seq).getBytes();
            Buffer buffer = new Buffer();
            buffer.setFormat(Codec.PCMA);
            buffer.setSequenceNumber(seq);
            buffer.setTimeStamp(seq * 20);
            buffer.setData(data);
            buffer.setOffset(0);
            buffer.setLength(data.length);

            if (sink != null) {
                sink.receive(buffer);
            }
        }

        public void start() {
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        public Format[] getFormats() {
            return new Format[] {PCMA};
        }

        public void started() {
        }

        public void ended() {
        }
        
    }

    private class Receiver extends AbstractSink {
    	
    	public Receiver(){
    		super("RtpSocketImplTest.Receiver");
    	}

        public void receive(Buffer buffer) {
            packets.add(buffer);
        }

        public Format[] getFormats() {
            return new Format[] {PCMA};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(PCMA);
        }

    }
}

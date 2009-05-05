package org.mobicents.media.server.impl.rtp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.dsp.Codec;

public class RtpSocketTest {

	private final static int TEST_DURATION = 2;
	private final static double ERRORS = 5;
	private int MAX_ERRORS;
	private int HEART_BEAT = 20;
	
	private RtpFactory rtpfactory = null;
	
	private RtpSocket serverSocket;
	private RtpSocket clientSocket;
	private int period = 20;
	private int jitter = 40;
	private InetAddress localAddress;
	private AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);

	private ArrayList packets = new ArrayList();
	private int errorCount;

	private TimerImpl timer = null;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		int ps = 1000 / HEART_BEAT;
		MAX_ERRORS = (int) Math.round(100.0 / ps * ERRORS);

		timer = new TimerImpl();
		timer.setHeartBeat(HEART_BEAT);
		
		rtpfactory = new RtpFactory();

		try {
			localAddress = InetAddress.getLocalHost();
		} catch (Exception e) {
		}
		HashMap<Integer, Format> rtpMap = new HashMap();
		rtpMap.put(8, PCMA);

		serverSocket = new RtpSocket(timer, rtpMap, rtpfactory);
		clientSocket = new RtpSocket(timer, rtpMap, rtpfactory);
	}
	
	   /**
     * Test of init method, of class RtpSocketImpl.
     */
    @Test
    public void init() throws Exception {
        serverSocket.init(localAddress, 2828, 2828);
        int port = serverSocket.getLocalPort();
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
    
    @Test
    @SuppressWarnings("static-access")
    public void testTransmission() throws Exception {
        int p1 = serverSocket.init(localAddress, 1024, 65535);
        int p2 = clientSocket.init(localAddress, 1024, 65535);
        
        System.out.println("p1 = "+p1);
        System.out.println("p2 = "+p2);

//        serverSocket.addFormat(1, PCMA);
//        clientSocket.addFormat(1, PCMA);

        serverSocket.setPeer(localAddress, p2);
        clientSocket.setPeer(localAddress, p1);


        serverSocket.getReceiveStream().connect(new Receiver());
        serverSocket.getReceiveStream().start();

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
            String s = new String((byte[]) buffer.getData());
            System.out.println("|"+s+"|aaaa");
            int seq = 0;
            try {
                seq = Integer.parseInt(s);
                System.out.println(seq);
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
    
    private class Receiver extends AbstractSink {
    	
    	public Receiver(){
    		super("RtpSocketImplTest.Receiver");
    	}

        public void receive(Buffer buffer) {
        	byte[] bb = (byte[])buffer.getData();
        	System.out.println("Received = "+buffer +" data = "+new String(bb)  );
            packets.add(buffer);
        }

        public Format[] getFormats() {
            return new Format[] {PCMA};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(PCMA);
        }
    }
    
    private class Sender extends AbstractSource implements Runnable {

        private int seq = 0;
        private TimerImpl timer = new TimerImpl();
        private ScheduledFuture task = null;
        
        public Sender() {
        	super("RtpSocketImplTest.Source");           
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
            
            System.out.println("Sending "+ buffer);

            if (otherParty != null) {
            	otherParty.receive(buffer);
            }
        }

        public void start() {
        	task = timer.synchronize(this);
        }

        public void stop() {
        	task.cancel(false);
        }

        public Format[] getFormats() {
            return new Format[] {PCMA};
        }        
    }    
}

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
public class LocalConnectionImplTest {

    public final static int TEST_DURATION = 20;
    
    private int count;
    private ArrayList packets;
    private ConnectionListener cListener=new HollowConnectionListener(this);
    protected boolean modeChanged= false;
    
    public LocalConnectionImplTest() {
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

    @Test
    public void testGetEndpoint() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            LocalConnectionImpl con = new LocalConnectionImpl(enp, ConnectionMode.SEND_RECV);
            assertEquals(enp, con.getEndpoint());
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
    }
    /**
     * Test of setState method, of class LocalConnectionImpl.
     */
    @Test
    public void testSetState() {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            LocalConnectionImpl con = new LocalConnectionImpl(enp, ConnectionMode.SEND_RECV);
            assertEquals(ConnectionState.HALF_OPEN, con.getState());
            
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testModeChanged() throws Exception {
        TestEndpoint enp = new TestEndpoint("test");
        try {
            LocalConnectionImpl con = new LocalConnectionImpl(enp, ConnectionMode.SEND_RECV);
            con.addListener(cListener);
            con.setMode(ConnectionMode.RECV_ONLY);
            assertEquals(true, modeChanged);
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
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        assertEquals(con2, con1.getOtherParty());
        assertEquals(con1, con2.getOtherParty());
        
        assertEquals(ConnectionState.OPEN, con1.getState());
        assertEquals(ConnectionState.OPEN, con2.getState());
    }

    @Test
    public void testDrop() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
                
        assertEquals(con2, con1.getOtherParty());
        assertEquals(con1, con2.getOtherParty());
        
        assertEquals(ConnectionState.OPEN, con1.getState());
        assertEquals(ConnectionState.OPEN, con2.getState());
        
        System.out.println("Deleting connection");
        con1.close();
        
        assertEquals(null, con1.getOtherParty());
        assertEquals(null, con2.getOtherParty());
        
        assertEquals(ConnectionState.CLOSED, con1.getState());
        assertEquals(ConnectionState.CLOSED, con2.getState());
        
    }
    

    @Test
    public void testClose() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
                
        assertEquals(con2, con1.getOtherParty());
        assertEquals(con1, con2.getOtherParty());
        
        assertEquals(ConnectionState.OPEN, con1.getState());
        assertEquals(ConnectionState.OPEN, con2.getState());
        
        con1.close();

        assertEquals(null, con1.getOtherParty());
        assertEquals(null, con2.getOtherParty());
        
        assertEquals(ConnectionState.CLOSED, con1.getState());
        assertEquals(ConnectionState.CLOSED, con2.getState());
        
    }
    
    @Test
    @SuppressWarnings("static-access")
    public void testTransmission() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        Source src = new Source();
            con1.getMux().connect(src);
        
        Sink sink = new Sink();
        try {
            for (int i = 0; i < sink.getFormats().length; i++) {
                System.out.println("F=" + sink.getFormats()[i]);
            }
            con2.getDemux().connect(sink);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        src.stop();
        con1.getMux().disconnect(src);
        con2.getDemux().disconnect(sink); 
        
        this.checkSeq();
    }

    @Test
    @SuppressWarnings("static-access")
    public void testHalfDuplex() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        Source src = new Source();
            con1.getMux().connect(src);
        
        Sink sink = new Sink();
        try {
            for (int i = 0; i < sink.getFormats().length; i++) {
                System.out.println("F=" + sink.getFormats()[i]);
            }
            con2.getDemux().connect(sink);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        src.stop();
        con1.getMux().disconnect(src);
        con2.getDemux().disconnect(sink); 
        
        this.checkSeq();
    }

    @Test
    @SuppressWarnings("static-access")
    public void testHalfDuplex2() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.RECV_ONLY);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        Source src = new Source();
            con1.getMux().connect(src);
        
        Sink sink = new Sink();
        try {
            for (int i = 0; i < sink.getFormats().length; i++) {
                System.out.println("F=" + sink.getFormats()[i]);
            }
            con2.getDemux().connect(sink);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        src.stop();
        con1.getMux().disconnect(src);
        con2.getDemux().disconnect(sink); 
        
        assertEquals(0, packets.size());
    }
    
    @Test
    @SuppressWarnings("static-access")
    public void testHalfDuplex3() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.SEND_ONLY);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_ONLY);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        Source src = new Source();
            con1.getMux().connect(src);
        
        Sink sink = new Sink();
        try {
            for (int i = 0; i < sink.getFormats().length; i++) {
                System.out.println("F=" + sink.getFormats()[i]);
            }
            con2.getDemux().connect(sink);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        src.stop();
        con1.getMux().disconnect(src);
        con2.getDemux().disconnect(sink); 
        
        assertEquals(0, packets.size());
    }
    
    @Test
    @SuppressWarnings("static-access")
    public void testHalfDuplex4() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.SEND_RECV);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.SEND_RECV);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        Source src = new Source();
            con1.getMux().connect(src);
        
        Sink sink = new Sink();
        try {
            for (int i = 0; i < sink.getFormats().length; i++) {
                System.out.println("F=" + sink.getFormats()[i]);
            }
            con2.getDemux().connect(sink);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.stop();
                
        this.checkSeq();        
        con1.setMode(ConnectionMode.RECV_ONLY);
        packets.clear();
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        
        src.stop();
        con1.getMux().disconnect(src);
        con2.getDemux().disconnect(sink); 
        
        enp1.deleteAllConnections();
        enp2.deleteAllConnections();
        
        if (packets.size() > 10) {
            fail("Only few packet can arrive, but in fact: " + packets.size());
        }
    }

    @Test
    @SuppressWarnings("static-access")
    public void testHalfDuplex5() {
        TestEndpoint enp1 = new TestEndpoint("test/1");
        TestEndpoint enp2 = new TestEndpoint("test/2");
        
        LocalConnectionImpl con1 = null;
        LocalConnectionImpl con2 = null;
        
        try {
            con1 = new LocalConnectionImpl(enp1, ConnectionMode.RECV_ONLY);
            con2 = new LocalConnectionImpl(enp2, ConnectionMode.RECV_ONLY);
        } catch (ResourceUnavailableException e) {
            fail(e.getMessage());
        }
        
        try {
            con1.setOtherParty(con2);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        Source src = new Source();
            con1.getMux().connect(src);
        
        Sink sink = new Sink();
        try {
            con2.getDemux().connect(sink);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        src.stop();
        assertEquals(0, packets.size());
                
        con1.setMode(ConnectionMode.RECV_ONLY);
        con1.setMode(ConnectionMode.SEND_ONLY);
        packets.clear();
        
        src.start();
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        
        src.stop();
        con1.getMux().disconnect(src);
        con2.getDemux().disconnect(sink); 
        
        enp1.deleteAllConnections();
        enp2.deleteAllConnections();
        
        this.checkSeq();        
        
    }
    
    private void checkSeq() {
        if (packets.size() == 0) {
            fail("Empty buffer");
        }
        for (int i = 0; i < packets.size() - 1; i++) {
            Buffer b1 = (Buffer)packets.get(i);
            Buffer b2 = (Buffer)packets.get(i +  1);
            if ((b2.getSequenceNumber() - b1.getSequenceNumber()) != 1) {
                fail("Wrong seq");
            }
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
        
        private AudioFormat f = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
        
        public Source() {
        	super("LocalConnectionImplTest.Source");
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
            buffer.setSequenceNumber(seq++);
            buffer.setTimeStamp(seq*Quartz.HEART_BEAT);
            buffer.setFormat(f);
            
            if (sink != null) {
                sink.receive(buffer);
                count++;
            }
        }

        public Format[] getFormats() {
            return new Format[] {f};
        }
        
    }
    
    private class Sink extends AbstractSink {

    	public Sink(){
    		super("LocalConnectionImpl.Sink");
    	}
    	
        private AudioFormat f = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);

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

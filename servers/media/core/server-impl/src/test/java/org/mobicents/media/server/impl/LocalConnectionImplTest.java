/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl;

import java.util.ArrayList;
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
import org.mobicents.media.server.impl.common.ConnectionMode;
import static org.junit.Assert.*;
import org.mobicents.media.server.impl.common.ConnectionState;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author Oleg Kulikov
 */
public class LocalConnectionImplTest {

    public final static int TEST_DURATION = 20;
    
    private int count;
    private ArrayList packets;
    
    
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
        
        try {
            con1.setOtherParty(null);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
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
    }
    
    private class Source extends AbstractSource implements Runnable {

        private Timer timer = new Timer();
        private int seq;
        
        private AudioFormat f = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
        
        public Source() {
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
            buffer.setSequenceNumber(seq);
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
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
import static org.junit.Assert.*;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.clock.Quartz;
import org.mobicents.media.server.impl.clock.Timer;
import org.mobicents.media.server.impl.clock.TimerTask;

/**
 *
 * @author Oleg Kulikov
 */
public class DemultiplexerTest {

    private int count;
    private final static int TEST_DURATION = 20;
    
    private ArrayList buff1 = new ArrayList();
    private ArrayList buff2 = new ArrayList();
    
    private Demultiplexer demux;
    
    public DemultiplexerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    	 demux = new Demultiplexer(new Format[]{new AudioFormat("F1")}, "DemultiplexerTest ");
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInput method, of class Demultiplexer.
     */
    @Test
    public void testDemux() {
        progress(TEST_DURATION);
        
        System.out.println("Checking buff1");
        checkSeq(buff1);
        
        System.out.println("Checking buff2");
        checkSeq(buff2);
    }

    @SuppressWarnings("static-access")
    private void progress(int duration) {
        Source s = new Source();
        
        s.start();
        
            demux.getInput().connect(s);
            demux.start();
        
        Sink1 sink1 = new Sink1();
        Sink2 sink2 = new Sink2();
        
            demux.connect(sink1);
            demux.connect(sink2);
        try {
            Thread.currentThread().sleep(duration * 1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
        demux.stop();
        s.stop();
    }
    
    private void checkSeq(ArrayList buff) {
        for (int i = 0; i < buff.size() - 1; i++) {
            Buffer b1 = (Buffer)buff.get(i);
            Buffer b2 = (Buffer)buff.get(i +  1);
            if ((b2.getSequenceNumber() - b1.getSequenceNumber()) != 1) {
                fail("Current seq=" + b1.getSequenceNumber() + " next seq=" + 
                        b2.getSequenceNumber());
            }
        }
    }
    /**
     * Test of connect method, of class Demultiplexer.
     */
    @Test
    public void testConnect() {
        Sink1 sink1 = new Sink1();
        Sink2 sink2 = new Sink2();

            demux.connect(sink1);        
            assertEquals(1, demux.getBranchCount());

            demux.connect(sink2);        
            assertEquals(2, demux.getBranchCount());  
        
            demux.connect(sink2);
            assertEquals(2, demux.getBranchCount());  
    }
    
    /**
     * Test of disconnect method, of class Demultiplexer.
     */
    @Test
    public void testDisconnect() {
        Sink1 sink1 = new Sink1();
        Sink2 sink2 = new Sink2();

            demux.connect(sink1);        
            assertEquals(1, demux.getBranchCount());
        
            demux.connect(sink2);        
            assertEquals(2, demux.getBranchCount()); 
        
        demux.disconnect(sink2);
        assertEquals(1, demux.getBranchCount());
        
        demux.disconnect(sink1);
        assertEquals(0, demux.getBranchCount());
    }

 /*   @Test
    public void testGetFormat() {
        AudioFormat f = new AudioFormat("F1");
        assertEquals(1,demux.getFormats().length);
        assertEquals(f,demux.getFormats()[0]);
    }*/
    
    private class Source extends AbstractSource implements TimerTask {

        private Timer timer = new Timer();
        private int seq;
        
        private AudioFormat f = new AudioFormat("F1");
        
        public Source() {
        	super("DemultiplexerTest.Source");
            timer.setListener(this);
        }
        
        public Source(AudioFormat f) {
        	super("DemultiplexerTest.Source");
            timer.setListener(this);
            this.f = f;
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
            seq++;
            if (sink != null) {
                sink.receive(buffer);
                count++;
            }
        }

        public Format[] getFormats() {
            return new Format[]{f};
        }

        public void started() {
        }

        public void ended() {
        }
        
    }
    
    private class Sink1 extends AbstractSink {
    	
    	public Sink1(){
    		super("DemultiplexerTest.Sink1");
    	}

        private AudioFormat f = new AudioFormat("F1");

        public void receive(Buffer buffer) {
            buff1.add(buffer);
        }

        public Format[] getFormats() {
            return new Format[]{f};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(format);
        }
        
    }

    private class Sink2 extends AbstractSink {

    	public Sink2(){
    		super("DemultiplexerTest.Sink2");
    	}
    	
        private AudioFormat f = new AudioFormat("F1");

        public void receive(Buffer buffer) {
            buff2.add(buffer);
        }
        
        public Format[] getFormats() {
            return new Format[]{f};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(format);
        }
        
        
    }
    
}
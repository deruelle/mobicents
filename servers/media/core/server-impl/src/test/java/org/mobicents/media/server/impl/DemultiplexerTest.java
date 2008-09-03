/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl;

import java.io.IOException;
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
        demux = new Demultiplexer();
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
        
        try {
            demux.getInput().connect(s);
            demux.start();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        Sink1 sink1 = new Sink1();
        Sink2 sink2 = new Sink2();
        
        try {
            demux.connect(sink1);
            demux.connect(sink2);
        } catch (IOException e) {
            fail(e.getMessage());
        }
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

        try {
            demux.connect(sink1);        
            assertEquals(1, demux.getBranchCount());
        } catch (IOException e) {
            fail(e.getMessage());
        }

        try {
            demux.connect(sink2);        
            assertEquals(2, demux.getBranchCount());  
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        try {
            demux.connect(sink2);
            assertEquals(2, demux.getBranchCount());  
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
    
    /**
     * Test of disconnect method, of class Demultiplexer.
     */
    @Test
    public void testDisconnect() {
        Sink1 sink1 = new Sink1();
        Sink2 sink2 = new Sink2();

        try {
            demux.connect(sink1);        
            assertEquals(1, demux.getBranchCount());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        try {
            demux.connect(sink2);        
            assertEquals(2, demux.getBranchCount()); 
        } catch (IOException e) {
            fail(e.getMessage());
        }
        
        demux.disconnect(sink2);
        assertEquals(1, demux.getBranchCount());
        
        demux.disconnect(sink1);
        assertEquals(0, demux.getBranchCount());
    }

    @Test
    public void testGetFormatWithoutSource() {
        assertEquals(null, demux.getFormats());
    }
    
    @Test
    public void testGetFormatWithSource() {
        AudioFormat f = new AudioFormat("test");
        Source s = new Source(f);
        
        try {
            s.connect(demux.getInput());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(1,demux.getFormats().length);
        assertEquals(f,demux.getFormats()[0]);
    }
    
    private class Source extends AbstractSource implements Runnable {

        private Timer timer = new Timer();
        private int seq;
        
        private AudioFormat f = new AudioFormat("F1");
        
        public Source() {
            timer.setListener(this);
        }
        
        public Source(AudioFormat f) {
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
        
    }
    
    private class Sink1 extends AbstractSink {

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
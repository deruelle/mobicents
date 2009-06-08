/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.dsp;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Utils;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.CodecFactory;

/**
 *
 * @author Oleg Kulikov
 */
public class ProcessorTest {

    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR,
            8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat DTMF = new AudioFormat("telephone-event/8000");
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    
    
    private final static Format[] FORMATS = new Format[]{LINEAR_AUDIO, PCMA, PCMU, SPEEX, G729, GSM, DTMF};
    
    private Processor dsp;
    
    private CodecFactory pcmaEncoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.alaw.EncoderFactory();
    private CodecFactory pcmaDecoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.alaw.DecoderFactory();
    
    private CodecFactory pcmuEncoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.ulaw.EncoderFactory();
    private CodecFactory pcmuDecoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.ulaw.DecoderFactory();
    
    private DspFactory dspFactory = new DspFactory();
    private Semaphore semaphore = new Semaphore(0);
    
    private ArrayList<Buffer> list;
    
    public ProcessorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        ArrayList<CodecFactory> codecFactories = new ArrayList();
        codecFactories.add(pcmaEncoderFactory);
        codecFactories.add(pcmaDecoderFactory);
        codecFactories.add(pcmuEncoderFactory);
        codecFactories.add(pcmuDecoderFactory);
        
        dspFactory.setName("test");
        dspFactory.setCodecFactories(codecFactories);
        dsp = (Processor) dspFactory.newInstance(null);
        
        list = new ArrayList();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInputFormats() {
        TestSink sink = new TestSink();
        dsp.getOutput().connect(sink);
        
        Format[] fmts = dsp.getInput().getFormats();
        
        Format[] expected = new Format[] {PCMA, DTMF};
        assertEquals(true, Utils.checkFormats(fmts, expected));
    }

    @Test
    public void testOutputFormats() {
        TestSource source = new TestSource();
        dsp.getInput().connect(source);
        
        Format[] fmts = dsp.getOutput().getFormats();
        
        Format[] expected = new Format[] {PCMA, PCMU, LINEAR_AUDIO, DTMF};
        assertEquals(true, Utils.checkFormats(fmts, expected));
        
    }
    
    @Test
    public void testTranscoding() throws Exception {
        TestSource source = new TestSource();
        TestSink sink = new TestSink();
        
        dsp.getInput().connect(source);
        dsp.getOutput().connect(sink);
        dsp.getOutput().start();
        
        source.start();
        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        
        boolean res = !list.isEmpty();
        for (Buffer buffer : list) {
            res &= buffer.getFormat().matches(PCMA);
        }
        
        assertEquals(true, res);
    }

    @Test
    public void testTransparentTransmission() throws Exception {
        TestSource source = new TestSource();
        TestSink sink = new TestSink();
        
        dsp.getInput().connect(source);
        dsp.getOutput().connect(sink);
        dsp.getOutput().start();
        
        source.setFormat(DTMF);
        source.start();
        semaphore.tryAcquire(10, TimeUnit.SECONDS);
        
        boolean res = !list.isEmpty();
        for (Buffer buffer : list) {
            res &= buffer.getFormat().matches(DTMF);
        }
        
        assertEquals(true, res);
    }
    
    private class TestSource extends AbstractSource implements Runnable {

        private Timer timer = new TimerImpl();
        private ScheduledFuture task;
        private Format f = LINEAR;
        
        private int count = 0;
        
    	public TestSource(){
            super("ProcessorTest.Source");
    	}
        public void setFormat(Format f) {
            this.f = f;
        }
        public void start() {
            task = timer.synchronize(this);
        }

        public void stop() {
            task.cancel(true);
        }

        public Format[] getFormats() {
            return new Format[] {LINEAR, DTMF};
        }

        public void run() {
            while (count < 10) {
                Buffer buffer = new Buffer();
                buffer.setData(new byte[320]);
                buffer.setLength(320);
                buffer.setFormat(f);
                this.otherParty.receive(buffer);
                count++;
            }
            semaphore.release();
        }
        
    }
        
    private class TestSink extends AbstractSink {
    	
    	public TestSink(){
    		super("ProcessorTest.Sink");
    	}

        public Format[] getFormats() {
            return new Format[]{PCMA, DTMF};
        }

        public boolean isAcceptable(Format format) {
            return format.matches(PCMA) || format.matches(DTMF);
        }

        public void receive(Buffer buffer) {
            list.add(buffer);
        }
    }

}

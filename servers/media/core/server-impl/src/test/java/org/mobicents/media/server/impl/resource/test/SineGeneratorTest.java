/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.resource.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Timer;

/**
 *
 * @author kulikov
 */
public class SineGeneratorTest {

    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    
    private final static short A = Short.MAX_VALUE;
    private final static int f = 50;
    
    private SineGenerator gen;
    private Sink det;
    
    private Timer timer;    //15 second audio buffer
    private short[] data = new short[8000 * 15];
    private int len;

    public SineGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        timer = new TimerImpl();
        gen = new SineGenerator("test.sine", timer);
        gen.setAmplitude(A);
        gen.setFrequency(f);
        
        det = new Sink();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setAmplitude method, of class SineGenerator.
     */
    @Test
    @SuppressWarnings("static-access")
    public void testGenerator() throws InterruptedException {
        det.connect(gen);
        det.start();
        gen.start();
        
        Thread.currentThread().sleep(10000);
        gen.stop();
        det.stop();
        
        assertTrue("Sine not recognized", check());
    }

    private boolean check() {
        boolean res = true;
        double dt = 1/LINEAR_AUDIO.getSampleRate();
        short E = (short)(Short.MAX_VALUE / 100);
        for (int i = 0; i < len; i++) {
            short s = (short) (A* Math.sin(2 * Math.PI * f * i * dt));
            if (Math.abs(s - data[i]) > E) {
                return false;
            }
        }
        return res;
    }
    
    private class Sink extends AbstractSink {

        public Sink() {
            super("test.sink");
        }

        @Override
        public void onMediaTransfer(Buffer buffer) {
            byte[] buff = (byte[]) buffer.getData();
            for (int i = 0; i < buffer.getLength() - 1; i += 2) {
                short s = (short) ((buff[i] & 0xff) | (buff[i + 1] << 8));
                data[len++] = s;
            }
        }

        public Format[] getFormats() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isAcceptable(Format format) {
            return true;
        }
    }
}
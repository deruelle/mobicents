/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.jmf.mixer.test;

import java.util.Timer;
import java.util.TimerTask;

import junit.framework.TestCase;

import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.jmf.mixer.MixerInputStream;
import org.mobicents.media.server.impl.test.audio.MeanderGenerator;

/**
 *
 * @author Oleg Kulikov
 */
public class MixerInputStreamTest extends TestCase {

    private static Timer timer = new Timer();
    private final static int JITTER = 60;
    private final static int PACKET_PERIOD = 20;
    private final static int MAX_TIME = 10000;
    private MeanderGenerator generator;
    private MixerInputStream inputStream;
    private boolean silence = false;
    private boolean initialSilence = true;
    private int time;
    private boolean res = false;

    public MixerInputStreamTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        generator = new MeanderGenerator(PACKET_PERIOD, timer);
        generator.start();
        PushBufferStream stream = generator.getStreams()[0];
        try {
            inputStream = new MixerInputStream(stream, JITTER);
        } catch (UnsupportedFormatException e) {
            fail(e.getMessage());
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        generator.stop();
    }

    /**
     * Test of transferData method, of class MixerInputStream.
     */
    public void testTransferData() {
        timer.scheduleAtFixedRate(new Reader(), PACKET_PERIOD, PACKET_PERIOD);
        synchronized (this) {
            try {
                wait();
                assertEquals(true, res);
            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
        }
    }

    private void print(byte[] data) {
        System.out.println("read: ");
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println();
        System.out.println("---------------------------------");
    }

    private boolean checkSilence(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkSignal(byte[] data) {
        int k = 0;
        for (int i = 0; i < data.length / 2; i++) {
            short s = (short) ((data[k++] << 8) | (data[k++] & 0xff));
            if (s != Short.MAX_VALUE) {
                return false;
            }
        }
        return true;
    }

    private void terminate(boolean res) {
        synchronized (this) {
            this.res = res;
            try {
                generator.stop();
            } catch (Exception e) {
            }
            notifyAll();
        }
    }

    private class Reader extends TimerTask {

        public void run() {
            time += PACKET_PERIOD;
            if (time < MAX_TIME) {
                if (!inputStream.isReady()) {
                    System.out.println("sleep factor");
                    return;
                }
                
                if (inputStream.getJitter() > JITTER) {
                    System.err.println("jitter overflow, time=" + time);
                    terminate(false);
                    cancel();
                    return;
                }
                
                byte[] data = inputStream.read(PACKET_PERIOD);
                                
                //ignore initial silence
                if (time < 2* PACKET_PERIOD && !silence && !checkSignal(data)) {
                    System.out.println("Initial silence, time=" + time);
                    return;
                } 

                if (silence && !checkSilence(data)) {
                    System.err.println("minimum expected, time=" + time);
                    terminate(false);
                    cancel();
                }

                if (!silence && !checkSignal(data)) {
                    System.err.println("maximum expected, time=" + time);
                    terminate(false);
                    cancel();
                }

                silence = !silence;
            } else {
                terminate(true);
                cancel();
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.dsp;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.impl.resource.Proxy;
import org.mobicents.media.server.impl.resource.test.TransmissionTester2;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.CodecFactory;

/**
 * Test for transcoding with different codecs.
 * Tester's generator is a sine wave signal, which is compressed and then 
 * decompressed back. Spectral analyzer checks the returned signal.
 * 
 * @author Oleg Kulikov
 */
public class ProcessorTest {

    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat G729 = new AudioFormat(AudioFormat.G729, 8000, 8, 1);
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);
    
    private Processor dsp1;
    private Processor dsp2;
    
    private CodecFactory pcmaEncoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.alaw.EncoderFactory();
    private CodecFactory pcmaDecoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.alaw.DecoderFactory();
    private CodecFactory pcmuEncoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.ulaw.EncoderFactory();
    private CodecFactory pcmuDecoderFactory = new org.mobicents.media.server.impl.dsp.audio.g711.ulaw.DecoderFactory();
    private CodecFactory gsmEncoderFactory = new org.mobicents.media.server.impl.dsp.audio.gsm.EncoderFactory();
    private CodecFactory gsmDecoderFactory = new org.mobicents.media.server.impl.dsp.audio.gsm.DecoderFactory();
    private CodecFactory speexEncoderFactory = new org.mobicents.media.server.impl.dsp.audio.speex.EncoderFactory();
    private CodecFactory speexDecoderFactory = new org.mobicents.media.server.impl.dsp.audio.speex.DecoderFactory();
    private CodecFactory g729EncoderFactory = new org.mobicents.media.server.impl.dsp.audio.g729.EncoderFactory();
    private CodecFactory g729DecoderFactory = new org.mobicents.media.server.impl.dsp.audio.g729.DecoderFactory();
    
    private DspFactory dspFactory = new DspFactory();
    private Timer timer;
    private TransmissionTester2 tester;
    private Proxy proxy;

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
        timer = new TimerImpl();
        timer.start();
        
        tester = new TransmissionTester2(timer);
        
        proxy = new Proxy("test");
        
        ArrayList<CodecFactory> codecFactories = new ArrayList();
        codecFactories.add(pcmaEncoderFactory);
        codecFactories.add(pcmaDecoderFactory);
        codecFactories.add(pcmuEncoderFactory);
        codecFactories.add(pcmuDecoderFactory);
        codecFactories.add(gsmEncoderFactory);
        codecFactories.add(gsmDecoderFactory);
        codecFactories.add(speexEncoderFactory);
        codecFactories.add(speexDecoderFactory);
        codecFactories.add(g729EncoderFactory);
        codecFactories.add(g729DecoderFactory);

        dspFactory.setName("test");
        dspFactory.setCodecFactories(codecFactories);
        
        dsp1 = (Processor) dspFactory.newInstance(null);
        dsp2 = (Processor) dspFactory.newInstance(null);
        
        tester.connect(dsp1.getInput());
        dsp1.getOutput().connect(proxy.getInput());
        dsp2.getInput().connect(proxy.getOutput());
        tester.connect(dsp2.getOutput());
        
        dsp1.getInput().addListener(tester);
        dsp1.getOutput().addListener(tester);
        
        dsp2.getInput().addListener(tester);
        dsp2.getOutput().addListener(tester);
        
        proxy.getInput().addListener(tester);
        proxy.getOutput().addListener(tester);
        
    }

    @After
    public void tearDown() {
        timer.stop();
    }

    @Test
    public void testInputFormats() {
    }

    @Test
    public void testOutputFormats() {
    }

    private void testTranscoding(Format fmt) {
        proxy.setFormat(new Format[] {fmt});
        proxy.start();        
        
        dsp1.start();
        dsp2.start();
        
        tester.start();
        
        proxy.stop();
        dsp1.stop();
        dsp2.stop();
        
//        printStat();
        assertTrue(tester.getMessage(), tester.isPassed());
    }
    @Test
    public void testLinear() throws Exception {
        testTranscoding(LINEAR_AUDIO);
    }

    @Test
    public void testPCMA() throws Exception {
        testTranscoding(PCMA);
    }
    
    @Test
    public void testPCMU() throws Exception {
        testTranscoding(PCMU);
    }

    @Test
    public void testGSM() throws Exception {
        testTranscoding(GSM);
    }

    @Test
    public void testSpeex() throws Exception {
        testTranscoding(AVProfile.SPEEX);
    }

    @Test
    public void testG729() throws Exception {
        testTranscoding(G729);
    }
    
    private void printStat() {
        System.out.println("Generator :" + tester.getGenerator().getPacketsTransmitted());
        System.out.println("Dsp1 input :" + dsp1.getInput().getPacketsReceived());
        System.out.println("Dsp1 output :" + dsp1.getOutput().getPacketsTransmitted());
        System.out.println("Proxy input :" + proxy.getInput().getPacketsReceived());
        System.out.println("Proxy output :" + proxy.getOutput().getPacketsTransmitted());
        System.out.println("Dsp2 input :" + dsp2.getInput().getPacketsReceived());
        System.out.println("Dsp2 output :" + dsp2.getOutput().getPacketsTransmitted());
        System.out.println("Detector :" + tester.getDetector().getPacketsReceived());
    }
}

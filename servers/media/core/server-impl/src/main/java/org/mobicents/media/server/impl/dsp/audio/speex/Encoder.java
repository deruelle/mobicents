package org.mobicents.media.server.impl.dsp.audio.speex;

import org.mobicents.media.Buffer;
/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.dsp.Codec;
import org.xiph.speex.SpeexEncoder;

/**
 * Implements Speex narrow band, 8kHz compressor.
 * 
 * @author Amit Bhayani
 * @author Oleg Kulikov
 */
public class Encoder implements Codec {

    private int MODE_NB = 0;
    private int mode = 0;
    private int QUALITY = 3;
    private int quality = 3;
    private final static int SAMPLE_RATE = 8000;
    private final static int CHANNELS = 1;
    private SpeexEncoder speexEncoder = new SpeexEncoder();

    public Encoder() {
        speexEncoder.init(MODE_NB, QUALITY, SAMPLE_RATE, CHANNELS);
    }

    /**
     * Gets the mode of the codec.
     * 
     * @return integer identifier of the mode.
     */
    public int getMode() {
        return mode;
    }
    
    /**
     * Sets the mode of codec.
     * 
     * @param mode the new mode value
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    /**
     * Gets the quality value.
     * 
     * @return integer value in range 0..10 which shows the quality.
     */
    public int getQuality(){
        return quality;
    }
    
    /**
     * Modify quality value.
     * 
     * @param quality integer value in range 0..10 which shows the quality.
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }
    
    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormats().
     */
    public Format[] getSupportedInputFormats() {
        Format[] formats = new Format[]{Codec.LINEAR_AUDIO};
        return formats;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormats(Format).
     */
    public Format[] getSupportedOutputFormats(Format fmt) {
        Format[] formats = new Format[]{Codec.SPEEX};
        return formats;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormats().
     */
    public Format[] getSupportedInputFormats(Format fmt) {
        Format[] formats = new Format[]{Codec.LINEAR_AUDIO};
        return formats;
    }

    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#getSupportedFormats(Format).
     */
    public Format[] getSupportedOutputFormats() {
        Format[] formats = new Format[]{Codec.SPEEX};
        return formats;
    }
    
    /**
     * (Non Java-doc)
     * 
     * @see org.mobicents.media.server.impl.jmf.dsp.Codec#process(Buffer).
     */
    public void process(Buffer buffer) {
        byte[] data = (byte[]) buffer.getData();
        
        int offset = buffer.getOffset();
        int length = buffer.getLength();
        
        byte[] media = new byte[length - offset];
        System.arraycopy(data, 0, media, 0, media.length);
        
        byte[] res = process(data);
        
        buffer.setData(res);
        buffer.setOffset(0);
        buffer.setFormat(Codec.SPEEX);
        buffer.setLength(res.length);
    }
    
    /**
     * Perform compression.
     * 
     * @param media input media
     * @return compressed media.
     */
    public byte[] process(byte[] media) {
        speexEncoder.processData(media, 0, media.length);
        int size = speexEncoder.getProcessedDataByteSize();
        byte[] buff = new byte[size];
        speexEncoder.getProcessedData(buff, 0);
        return buff;
    }
}

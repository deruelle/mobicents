package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import java.io.StreamCorruptedException;

import org.mobicents.media.Format;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.xiph.speex.SpeexDecoder;

public class Decoder implements Codec {

    private final static int MODE_NB = 0;
    private final static boolean ENHANCED = false;
    private final static int SAMPLE_RATE = 8000;
    private final static int CHANNELS = 1;
    private SpeexDecoder decoder = new SpeexDecoder();

    public Decoder() {
        decoder.init(MODE_NB, SAMPLE_RATE, CHANNELS, ENHANCED);
    }

    public Format[] getSupportedInputFormats() {
        Format[] formats = new Format[]{Codec.SPEEX};
        return formats;
    }

    public Format[] getSupportedOutputFormats(Format fmt) {
        Format[] formats = new Format[]{Codec.LINEAR_AUDIO};
        return formats;
    }

    public byte[] process(byte[] media) {
        byte[] output = null;
        
        try {
            decoder.processData(media, 0, media.length);
            output = new byte[decoder.getProcessedDataByteSize()];
            decoder.getProcessedData(output, 0);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        }

        return output;
    }
}

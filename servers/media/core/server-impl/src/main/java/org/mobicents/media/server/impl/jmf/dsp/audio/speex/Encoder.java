package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import org.mobicents.media.Format;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.xiph.speex.SpeexEncoder;

public class Encoder implements Codec {

    private final static int MODE_NB = 0;
    private final static int QUALITY = 3;
    private final static int SAMPLE_RATE = 8000;
    private final static int CHANNELS = 1;
    private SpeexEncoder speexEncoder = new SpeexEncoder();

    public Encoder() {
        speexEncoder.init(MODE_NB, QUALITY, SAMPLE_RATE, CHANNELS);
    }

    public Format[] getSupportedInputFormats() {
        Format[] formats = new Format[]{Codec.LINEAR_AUDIO};
        return formats;
    }

    public Format[] getSupportedOutputFormats(Format fmt) {
        Format[] formats = new Format[]{Codec.SPEEX};
        return formats;
    }

    public byte[] process(byte[] media) {
        speexEncoder.processData(media, 0, media.length);
        int size = speexEncoder.getProcessedDataByteSize();
        byte[] buff = new byte[size];
        speexEncoder.getProcessedData(buff, 0);
        return buff;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.dsp.audio.gsm;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.dsp.BaseCodec;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author kulikov
 */
public class Encoder extends BaseCodec {

    private final static AudioFormat LINEAR = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN,
            AudioFormat.SIGNED);
    
    private final static AudioFormat GSM = new AudioFormat(
            AudioFormat.LINEAR, 8000, 
            AudioFormat.NOT_SPECIFIED, 1);

    private final static AudioFormat[] supportedInputFormats = new AudioFormat[] {LINEAR};
    private final static AudioFormat[] supportedOutputFormats = new AudioFormat[] {GSM};
    
    private org.tritonus.lowlevel.gsm.Encoder encoder = new org.tritonus.lowlevel.gsm.Encoder();
    
    public Format[] getSupportedInputFormats() {
        return supportedInputFormats;
    }

    public Format[] getSupportedOutputFormats(Format fmt) {
        if (fmt.matches(LINEAR)) {
            return supportedOutputFormats;
        } else return null;
    }

    public Format[] getSupportedOutputFormats() {
        return supportedOutputFormats;
    }

    public Format[] getSupportedInputFormats(Format fmt) {
        if (fmt.matches(GSM)) {
            return supportedInputFormats;
        } else return null;
    }

    public void process(Buffer buffer) {
        if (buffer.getLength() != 320) {
            buffer.setFlags(Buffer.FLAG_BUF_UNDERFLOWN);
            return;
        }
        
        //encode into short values
        byte[] data = (byte[]) buffer.getData();
        short[] signal = new short[160];
        
        int k = 0;
        for (int i = 0; i < 160; i++) {
            signal[i] = (short)((data[k++] << 8) & (data[k++]));
        }
        
        byte[] res = new byte[33];
        encoder.encode(signal, res);
        
        buffer.setData(res);
    }
}

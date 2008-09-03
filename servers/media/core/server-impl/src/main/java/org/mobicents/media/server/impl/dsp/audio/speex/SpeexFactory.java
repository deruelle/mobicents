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
package org.mobicents.media.server.impl.dsp.audio.speex;

import org.mobicents.media.server.impl.dsp.audio.g711.*;
import java.util.ArrayList;
import java.util.List;
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.dsp.CodecFactory;

/**
 *
 * @author Oleg Kulikov
 */
public class SpeexFactory implements CodecFactory {

    private static List<Codec> codecs = new ArrayList();

    static {
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.speex.Encoder());
        codecs.add(new org.mobicents.media.server.impl.dsp.audio.speex.Decoder());
    }

    private static boolean matches(Format[] supported, Format input) {
        for (int i = 0; i < supported.length; i++) {
            if (supported[i].matches(input)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Codec getCodec(Format inputFmt, Format outputFmt) {
        for (Codec codec : codecs) {
            if ((matches(codec.getSupportedInputFormats(), inputFmt)) && (matches(codec.getSupportedOutputFormats(inputFmt), outputFmt))) {
                Class cls = codec.getClass();
                try {
                    return (Codec) cls.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

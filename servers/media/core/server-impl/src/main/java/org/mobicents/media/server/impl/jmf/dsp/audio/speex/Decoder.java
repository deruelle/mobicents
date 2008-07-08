package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import java.io.StreamCorruptedException;

import org.mobicents.media.Format;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.xiph.speex.SpeexDecoder;


public class Decoder implements Codec {

	private SpeexDecoder decoder = null;

	public Format[] getSupportedInputFormats() {
		Format[] formats = new Format[] { Codec.SPEEX };
		return formats;
	}

	public Format[] getSupportedOutputFormats(Format fmt) {
		Format[] formats = new Format[] { Codec.LINEAR_AUDIO };
		return formats;
	}

	public byte[] process(byte[] media) {
		byte[] output = null;
		decoder = new SpeexDecoder();

		decoder.init(0, 8000, 1, false);
		
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

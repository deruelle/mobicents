package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import org.mobicents.media.Format;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.xiph.speex.SpeexEncoder;

public class Encoder implements Codec {

	private SpeexEncoder speexEncoder = null;

	public Format[] getSupportedInputFormats() {
		Format[] formats = new Format[] { Codec.LINEAR_AUDIO };
		return formats;
	}

	public Format[] getSupportedOutputFormats(Format fmt) {
		Format[] formats = new Format[] { Codec.SPEEX };
		return formats;
	}

	public byte[] process(byte[] media) {
		speexEncoder = new SpeexEncoder();
		speexEncoder.init(0, 3, 8000, 1);
		
//		Buffer input = new Buffer();		
//		input.setFormat(getSupportedInputFormats()[0]);
//		input.setData(media);
//		input.setLength(media.length);
//		
//		
//		Buffer output = new Buffer();
//		
//		 byte[] compressed = new byte[media.length / 2];
		
		int channels = 1;
		
		int pcmPacketSize = 2 * channels * speexEncoder.getFrameSize();
		
		speexEncoder.processData(media, 0, pcmPacketSize);
		
		byte[] buff = new byte[speexEncoder.getProcessedDataByteSize()];
		
		speexEncoder.getProcessedData(buff, 0);
		 
		return buff;
	}

}

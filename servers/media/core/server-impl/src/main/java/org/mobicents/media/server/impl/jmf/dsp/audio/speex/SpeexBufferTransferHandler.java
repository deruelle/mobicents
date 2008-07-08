package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import java.io.IOException;

import org.mobicents.media.Buffer;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;

public class SpeexBufferTransferHandler implements BufferTransferHandler {

	public void transferData(PushBufferStream stream) {

		Buffer inputBuffer = new Buffer();

		try {
			stream.read(inputBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Encoder speexEncoder = new Encoder();
		byte[] input = (byte[]) inputBuffer.getData();

		System.out.println("Input length is = " + inputBuffer.getLength());
		System.out.println("Input is = " + input);

		byte[] output = speexEncoder.process(input);

		System.out.println("Output is = " + output);

		System.out.println("Lets decode this now");
	}

}

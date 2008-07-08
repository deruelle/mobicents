package org.mobicents.media.server.impl.jmf.dsp.audio.speex;

import java.io.IOException;

import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.test.audio.SineGenerator;
import org.mobicents.media.server.impl.test.audio.SineStream;

public class TestSpeex {
	public static void main(String args[]) throws IOException {

		final int[] sineFrequencies = new int[] { 100 };

		// packetization period
		int sineDuruation = 1000;

		SineGenerator sineGenerator = new SineGenerator(sineDuruation, sineFrequencies, true);

		PushBufferStream[] streams = sineGenerator.getStreams();

		PushBufferStream stream = streams[0];

		SpeexBufferTransferHandler speexBufferTransferHandler = new SpeexBufferTransferHandler();

		stream.setTransferHandler(speexBufferTransferHandler);
		
		sineGenerator.start();

//		Decoder speexDecoder = new Decoder();
//		byte[] processedInput = speexDecoder.process(output);
//
//		System.out.println("processedInput = " + processedInput);

	}

}

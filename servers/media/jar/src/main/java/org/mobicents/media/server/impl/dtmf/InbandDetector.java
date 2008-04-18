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
package org.mobicents.media.server.impl.dtmf;

import java.io.IOException;
import java.util.Properties;
import org.mobicents.media.Buffer;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;
import org.apache.log4j.Logger;
import org.mobicents.media.goertzel.Filter;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.impl.common.MediaResourceState;

/**
 * 
 * @author Oleg Kulikov
 */
public class InbandDetector extends BaseDtmfDetector implements
		BufferTransferHandler {

	public final static String[][] events = new String[][] {
			{ "1", "2", "3", "A" }, { "4", "5", "6", "B" },
			{ "7", "8", "9", "C" }, { "*", "0", "#", "D" } };

	private int[] lowFreq = new int[] { 697, 770, 852, 941 };
	private int[] highFreq = new int[] { 1209, 1336, 1477, 1633 };
	private Codec codec;
	private byte[] localBuffer = new byte[16000];
	private int offset = 0;
	private Filter filter = new Filter(10);
	private Logger logger = Logger.getLogger(InbandDetector.class);

	public InbandDetector() {
		super();
		Properties props = new Properties();
		try {
			props.load(getClass().getResourceAsStream("dtmf.properties"));
			int t = Integer.parseInt(props.getProperty("dtmf.threshold"));
			filter = new Filter(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void configure(Properties config) {
		setState(MediaResourceState.CONFIGURED);
	}

	public void prepare(PushBufferStream stream)
			throws UnsupportedFormatException {
		stream.setTransferHandler(this);
		if (!stream.getFormat().matches(Codec.LINEAR_AUDIO)) {
			codec = CodecLocator.getCodec(stream.getFormat(),
					Codec.LINEAR_AUDIO);
			setState(MediaResourceState.PREPARED);
			if (codec == null) {
				throw new UnsupportedFormatException(stream.getFormat());
			}
		}
	}

	public void start() {
		setState(MediaResourceState.STARTED);
	}

	public void stop() {
		setState(MediaResourceState.PREPARED);
	}

	public void transferData(PushBufferStream stream) {
		if (getState() != MediaResourceState.STARTED) {
			return;
		}

		Buffer buffer = new Buffer();
		try {
			stream.read(buffer);
		} catch (IOException e) {
		}

		byte[] data = (byte[]) buffer.getData();
		// System.out.println("receive " + data.length + " bytes");
		// InbandGenerator.print(data);

		// byte[] data = null;
		if (codec != null) {
			data = codec.process(data);
			// System.out.println("Decompressed to " + data.length + " bytes");
			// InbandGenerator.print(data);
		}

		int len = Math.min(16000 - offset, data.length);
		System.arraycopy(data, 0, localBuffer, offset, len);
		offset += len;

		// System.out.println("append " + len + " bytes to the local buffer,
		// buff size=" + offset);
		if (logger.isDebugEnabled()) {
			logger.debug("append " + len
					+ " bytes to the local buffer, buff size=" + offset);
		}

		// buffer full?
		if (offset == 16000) {
			// System.out.println("SIGNAL");
			// InbandGenerator.print(localBuffer);
			double[] signal = new double[8000];
			int k = 0;
			for (int i = 0; i < 8000; i++) {
				signal[i] = ((localBuffer[k++] & 0xff) | (localBuffer[k++] << 8));
				// signal[i] = signal[i] / Short.MAX_VALUE;
				// System.out.println("s[" + i + "]=" +signal[i]);
			}

			localBuffer = new byte[16000];
			offset = 0;

			logger.debug("Checking low frequencies");
			int f1 = checkFreq(lowFreq, signal);
			// System.out.println("Low group: " + f1);
			if (f1 == -1) {
				logger.debug("No low frequencies were found, break");
				return;
			}

			logger.debug("Found frequency " + lowFreq[f1]
					+ " Checking hight frequencies");
			int f2 = checkFreq(highFreq, signal);
			if (f2 == -1) {
				logger.debug("No high frequencies were found, break");
				return;
			}

			logger.debug("Found frequency " + highFreq[f1] + ", evt="
					+ events[f1][f2]);
			digitBuffer.push(events[f1][f2]);
		}
	}

	private int checkFreq(int[] freq, double[] data) {
		for (int i = 0; i < freq.length; i++) {
			if (logger.isDebugEnabled()) {
				logger.debug("Checking freq: " + freq[i]);
			}
			if (filter.detect(freq[i], data)) {
				return i;
			}
		}
		return -1;
	}

	public void release() {
		localBuffer = null;
		setState(MediaResourceState.NULL);
	}
}

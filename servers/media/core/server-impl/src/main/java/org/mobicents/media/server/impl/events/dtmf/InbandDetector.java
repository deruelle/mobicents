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
package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;

/**
 * Implements inband DTMF detector.
 * 
 * Inband means that DTMF is transmitted within the audio of the phone
 * conversation, i.e. it is audible to the conversation partners. Therefore only
 * uncompressed codecs like g711 alaw or ulaw can carry inband DTMF reliably.
 * Female voice are known to once in a while trigger the recognition of a DTMF
 * tone. For analog lines inband is the only possible means to transmit DTMF.
 * 
 * Though Inband DTMF detection may work for other codecs like SPEEX, GSM, G729
 * as DtmfDetector is using DSP in front of InbandDetector there is no guarantee
 * that it will always work. In future MMS may not have DSP in front of
 * InbandDetector and hence Inband detection for codecs like SPEEX, GSM, G729
 * may completely stop
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class InbandDetector extends DtmfBuffer {

	private final static AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

	private final static Format[] FORMATS = new Format[] { LINEAR };
	public final static String[][] events = new String[][] { { "1", "2", "3", "A" }, { "4", "5", "6", "B" },
			{ "7", "8", "9", "C" }, { "*", "0", "#", "D" } };
	/** DTMF tone duration in milliseconds */
	private int TONE_DURATION = 50;
	private double THRESHOLD = 30;
	private int[] lowFreq = new int[] { 697, 770, 852, 941 };
	private int[] highFreq = new int[] { 1209, 1336, 1477, 1633 };
	private byte[] localBuffer;
	private int offset = 0;

	private boolean started = false;

	private int N = 16 * TONE_DURATION / 2;
	private double scale = (double) TONE_DURATION / (double) 1000;
	private double[] ham = new double[N];
	private double[] realWLowFreq = new double[lowFreq.length];
	private double[] imagWLowFreq = new double[lowFreq.length];

	private double[] realWHighFreq = new double[highFreq.length];
	private double[] imagWHighFreq = new double[highFreq.length];

	/**
	 * Creates new instance of Detector.
	 */
	public InbandDetector(String name) {
		super(name);
		localBuffer = new byte[16 * TONE_DURATION];

		// hamming window
		for (int i = 0; i < N; i++) {
			ham[i] = (0.54 - 0.46 * Math.cos(2 * Math.PI * i / N));
		}

		for (int i = 0; i < lowFreq.length; i++) {
			realWLowFreq[i] = 2.0 * Math.cos(2.0 * scale * Math.PI * lowFreq[i] / N);
			imagWLowFreq[i] = Math.sin(2.0 * scale * Math.PI * lowFreq[i] / N);
		}

		for (int i = 0; i < highFreq.length; i++) {
			realWHighFreq[i] = 2.0 * Math.cos(2.0 * scale * Math.PI * highFreq[i] / N);
			imagWHighFreq[i] = Math.sin(2.0 * scale * Math.PI * highFreq[i] / N);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.MediaSink#start().
	 */
	public void start() {
		started = true;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.server.spi.MediaSink#stop().
	 */
	public void stop() {
		started = false;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.media.protocol.BufferTransferHandler.transferData().
	 */
	public void receive(Buffer buffer) {

		try {
			byte[] data = (byte[]) buffer.getData();
			int len = Math.min(localBuffer.length - offset, data.length);
			System.arraycopy(data, 0, localBuffer, offset, len);
			offset += len;

			// buffer full?
			if (offset == localBuffer.length) {
				double[] signal = new double[localBuffer.length / 2];
				int k = 0;
				for (int i = 0; i < signal.length; i++) {
					signal[i] = ((localBuffer[k++] & 0xff) | (localBuffer[k++] << 8));
				}

				localBuffer = new byte[localBuffer.length];
				offset = 0;

				double p[] = getPower(lowFreq, realWLowFreq, imagWLowFreq, signal);
				double P[] = getPower(highFreq, realWHighFreq, imagWHighFreq, signal);

				String tone = getTone(p, P);
				if (tone != null) {
					super.push(tone);
				}
			}
		} finally {
			buffer.dispose();
		}

	}

	/**
	 * Calculate power of the specified frequencies.
	 * 
	 * @param freq
	 *            the array of frequencies
	 * @param data
	 *            signal
	 * @return the power for the respective frequency
	 */
	private double[] getPower(int[] freq, double[] realW, double[] imagW, double[] data) {
		double[] power = new double[freq.length];

		double d1 = 0.0;
		double d2 = 0.0;
		double y = 0;

		for (int j = 0; j < freq.length; j++) {

			double f = freq[j];

			// hamming window
			for (int i = 0; i < N; i++) {
				data[i] *= ham[i];
			}

			d1 = 0.0;
			d2 = 0.0;
			y = 0;

			for (int n = 0; n < N; ++n) {
				y = data[n] + realW[j] * d1 - d2;
				d2 = d1;
				d1 = y;
			}

			double resultr = 0.5 * realW[j] * d1 - d2;
			double resulti = imagW[j] * d1;

			// The profiling shows that Math.sqrt() is really not taking much
			// CPU. If you think otherwise try using the calculateSqRt() method
			// using Babylonian algo and profile.
			power[j] = Math.sqrt((resultr * resultr) + (resulti * resulti));
		}
		return power;
	}

	// This is Babylonian method of calculating Sq Root
	// http://en.wikipedia.org/wiki/Babylonian_method#Babylonian_method
	private double calculateSqRt(double n) {

		double x = n * 0.25;
		double a = 0.0;

		// The loop shows how many iteration it took to get the Sq Root. The
		// lesser the iteration the fast is calculation and less is CPU. This
		// depends on approximation 'x' above. If this can some how be close to
		// Sq Root value the iteration can be reduced greatly

		// int loop = 0;

		do {
			// loop++;
			x = 0.5 * (x + n / x);
			a = x * x - n;
			if (a < 0)
				a = -a;

		} while (a > 0.00001);

		return x;
	}

	/**
	 * Searches maximum value in the specified array.
	 * 
	 * @param data[]
	 *            input data.
	 * @return the index of the maximum value in the data array.
	 */
	private int getMax(double data[]) {
		int idx = 0;
		double max = data[0];
		for (int i = 1; i < data.length; i++) {
			if (max < data[i]) {
				max = data[i];
				idx = i;
			}
		}
		return idx;
	}

	/**
	 * Searches DTMF tone.
	 * 
	 * @param f
	 *            the low frequency array
	 * @param F
	 *            the high frequency array.
	 * @return DTMF tone.
	 */
	private String getTone(double f[], double F[]) {
		int fm = getMax(f);
		boolean fd = true;

		for (int i = 0; i < f.length; i++) {
			if (fm == i) {
				continue;
			}
			double r = f[fm] / (f[i] + 1E-15);
			if (r < THRESHOLD) {
				fd = false;
				break;
			}
		}

		if (!fd) {
			return null;
		}

		int Fm = getMax(F);
		boolean Fd = true;

		for (int i = 0; i < F.length; i++) {
			if (Fm == i) {
				continue;
			}
			double r = F[Fm] / (F[i] + 1E-15);
			if (r < THRESHOLD) {
				Fd = false;
				break;
			}
		}

		if (!Fd) {
			return null;
		}

		return events[fm][Fm];
	}

	public Format[] getFormats() {
		return FORMATS;
	}

	public boolean isAcceptable(Format format) {
		return format.matches(LINEAR);
	}
}

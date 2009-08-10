package org.mobicents.media.server.impl.resource.tone;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.resource.GoertzelFilter;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.resource.FrequencyBean;
import org.mobicents.media.server.spi.resource.MultiFreqToneDetector;

/**
 * 
 * @author amit.bhayani
 * 
 */
public class MultiFreqToneDetectorImpl extends AbstractSink implements MultiFreqToneDetector {

	private static transient Logger logger = Logger.getLogger(MultiFreqToneDetectorImpl.class);

	private final static int STATE_IDLE = 0;
	private final static int STATE_SIGNAL = 1;
	private final static int STATE_SILENCE = 2;

	private int POWER = 10000;

	private int state = STATE_IDLE;

	private final static double E = 100;
	private final static int PACKET_DURATION = 50;

	private final static Format[] FORMATS = new Format[] { Codec.LINEAR_AUDIO };

	private int offset;

	private int toneDuration = PACKET_DURATION;
	private int N = 8 * toneDuration;
	private double scale = (double) toneDuration / (double) 1000;

	private GoertzelFilter[] freqFilters;
	private double[] signal;
	private double maxAmpl;

	private int level = -30;
	private double threshold = Math.pow(Math.pow(10, level), 0.1) * Short.MAX_VALUE;
	private double p[];

	private FrequencyBean freqBean;

	private MultiFreqToneEvent event;

	public MultiFreqToneDetectorImpl(String name) {
		super(name);
		event = new MultiFreqToneEvent(this);
	}

	public void onMediaTransfer(Buffer buffer) throws IOException {
		byte[] data = (byte[]) buffer.getData();

		int M = buffer.getOffset() + buffer.getLength();
		int k = buffer.getOffset();
		while (k < M) {
			while (offset < N && k < M - 1) {
				double s = ((data[k++] & 0xff) | (data[k++] << 8));
				double sa = Math.abs(s);
				if (sa > maxAmpl) {
					maxAmpl = sa;
				}
				signal[offset++] = s;
			}

			// if dtmf buffer full check signal
			if (offset == N) {
				System.out.println("buffer full maxAmpl = " + maxAmpl);
				offset = 0;
				// and if max amplitude of signal is greater theshold
				// try to detect tone.
				if (maxAmpl >= threshold) {
					maxAmpl = 0;
					getPower(freqFilters, signal, 0, p);
					if (isDetected()) {
						notifySignal();
					}
				}
			}
		}

	}

	private void notifySignal() {
		sendEvent(event);
		this.stop();
	}

	private boolean isDetected() {
		for (double P : p) {
			System.out.println("POWER =" + P);
			if (P <= POWER) {
				return false;
			}
		}
		return true;
	}

	private void getPower(GoertzelFilter[] filters, double[] data, int offset, double[] power) {
		for (int i = 0; i < filters.length; i++) {
			power[i] = filters[i].getPower(data, offset);
		}
	}

	/**
	 * (Non Java-doc.)
	 * 
	 * @see org.mobicents.media.MediaSink.isAcceptable(Format).
	 */
	public boolean isAcceptable(Format fmt) {
		return fmt.matches(Codec.LINEAR_AUDIO);
	}

	public Format[] getFormats() {
		return FORMATS;
	}

	public FrequencyBean getFreqBean() {
		return freqBean;
	}

	public void setFreqBean(FrequencyBean freqBean) {
		this.freqBean = freqBean;

		this.signal = new double[N];

		freqFilters = new GoertzelFilter[2];
		p = new double[2];

		freqFilters[0] = new GoertzelFilter(freqBean.getLowFreq(), N, scale);
		freqFilters[1] = new GoertzelFilter(freqBean.getHighFreq(), N, scale);

	}

	public int getVolume() {
		return level;
	}

	public void setVolume(int level) {
		this.level = level;
		threshold = Math.pow(Math.pow(10, level), 0.1) * Short.MAX_VALUE;
	}

}

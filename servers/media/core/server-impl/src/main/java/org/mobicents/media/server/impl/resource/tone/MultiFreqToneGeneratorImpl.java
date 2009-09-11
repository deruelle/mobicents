package org.mobicents.media.server.impl.resource.tone;

import java.util.List;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.spi.SyncSource;
import org.mobicents.media.server.spi.resource.FrequencyBean;
import org.mobicents.media.server.spi.resource.MultiFreqToneGenerator;

/**
 * 
 * @author amit bhayani
 * 
 */
public class MultiFreqToneGeneratorImpl extends AbstractSource implements MultiFreqToneGenerator {

	private final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);

	private double dt;
	private int pSize;

	private short A = (short)(Short.MAX_VALUE /2);

	private int volume = 0;

	private double time = 0;
	private int count = 0;

	private double totalDuration = 0;

	private volatile List<FrequencyBean> frequencies;

	public MultiFreqToneGeneratorImpl(String name, SyncSource syncSource) {
		super(name);
		setSyncSource(syncSource);

		dt = 1 / LINEAR_AUDIO.getSampleRate();
	}

	@Override
	public void beforeStart() throws Exception {
		for (FrequencyBean freqBean : this.frequencies) {
			this.totalDuration = this.totalDuration + freqBean.getDuration();
		}
		this.totalDuration = this.totalDuration / 1000.0;
		this.time = 0;
		this.count = 0;
	}

	public int getVolume() {
		return this.volume;
	}

	public void setVolume(int volume) {
		// if (volume > 0) {
		// throw new IllegalArgumentException("Volume has to be negative value expressed in dBm0");
		// }
		this.volume = volume;
		A = (short) (Math.pow(Math.pow(10, volume), 0.1) * (Short.MAX_VALUE / 2));
	}

	@Override
	public void evolve(Buffer buffer, long timestamp, long seq) {		

		try {
			byte[] data = (byte[]) buffer.getData();

			int k = 0;
			int tempDuration = 0;

			for (FrequencyBean freqBean : frequencies) {
				int lowFreq = freqBean.getLowFreq();
				int highFreq = freqBean.getHighFreq();
				int duration = freqBean.getDuration();

				tempDuration = tempDuration + duration;

				if (time < (tempDuration / 1000.0)) {
                        		pSize = (int) ((double) getDuration() / 1000.0 / dt);
					for (int i = 0; i < pSize; i++) {
						short v;
						if (lowFreq == 0 || highFreq == 0) {
							v = 0;
						} else {
							v = getValue(time + dt * i, lowFreq, highFreq);
						}
						data[k++] = (byte) v;
						data[k++] = (byte) (v >> 8);
					}

					buffer.setFormat(LINEAR_AUDIO);
					buffer.setSequenceNumber(seq);
					buffer.setTimeStamp(getSyncSource().getTimestamp());
					buffer.setDuration(getDuration());
					buffer.setOffset(0);
					buffer.setLength(2 * pSize);

					time += ((double) getDuration()) / 1000.0;
					buffer.setEOM(false);
					if (time >= totalDuration) {
						time = 0.0;
					}

					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private short getValue(double t, int lowFreq, int highFreq) {
		return (short) (A * (Math.sin(2 * Math.PI * lowFreq * t) + Math.sin(2 * Math.PI * highFreq * t)));
	}

	public Format[] getFormats() {
		return new Format[] { LINEAR_AUDIO };
	}

	public List<FrequencyBean> getFreqBeanList() {
		return this.frequencies;
	}

	public void setFreqBeanList(List<FrequencyBean> freqBeans) {
		this.frequencies = freqBeans;
	}

}

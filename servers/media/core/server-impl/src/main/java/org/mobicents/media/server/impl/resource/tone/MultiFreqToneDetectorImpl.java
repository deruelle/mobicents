package org.mobicents.media.server.impl.resource.tone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.resource.fft.Complex;
import org.mobicents.media.server.impl.resource.fft.FFT;
import org.mobicents.media.server.spi.resource.FrequencyBean;
import org.mobicents.media.server.spi.resource.MultiFreqToneDetector;

/**
 * 
 * @author amit.bhayani
 *
 */
public class MultiFreqToneDetectorImpl extends AbstractSink implements MultiFreqToneDetector{

	private String reason;

	public MultiFreqToneDetectorImpl(String name) {
		super(name);
	}

	private final static AudioFormat LINEAR_AUDIO = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
			AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
	private int offset = 0;
	private byte[] localBuffer = new byte[16000];
	private FFT fft = new FFT();
	private static transient Logger logger = Logger.getLogger(MultiFreqToneDetectorImpl.class);

	private int samplingRate = 8000;
	private int toneDuartion = 50;
	private int N = (samplingRate / 1000) * 50;
	private int powerOf2 = 1;

	private FrequencyBean freqBean;

	private double[] mod(Complex[] x) {
		double[] res = new double[x.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = Math.sqrt(x[i].re() * x[i].re() + x[i].im() * x[i].im());
		}
		return res;
	}

	protected void sendEvent(int[] spectra) {
		MultiFreqToneEvent evt = new MultiFreqToneEvent(this, spectra);
		sendEvent(evt);
	}

	public void onMediaTransfer(Buffer buffer) throws IOException {	
		
		byte[] data = (byte[]) buffer.getData();
		int len = Math.min((N * 2) - offset, buffer.getLength());
		System.arraycopy(data, buffer.getOffset(), localBuffer, offset, len);
		offset += len;

		if (logger.isDebugEnabled()) {
			logger.debug("append " + len + " bytes to the local buffer, buff size=" + offset);
		}

		// buffer full?
		if (offset == (N * 2)) {
			System.out.println("Buffer Fool");
			double[] media = new double[N];
			int j = 0;
			for (int i = 0; i < media.length; i++) {
				media[i] = (localBuffer[j++] & 0xff) | (localBuffer[j++] << 8);
			}

			// resampling
			Complex[] signal = new Complex[this.powerOf2];
			double k = (double) (media.length - 1) / (double) (signal.length);

			for (int i = 0; i < signal.length; i++) {
				int p = (int) (k * i);
				int q = (int) (k * i) + 1;

				double K = (media[q] - media[p]) * media.length;
				double dx = (double) i / (double) signal.length - (double) p / (double) media.length;
				signal[i] = new Complex(media[p] + K * dx, 0);
			}
			
			localBuffer = new byte[N * 2];
			offset = 0;

			Complex[] sp = fft.fft(signal);
			double[] res = mod(sp);

			int[] ext = getFreq(res);
			
			System.out.println("ext.length = "+ ext.length);
			
			if (ext.length == 2) {

				for (int count = 0; count < ext.length; count++) {
					ext[count] = ext[count] * 1000 / this.toneDuartion;
				}

				boolean r = checkFreq(ext, new int[] { this.freqBean.getLowFreq(), this.freqBean.getHighFreq() },
						10);

				if (r) {
					System.out.println("Detected Frequency " + ext[0] + " " + ext[1]);
					sendEvent(ext);
					this.stop();
				} else{
					System.out.println("Failed to detect");
				}
			}

		}
	}
	
    public synchronized boolean checkFreq(int[] ext, int[] F, int error) {
        if (ext.length < F.length) {
            reason = "Expected " + F.length + " peaks but found " + ext.length;
            return false;
        }
        for (int i = 0; i < F.length; i++) {
            if (Math.abs(ext[i] - F[i]) > error) {
                reason = "Expected " + F[i] + " but found " + ext[i];
                return false;
            }
        }

        return true;
    }
	
    private int[] getFreq(double[] s) {
        double max = findMax(s);

        int len = s.length / 2;
        double ss[] = new double[len];

        for (int i = 0; i < len; i++) {
            ss[i] = s[i] / max;
            ss[i] = ss[i] < 0.7 ? 0 : ss[i];
        }

        double[] diff = diff(ss);
        int[] ext = findExtremums(diff);
        
        return ext;
    }
    
    private double findMax(double[] f) {
        double max = f[0];
        for (int i = 1; i < f.length; i++) {
            max = Math.max(max, f[i]);
        }
        return max;
    }
    
    private double[] diff(double[] f) {
        double[] diff = new double[f.length];
        for (int i = 0; i < f.length - 1; i++) {
            diff[i] = f[i + 1] - f[i];
        }
        return diff;
    }
    
    private int[] findExtremums(double[] f) {
        List<Integer> ext = new ArrayList();
        for (int i = 0; i < f.length - 1; i++) {
            if (f[i] > 0 && f[i + 1] < 0) {
                ext.add(i);
            }
        }

        int[] res = new int[ext.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = ext.get(i);
        }
        return res;
    }
    

	/**
	 * (Non Java-doc.)
	 * 
	 * @see org.mobicents.media.MediaSink.isAcceptable(Format).
	 */
	public boolean isAcceptable(Format fmt) {
		return fmt.matches(LINEAR_AUDIO);
	}

	public Format[] getFormats() {
		return new Format[] { LINEAR_AUDIO };
	}

	public FrequencyBean getFreqBean() {
		return freqBean;
	}

	public void setFreqBean(FrequencyBean freqBean) {
		this.freqBean = freqBean;

		this.toneDuartion = freqBean.getDuration();
		this.N = 8 * toneDuartion;

		while (powerOf2 < this.N) {
			this.powerOf2 = this.powerOf2 * 2;
		}

		System.out.println("this.N = " + this.N);
		System.out.println("this.powerOf2 = " + this.powerOf2);

		localBuffer = new byte[N * 2];
	}

	public int getVolume() {
		return 0;
	}

	public void setVolume(int level) {
		
	}

}

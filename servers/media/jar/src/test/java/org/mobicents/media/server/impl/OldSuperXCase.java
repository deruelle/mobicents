package org.mobicents.media.server.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.fft.Complex;
import org.mobicents.media.server.impl.fft.FFT;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class OldSuperXCase extends TestCase implements BufferTransferHandler {
	/**
	 * Test signal duration in mS
	 */
	protected int testSignalDuration = 20;
	/**
	 * Size of buffer taken into dft - this is milisecond buffer size
	 */

	protected int dftWindowSize = 1000;
	protected int[] sineFrequencies = new int[] { 50, 80 };
	/**
	 * Tells us how many F our peaks can be found.
	 */
	protected int strayNumber = 5;
	/**
	 * We scale results according to max value. However with multiple peaks we
	 * can get differetn values, this it tolerance in comparison to max value.
	 */
	protected double peakPercentage = 0.95d;

	private String mcHOME = System.getenv("MOBICENTS_HOME");
	protected String dumpDir = (mcHOME + File.separator + "servers"
			+ File.separator + "media" + File.separator + "jar"
			+ File.separator + "target" + File.separator + "surefire-reports")
			.replace("core", "");

	private BitSize signalBufferBitFormat = BitSize.BIT_8;
	/**
	 * This shows how many bytes we have per sample - 1==8,2==16, etc
	 */
	private int bitSizeMultipllier = 1;

	/**
	 * One time result for test.
	 */
	private ArrayList<double[]> results = null;

	/**
	 * Not very efficient way to record signal, but we really don't need to be
	 * super efficient here, we just have to be sure we get proper results
	 */
	private ArrayList<Complex> signalValues = new ArrayList<Complex>(10000);
	protected PushBufferStream receiveStream = null;
	private Codec codec = null;

	protected Logger logger = Logger.getLogger(this.getClass()
			.getCanonicalName());

	
	
	
	
	
	
	

	
	
	
	
	
	
	
	protected int plotUnitWidth = 1;
	protected int plotUnitHeight = 1;
	protected File plotFile = null;
	protected boolean firstPlotRun = true;

	
	
	
	@Override
	protected void setUp() throws Exception {

		results = new ArrayList<double[]>();

		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		results.clear();
		results = null;
		super.tearDown();
	}

	/**
	 * Obligatory method to call
	 * 
	 * @param receiverStream
	 */
	protected void setReceiveStream(PushBufferStream receiverStream) {
		this.receiveStream = receiverStream;
		this.determineBitSize();
		this.determineCodec();
		this.receiveStream.setTransferHandler(this);
	}

	// ---------- METHODS TO BE CALLED IN PREAPRE method of MediaSink

	protected void determineBitSize() {
		if (this.receiveStream == null)
			throw new IllegalStateException("resource not configured!!!");
		Format fmt = this.receiveStream.getFormat();

		if (!(fmt instanceof AudioFormat)) {
			throw new IllegalArgumentException(
					"Source stream is not an audio stream!!!!");
		}

		AudioFormat af = (AudioFormat) fmt;
		this.bitSizeMultipllier = af.getSampleSizeInBits() / 8;

		switch (this.bitSizeMultipllier) {

		case 1:
			this.signalBufferBitFormat = BitSize.BIT_8;
			break;
		case 2:
			this.signalBufferBitFormat = BitSize.BIT_16;
			break;
		default:

			throw new IllegalArgumentException(
					"Source stream has more bits per sample["
							+ af.getSampleSizeInBits()
							+ "] than we can HANDLE!!!!!");

		}

	}

	protected void determineCodec() {

		AudioFormat af = (AudioFormat) receiveStream.getFormat();

		if (!af.getEncoding().equals(Codec.LINEAR_AUDIO.getEncoding())) {
			codec = CodecLocator.getCodec(af, Codec.LINEAR_AUDIO);
		}

	}

	public void transferData(PushBufferStream stream) {

		// stream==this.receiveStream
		Buffer buffer = new Buffer();
		try {
			stream.read(buffer);
		} catch (IOException e) {
		}

		byte[] data = (byte[]) buffer.getData();
		double[] rawData = null;

		data = decompress(data);
		switch (this.signalBufferBitFormat) {
		case BIT_8:
			rawData = new double[data.length];
			for (int i = 0; i < rawData.length; i++)
				rawData[i] = data[i];

			// do nothing
			break;
		case BIT_16:
			rawData = new double[data.length / 2];

			int dataPointer = 0;
			for (int i = 0; i < rawData.length; i++) {
				rawData[i] = (data[dataPointer++] << 8)
						| (data[dataPointer++] & 0xff);

			}

			break;
		default:
			logger.severe("!!!!!!!!!!!!!!WE DIDNT PROCESS!!!!!!!!!!");
			return;
		}

		for (double d : rawData) {
			this.signalValues.add(new Complex(d, 0));
		}

		if (this.signalValues.size() % this.getSecondsBufferSize() == 5) {
			doIntermediateDFT();
		}

	}

	// ----- PROCESS compressed data to linear

	private byte[] decompress(byte[] data) {
		if (codec != null) {
			data = codec.process(data);
		}
		return data;
	}

	protected ArrayList<Complex> getSignalValues() {
		return this.signalValues;
	}

	/**
	 * Returns deft set of results, depending on algorithm and signal number of
	 * passed array may vary.
	 * 
	 * @return
	 */
	protected ArrayList<double[]> getDFTResults() {

		doIntermediateDFT();

		return this.results;
	}

	public File getPlotFile() {
		return plotFile;
	}

	/**
	 * Sets graphical plot data file, must be supported format. This hass tobe
	 * set befre test runs.
	 * 
	 * @param plotFile
	 */
	public void setPlotFile(File plotFile) {
		this.plotFile = plotFile;
	}

	/**
	 * This function should be overriden if extending class is not using sines
	 * defined in SuperXCase or comparison of results is more complex than just
	 * check in whole signal for certain frequencies
	 * 
	 * @param frequencies -
	 *            optionaly pass sine frequencies here.
	 * @return
	 */
	protected String validateResults(int[] frequencies,
			boolean performStrayFrequenciesCheck) {

		int[] freqs = null;
		if (frequencies != null)
			freqs = frequencies;
		else
			freqs = this.sineFrequencies;

		for (double[] r : results) {
			String report = this.performFrequencyCheck(r, freqs,
					performStrayFrequenciesCheck);
			if (report != null)
				return report;
		}
		return null;
	}

	/**
	 * Implement if frequency checck is more complex than matching frequencies
	 * 
	 * @param line
	 * @param freq
	 * @return null in case of success, String containing report otherwise
	 */
	protected String performFrequencyCheck(double[] line, int[] freq,
			boolean performStrayNumberFrequenciesCheck) {

		ArrayList<Integer> found = new ArrayList<Integer>();
		ArrayList<Integer> duplicate = new ArrayList<Integer>();

		for (int i = 0; i < line.length / 2; i++) {

			if (line[i] >= this.peakPercentage) {
				found.add(i);
				duplicate.add(i);
			}
		}

		// ArrayList<Integer> matched = new ArrayList<Integer>();
		Iterator<Integer> it = found.iterator();
		while (it.hasNext()) {
			Integer f = it.next();
			for (int i = 0; i < freq.length; i++) {
				if (f.intValue() == freq[i]) {
					it.remove();
				}
			}
		}

		if (found.size() == 0) {
			return null;
		} else {

			// We have to perform stray number check.
			if (performStrayNumberFrequenciesCheck) {

				it = found.iterator();
				while (it.hasNext()) {
					Integer f = it.next();
					for (int i = 0; i < freq.length; i++) {
						int abs = Math.abs(f.intValue() - freq[i]);
						if (abs < -this.strayNumber) {
							it.remove();
						}
					}
				}

				if (found.size() == 0)
					return null;

			}

			return "Failed to match all frequencies[" + found
					+ "] from set of peaks[" + duplicate
					+ "] with frequencies[" + Arrays.toString(freq)
					+ "] with peak percentage[" + this.peakPercentage + "]";

		}

	}

	protected void doDataPlot() throws Exception {
		if (results.size() == 0) {
			doFinalDFT();
		}
		if (this.plotFile == null) {
			throw new IllegalStateException("Plot file has not been set!!!!");
		}

		if (this.firstPlotRun) {
			if (this.plotFile.exists()) {
				this.plotFile.delete();
				this.plotFile.createNewFile();
			} else {
				this.plotFile.createNewFile();
			}
			firstPlotRun = false;
		}

		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(this.plotFile));

		BufferedImage bi = new BufferedImage(this.plotUnitWidth
				* results.size(), this.plotUnitHeight * results.get(0).length,
				BufferedImage.TYPE_INT_RGB);

		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
		Graphics2D g = bi.createGraphics();

		for (int resultsCount = 0; resultsCount < results.size(); resultsCount++) {
			double[] result = results.get(resultsCount);

			for (int i = 0; i < result.length; i++) {
				try {
					g.setColor(ColorScale.getSpectrumColor(result[i]));
					g.drawRect(resultsCount * this.plotUnitWidth, i
							* this.plotUnitHeight, this.plotUnitWidth,
							this.plotUnitHeight);
				} catch (Exception e) {
					logger.info("Exception on[" + i + "][" + result[i] + "]");
					throw e;
				}
			}

		}
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
		int quality = 5;
		quality = Math.max(0, Math.min(quality, 100));
		param.setQuality((float) quality / 100.0f, false);
		encoder.setJPEGEncodeParam(param);
		encoder.encode(bi);
		bos.close();
	}

	protected void doIntermediateDFT() {

		// Here we have biggest2NSize - which is our ~ second samples count
		// sliding window, we will slide through signal values with that, in the
		// end, we will take remainig values, fill array with zeros, and pass
		// into DFT function
		int biggest2NSize = getSecondsBufferSize();
		// Offset from begining of results
		int offset = 0;
		Complex[] localData = null;

		while (signalValues.size() > offset + biggest2NSize - 1) {

			List<Complex> list = signalValues.subList(offset, offset
					+ biggest2NSize);
			offset += biggest2NSize;
			Complex[] x = new Complex[1];
			x = list.toArray(x);
			// FIXME: Oleg,Amit, Vladimir - is this correct - I use local max
			// value to calibrate?
			Complex[] y = FFT.fft(x);

			results.add(calibrate(y));

		}
		if (offset != 0)
			signalValues.remove(signalValues.subList(0, offset));
	}

	protected void doFinalDFT() {

		// Here we have biggest2NSize - which is our ~ second samples count
		// sliding window, we will slide through signal values with that, in the
		// end, we will take remainig values, fill array with zeros, and pass
		// into DFT function
		int biggest2NSize = getSecondsBufferSize();

		// Offset from begining of results
		int offset = 0;
		doIntermediateDFT();

		// Here is a chance we have some leftovers, we need to dft them also
		if (signalValues.size() - offset != 0) {
			Complex[] x = new Complex[biggest2NSize];
			for (int i = 0; i < x.length; i++) {
				Complex zero = new Complex(0, 0);
				Complex c = null;
				if (i + offset < signalValues.size()) {
					c = signalValues.get(i + offset);
				} else {
					c = zero;
				}
				x[i] = c;
			}

			results.add(calibrate(FFT.fft(x)));
		}

	}

	protected int getSecondsBufferSize() {
		AudioFormat af = (AudioFormat) this.receiveStream.getFormat();
		double sampleRate = af.getSampleRate();
		int secondBufferSize = (int) (Math.round(sampleRate));
		// we use tuckey with radix-2, we need 2^n size (2*N to be specific, but
		// we go down /2 each time)
		// FIXME: is int enough?

		int biggest2NSize = 2;

		while (biggest2NSize != secondBufferSize
				&& biggest2NSize < secondBufferSize) {
			biggest2NSize = biggest2NSize * 2;
		}
		return biggest2NSize;
	}

	protected double[] calibrate(Complex[] y) {

		double max = 0;
		double result[] = new double[y.length / 2];
		int count = 0;
		for (Complex c : y) {
			double v = Math.sqrt(c.re() * c.re() + c.im() * c.im());
			if (v > max)
				max = v;
			if (count >= y.length / 2)
				break;
			result[count++] = v;

		}
		for (int i = 0; i < result.length; i++) {
			result[i] = result[i] / max;
		}
		return result;
	}

	public static enum BitSize {
		// uff, is there a smart way of concating those?

		BIT_8, BIT_16;
	}
}

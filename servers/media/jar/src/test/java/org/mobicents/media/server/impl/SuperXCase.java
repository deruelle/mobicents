package org.mobicents.media.server.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

public abstract class SuperXCase extends TestCase implements
		BufferTransferHandler {
	/**
	 * Test signal duration in mS
	 */
	protected int testSignalDuration = 20;
	/**
	 * Size of buffer taken into dft - this is milisecond buffer size
	 */
	protected int dftWindowSize = 1000;
	protected int[] sineFrequencies = new int[] { 50,80 };
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

	/*
	 * All of the contain output streams - we will receive data through those
	 */
	private HashMap<PushBufferStream, ArrayList<double[]>> results = new HashMap<PushBufferStream, ArrayList<double[]>>(
			5);
	private HashMap<PushBufferStream, ArrayList<Complex>> signalValues = new HashMap<PushBufferStream, ArrayList<Complex>>(
			5);
	private HashMap<PushBufferStream, StreamData> streamData = new HashMap<PushBufferStream, StreamData>();

	protected Logger logger = Logger.getLogger(this.getClass()
			.getCanonicalName());

	/*
	 * As we relly on SuperXCase and want to test multiple output from signals,
	 * for now I assume that we will receive mix of input signals in few
	 * streams, this outputStream1==outputStreamN Not sure how to test input
	 * streams vs output streams, is it possible? (other way than only testing
	 * output for expected values??)
	 */

	class StreamData {
		Codec codec = null;
		/**
		 * This shows how many bytes we have per sample - 1==8,2==16, etc
		 */
		int bitSizeMultipllier = 1;
		BitSize signalBufferBitFormat = null;
		int secondLengthBufferSize = 0;
	}

	protected int plotUnitWidth = 1;
	protected int plotUnitHeight = 1;
	protected File plotFile = null;
	protected boolean firstPlotRun = true;

	protected File makeDumpFile(String fileName) {
		return new File(dumpDir, fileName);
	}
	
	
	protected void setUp() throws Exception {

		results = new HashMap<PushBufferStream, ArrayList<double[]>>(5);
		signalValues = new HashMap<PushBufferStream, ArrayList<Complex>>(5);
		streamData = new HashMap<PushBufferStream, StreamData>();
		super.setUp();
	}

	protected void tearDown() throws Exception {
		results.clear();
		results = null;
		signalValues.clear();
		signalValues = null;
		for(PushBufferStream pbs: streamData.keySet())
			pbs.setTransferHandler(null);
		streamData.clear();
		streamData = null;
		super.tearDown();
	}

	protected void addReceiveStream(PushBufferStream receiverStream) {

		results.put(receiverStream, new ArrayList<double[]>(50));
		signalValues.put(receiverStream, new ArrayList<Complex>(50));
		prepareStreamData(receiverStream);
		receiverStream.setTransferHandler(this);

	}

	private void prepareStreamData(PushBufferStream receiverStream) {

		StreamData data = new StreamData();
		if (receiverStream == null)
			throw new IllegalStateException("resource not configured!!!");
		Format fmt = receiverStream.getFormat();

		if (!(fmt instanceof AudioFormat)) {
			throw new IllegalArgumentException(
					"Source stream is not an audio stream!!!!");
		}

		AudioFormat af = (AudioFormat) fmt;
		data.bitSizeMultipllier = af.getSampleSizeInBits() / 8;

		switch (data.bitSizeMultipllier) {

		case 1:
			data.signalBufferBitFormat = BitSize.BIT_8;
			break;
		case 2:
			data.signalBufferBitFormat = BitSize.BIT_16;
			break;
		default:

			throw new IllegalArgumentException(
					"Source stream has more bits per sample["
							+ af.getSampleSizeInBits()
							+ "] than we can HANDLE!!!!!");

		}

		data.secondLengthBufferSize = getSecondsBufferSize(receiverStream);
		data.codec = determineCodec(receiverStream);
		streamData.put(receiverStream, data);

	}

	protected Codec determineCodec(PushBufferStream receiverStream) {
		AudioFormat af = (AudioFormat) receiverStream.getFormat();

		if (!af.getEncoding().equals(Codec.LINEAR_AUDIO.getEncoding())) {
			return CodecLocator.getCodec(af, Codec.LINEAR_AUDIO);
		}

		return null;
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
		StreamData streamData = this.streamData.get(stream);
		data = decompress(data, streamData);
		switch (streamData.signalBufferBitFormat) {
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

		ArrayList<Complex> _values = signalValues.get(stream);
		//StringBuffer sb=new StringBuffer();
		//int cc=0;
		for (double d : rawData) {
			//logger.info("T -> "+d);
			//sb.append("["+(cc++)+"]["+d+"]\n");
			_values.add(new Complex(d, 0));
		}
		//logger.info("TRT:\n"+sb);
		if (_values.size() / streamData.secondLengthBufferSize == 4) {
			//StringBuffer kb=new StringBuffer();
			//for(int k=0;k<_values.size();k++)
			//	kb.append("["+k+"]["+_values.get(k)+"]\n");
		
			//logger.info("CALLING DFT ON:\n"+kb);
			doIntermediateDFT(stream);
		}

	}

	// ----- PROCESS compressed data to linear

	private byte[] decompress(byte[] data, StreamData streamData) {
		//if (streamData.codec != null) {
		//	data = streamData.codec.process(data);
		//}
		return data;
	}

	// protected ArrayList<Complex> getSignalValues() {
	// return this.signalValues;
	// }

	/**
	 * Returns deft set of results, depending on algorithm and signal number of
	 * passed array may vary.
	 * 
	 * @return
	 */
	protected ArrayList<double[]> getDFTResults(PushBufferStream receiverStream) {

		return results.get(receiverStream);
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
		
		for (PushBufferStream pbs : results.keySet()) {
			for (double[] r : results.get(pbs)) {
				
				//StringBuffer sb=new StringBuffer();
				//for(double d:r)
				//{
				//	sb.append(d+"\n");
				//}
				//logger.info("PERFORMING CHECK ON["+r.length+"]  \n"+sb);
				
				String report = this.performFrequencyCheck(r, freqs,
						performStrayFrequenciesCheck);
				if (report != null)
					return report;
			}

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
		if(found.size()< freq.length)
		{
			StringBuffer sb=new StringBuffer();
			int ccc=0;
			for(double i:line)
			{
				if(i>0.85d)
					sb.append("["+(ccc++)+"]["+i+"]<---------\n");
				else
					sb.append("["+ccc+++"]["+i+"]\n");
			}
			
			return "Failed to find frequency that comes up to ["+this.peakPercentage+"] of maximum signal value, found only ["+found.size()+"] "+found+"\n"+sb;
		}
		logger.info("Performing frequency check on found "+Arrays.toString(found.toArray())+" vs declared "+Arrays.toString(freq));
		// ArrayList<Integer> matched = new ArrayList<Integer>();
		Iterator<Integer> it = found.iterator();
		while (it.hasNext()) {
			Integer f = it.next();
			for (int i = 0; i < freq.length; i++) {
				if (f.intValue() == freq[i]) {
					it.remove();
					break;
				}
			}
		}

		if (found.size() == 0) {
			return null;
		} else {

			logger.warning("Performing Stray frequency check with stray number "+this.strayNumber);
			// We have to perform stray number check.
			if (performStrayNumberFrequenciesCheck) {

				it = found.iterator();
				while (it.hasNext()) {
					Integer f = it.next();
					for (int i = 0; i < freq.length; i++) {
						int abs = Math.abs(f.intValue() - freq[i]);
						if (abs < this.strayNumber) {
							logger.warning("Removing stray frequency - distance["+abs+"]");
							it.remove();
						}else
						{
							logger.info("Not removing stray frequency - distance["+abs+"]");
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

		Iterator<PushBufferStream> streamIterator = results.keySet().iterator();
		int outputIndex = 0;
		while (streamIterator.hasNext()) {

			PushBufferStream pbs = streamIterator.next();
			ArrayList<double[]> _results = results.get(pbs);
			if (_results.size() == 0) {
				doFinalDFT();
			}
			if (this.plotFile == null) {
				throw new IllegalStateException(
						"Plot file has not been set!!!!");
			}
			String plotFilePattern = this.plotFile.toURI().toString();

			String currentPlotFileURL = plotFilePattern.substring(0,
					plotFilePattern.lastIndexOf("."))
					+ (outputIndex++)
					+ plotFilePattern.substring(plotFilePattern
							.lastIndexOf("."), plotFilePattern.length());
			File currentPlotFile = new File(currentPlotFileURL.replace("file:/", ""));

			logger.fine("Ploting output into["+currentPlotFile+"]");
			
			if (currentPlotFile.exists()) {
				currentPlotFile.delete();
				currentPlotFile.createNewFile();
			} else {
				currentPlotFile.createNewFile();
			}

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(currentPlotFile));

			BufferedImage bi = new BufferedImage(this.plotUnitWidth
					* _results.size(), this.plotUnitHeight
					* _results.get(0).length, BufferedImage.TYPE_INT_RGB);

			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			Graphics2D g = bi.createGraphics();

			for (int resultsCount = 0; resultsCount < results.size(); resultsCount++) {
				double[] result = _results.get(resultsCount);

				for (int i = 0; i < result.length; i++) {
					try {
						g.setColor(ColorScale.getSpectrumColor(result[i]));
						g.drawRect(resultsCount * this.plotUnitWidth, i
								* this.plotUnitHeight, this.plotUnitWidth,
								this.plotUnitHeight);
					} catch (Exception e) {
						logger.info("Exception on[" + i + "][" + result[i]
								+ "]");
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
	}

	protected void doIntermediateDFT(PushBufferStream stream) {
		// Here we have biggest2NSize - which is our ~ second samples count
		// sliding window, we will slide through signal values with that, in the
		// end, we will take remainig values, fill array with zeros, and pass
		// into DFT function

		int biggest2NSize = streamData.get(stream).secondLengthBufferSize;
		// Offset from begining of results
		int offset = 0;
		Complex[] localData = null;
		ArrayList<Complex> _values = signalValues.get(stream);
		ArrayList<double[]> _results = results.get(stream);
		while (_values.size() >  biggest2NSize ) {
			
			Complex[] x=new Complex[biggest2NSize];
			//StringBuffer sb=new StringBuffer("");
			
			for(int i=0;i<biggest2NSize;i++)
			{	
				//sb.append("["+i+"]["+_values.get(0)+"]");
				x[i]=_values.remove(0);
				//sb.append("["+x[i]+"]\n");
			}
			//logger.info(sb.toString());
			// FIXME: Oleg,Amit, Vladimir - is this correct - I use local max
			// value to calibrate?
			Complex[] y = FFT.fft(x);
			
			
			_results.add(calibrate(y));

		}

		
	}

	protected void doFinalDFT() {

		Iterator<PushBufferStream> streamIterator = results.keySet().iterator();
		while (streamIterator.hasNext()) {
			PushBufferStream pbs = streamIterator.next();
			int biggest2NSize = streamData.get(pbs).secondLengthBufferSize;

			// Offset from begining of results
			int offset = 0;
			doIntermediateDFT(pbs);
			ArrayList<Complex> _values = signalValues.get(pbs);
			ArrayList<double[]> _results = results.get(pbs);
			// Here is a chance we have some leftovers, we need to dft them also
			if (_values.size() - offset != 0) {
				Complex[] x = new Complex[biggest2NSize];
				for (int i = 0; i < x.length; i++) {
					Complex zero = new Complex(0, 0);
					Complex c = null;
					if (i + offset < _values.size()) {
						c = _values.get(i + offset);
					} else {
						c = zero;
					}
					x[i] = c;
				}

				_results.add(calibrate(FFT.fft(x)));
			}
		}
	}

	protected int getSecondsBufferSize(PushBufferStream receiverStream) {
		AudioFormat af = (AudioFormat) receiverStream.getFormat();
		double sampleRate = af.getSampleRate();
		int secondBufferSize = (int) (Math.round(sampleRate));
		// we use tuckey with radix-2, we need 2^n size (2*N to be specific, but
		// we go down /2 each time)
		// FIXME: is it enough?

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
		//StringBuffer sb=new StringBuffer();
		for (Complex c : y) {
			double v = Math.sqrt(c.re() * c.re() + c.im() * c.im());
			if (v > max)
				max = v;
			if (count >= y.length / 2)
				break;
			result[count++] = v;

		}
		
		for (int i = 0; i < result.length; i++) {
			
			//if(result[i]==max)
			//{
			//	sb.append("["+i+"]["+result[i]+"]["+(result[i] / max)+"] ***\n");
			//}else
			//{
			//	sb.append("["+i+"]["+result[i]+"]["+(result[i] / max)+"]\n");
			//}
			result[i] = result[i] / max;
		}
		//logger.info("CALIBRATE:\n"+sb);
		return result;
	}

	public static enum BitSize {
		// uff, is there a smart way of concating those?

		BIT_8, BIT_16;
	}

	protected abstract void validateInputVSOutput();

}

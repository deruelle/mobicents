package org.mobicents.media.server.impl.fft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;



import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.format.UnsupportedFormatException;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResource;
import org.mobicents.media.server.impl.common.MediaResourceState;
import org.mobicents.media.server.impl.jmf.dsp.Codec;
import org.mobicents.media.server.impl.jmf.dsp.CodecLocator;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.MediaSink;
import org.mobicents.media.server.spi.NotificationListener;

public class DFTFilterResource extends BaseResource implements MediaSink,
		BufferTransferHandler {

	private static Logger logger = Logger.getLogger(DFTFilterResource.class);

	private String id = null;
	private Connection conn = null;
	private DFTEndpointImpl endpoint = null;
	private Properties generalConfig = null;
	private Properties config = null;
	private PushBufferStream receiveStream = null;
	private StringBuffer result = null;
	private Codec codec=null;
	private BitSize signalBufferBitFormat = BitSize.BIT_8;
        private FFT fft = new FFT();
        
	/**
	 * This shows how many bytes we have per sample - 1==8,2==16, etc
	 */
	private int bitSizeMultipllier = 1;

	// Holds data that is to be DFTed, should be moved to double asap
	private Complex[] localData = null;
	private int currentPositionInBuffer = 0;
	private long samplesPerSecond=0;
	public DFTFilterResource(BaseEndpoint endpoint, Connection connection,
			Properties config) {
		this.id = connection.getId();
		this.conn = connection;
		this.endpoint = (DFTEndpointImpl) endpoint;
		this.generalConfig = config;
	}

	

	public void prepare(Endpoint endpoint, PushBufferStream mediaStream)
			throws UnsupportedFormatException {

		setState(MediaResourceState.PREPARED);
		this.receiveStream = mediaStream;
		this.receiveStream.setTransferHandler(this);
		determineCodec();
		determineBitSize();
		determineLocalBufferSize();

	}

	public void addListener(NotificationListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");

	}

	public void configure(Properties config) {
		this.config = config;
		// MORE

		setState(MediaResourceState.CONFIGURED);

	}

	public void release() {
		this.receiveStream.setTransferHandler(null);

		setState(MediaResourceState.NULL);
		// HERE DO THE DUMP
		// new XMLDump().doDump(this.endpoint.getDumpDir(), this.endpoint
		// .getTestName(), this.report);

		// new SimpleDump().doDump(this.endpoint.getDumpDir(), this.endpoint
		// .getTestName(), this.report);

		
		//we have to dump last samples if present:
		if(this.currentPositionInBuffer!=0)
		{
			for(int i=this.currentPositionInBuffer+1;i<this.localData.length;i++)
			{
				this.localData[i]=new Complex(0,0);
			}
			this.currentPositionInBuffer=0;
			doDFT();
			
		}
		
		try {
			File dumpFile = new File(this.endpoint.getDumpDir(), this.endpoint
					.getTestName()
					+ ".txt");
			if (dumpFile.exists()) {
				dumpFile.delete();
			} else {
				{
					dumpFile.createNewFile();
				}
				
				
				FileOutputStream fos = new FileOutputStream(dumpFile);
				fos.write(result.toString().getBytes());
				//ObjectOutputStream oos = new ObjectOutputStream(fos);
				//oos.writeUTF(result.toString());	
				
				
			}
		} catch (Exception e) {
			logger.error("Failed to dump DFT results into file!!!");
			e.printStackTrace();
		}
		


	}

	public void removeListener(NotificationListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");

	}

	public void start() {
		setState(MediaResourceState.STARTED);

	}

	public void stop() {
		if (getState() == MediaResourceState.STARTED) {
			setState(MediaResourceState.PREPARED);
		}

	}

	private StringBuffer runBuffer=new StringBuffer();
	public void transferData(PushBufferStream stream) {

		// stream==this.receiveStream
		Buffer buffer = new Buffer();
		try {
			stream.read(buffer);
		} catch (IOException e) {
		}

		byte[] data = (byte[]) buffer.getData();
		double[] rawData = null;
		
		
		
		
		data=decompress(data);
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
			for (int i = 0; i < rawData.length; i++)
			{
				rawData[i] = (data[dataPointer++] << 8)
						| (data[dataPointer++] & 0xff);
				//logger.info("---->WROTE["+rawData[i]+"]");
			}
			
			
			break;
		default:
			logger.error("!!!!!!!!!!!!!!WE DIDNT PROCESS!!!!!!!!!!");
			return;
		}
		
		
		
		
		// TODO: This is a waste, we need something better here.

		for (int i = 0; i < rawData.length; i++) {
			if (this.currentPositionInBuffer == this.samplesPerSecond) {
				logger.info("POSITION iN BFFER["+this.currentPositionInBuffer+"]");
				//System.out.println("POSITION iN BFFER["+this.currentPositionInBuffer+"]");
				doDFT();
				this.currentPositionInBuffer = 0;
			}

			this.localData[this.currentPositionInBuffer++] = new Complex(
					rawData[i], 0);
			//runBuffer.append("["+(currentPositionInBuffer-1)+"] "+localData[currentPositionInBuffer-1]+"\n");
		}
		//here we do doubl check, in case we get only 
		if (this.currentPositionInBuffer == this.samplesPerSecond) {
			runBuffer.append(Arrays.toString(this.localData)+"\n");
			doDFT();
			this.currentPositionInBuffer = 0;
		}

	}
	
	
	
	
	
//---------- METHODS TO BE CALLED IN PREAPRE method of MediaSink
	
	private void determineBitSize() {
		if (this.receiveStream == null
				|| this.getState() == MediaResourceState.NULL)
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

	private void determineLocalBufferSize() {
		AudioFormat af = (AudioFormat) this.receiveStream.getFormat();
		double sampleRate = af.getSampleRate();  

		// TODO: is int enough? Oleg!! Should sampleRate be taken from remote AudioFormat?
		//int secondBufferSize = (int) (Math.round(sampleRate))*this.bitSizeMultipllier;
		
		//this.bitSizeMultiplier is not used since ew store everything as double from the begginging, composition of doubles is done when bitsize is 16
		int secondBufferSize = (int) (Math.round(sampleRate));
		// we use tuckey with radix-2, we need 2^n size (2*N to be specific, but we go down /2 each time)
		//FIXME: is int enough?
		
		int biggest2NSize=2;
		
		while(biggest2NSize!=secondBufferSize && biggest2NSize<secondBufferSize)
		{
			biggest2NSize=biggest2NSize*2;
		}

		
		this.localData=new Complex[biggest2NSize];

		for(int i=secondBufferSize-1;i<this.localData.length;i++)
		{
			this.localData[i]=new Complex(0,0);
		}
		
		//this.samplesPerSecond=secondBufferSize;
		this.samplesPerSecond=secondBufferSize;

		this.result = new StringBuffer(10 * 10 * secondBufferSize);

	}

	private void determineCodec()
	{
		
		AudioFormat af=(AudioFormat)receiveStream.getFormat();
		
		if(!af.getEncoding().equals(Codec.LINEAR_AUDIO.getEncoding()))
		{
			codec=CodecLocator.getCodec(af, Codec.LINEAR_AUDIO);
		}
		
	}

	//---------- HEART OF DFT Cooley-Tuckey RADIX2
	private void doDFT() {
		


		Complex[] y = fft.fft(this.localData);
		
		StringBuffer tmpLine=new StringBuffer(5*this.localData.length);
		
		double max=0;
		for (Complex c : y) {
			double v = Math.sqrt(c.re() * c.re() + c.im() * c.im());
			if(v>max)
				max=v;
			tmpLine.append(Double.toString(v) + ",");
		}
		
		this.result.append(tmpLine.toString()+"\n");

	}

	//----- PROCESS compressed data to linear
	
	private byte[] decompress(byte[] data) {
			if(codec!=null)
			{
				data=codec.process(data);
			}
		return data;
	}

	
	

	enum BitSize {
		// uff, is there a smart way of concating those?

		BIT_8, BIT_16;
	}

    public PushBufferStream newBranch(String branchID) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void remove(String branchID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
	
}

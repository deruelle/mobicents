/*
 * BaseConnection.java
 *
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
package org.mobicents.media.server.impl.resource.audio;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ScheduledFuture;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.rtp.BufferFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.resource.AudioPlayer;
import org.xiph.speex.spi.SpeexAudioFileReader;
import org.xiph.speex.spi.SpeexEncoding;

/**
 * 
 * @author Oleg Kulikov
 */
public class AudioPlayerImpl extends AbstractSource implements AudioPlayer, Runnable {
	private final static int MAX_ERRORS = 5;
	/** supported formats definition */
	private final static Format[] formats = new Format[] { AVProfile.L16_MONO, AVProfile.L16_STEREO, AVProfile.PCMA,
			AVProfile.PCMU, AVProfile.SPEEX, AVProfile.GSM, Codec.LINEAR_AUDIO };

	private Encoding GSM_ENCODING = new Encoding("GSM0610");
	/** format of the file */
	private AudioFormat format;
	/** audio stream */
	private transient AudioInputStream stream = null;
	/** the size of the packet in bytes */
	private int packetSize;
	/** packetization period in millisconds */
	private int period = 20;
	/** sequence number of the packet */
	private long seq = 0;
	/** Timer */
	// private transient TimerImpl timer;
	private transient ScheduledFuture worker;
	/** Name (path) of the file to play */
	private String file;
	/** Flag indicating end of media */
	private volatile boolean eom = false;
	/** The countor for errors occured during processing */
	private int errorCount;
	private BufferFactory bufferFactory = null;

	private static transient Logger logger = Logger.getLogger(AudioPlayerImpl.class);

	public AudioPlayerImpl(String name) {
		super(name);
		bufferFactory = new BufferFactory(10, name, 3528);
	}

	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * Sarts playback. Executes asynchronously.
	 */
	public void start() {
		if (worker != null && !worker.isCancelled()) {
			worker.cancel(true);
		}

		closeAudioStream();
		seq = 0;

		try {
			if (file.endsWith("spx")) {
				stream = new SpeexAudioFileReader().getAudioInputStream(new URL(file));
			} else {
				stream = AudioSystem.getAudioInputStream(new URL(file));
			}
			format = getFormat(stream);
			if (format == null) {
				throw new IOException("Unsupported format: " + stream.getFormat());
			}
			packetSize = getPacketSize();
			eom = false;

			period = getEndpoint().getTimer().getHeartBeat();
			worker = getEndpoint().getTimer().synchronize(this);
			started();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in file " + file, e);
			failed(e);
		}
	}

	/**
	 * Terminates player. Methods executes asynchronously.
	 */
	public void stop() {
		if (worker != null && !worker.isCancelled()) {
			worker.cancel(true);
			closeAudioStream();
		}
	}

	/**
	 * Gets the format of specified stream.
	 * 
	 * @param stream
	 *            the stream to obtain format.
	 * @return the format object.
	 */
	private AudioFormat getFormat(AudioInputStream stream) {
		Encoding encoding = stream.getFormat().getEncoding();
		if (encoding == Encoding.ALAW) {
			return AVProfile.PCMA;
		} else if (encoding == Encoding.ULAW) {
			return AVProfile.PCMU;
		} else if (encoding == SpeexEncoding.SPEEX) {
			return AVProfile.SPEEX;
		} else if (encoding.equals(GSM_ENCODING)) {
			return AVProfile.GSM;
		} else if (encoding == Encoding.PCM_SIGNED) {
			int sampleSize = stream.getFormat().getSampleSizeInBits();
			if (sampleSize != 16) {
				return null;
			}
			int sampleRate = (int) stream.getFormat().getSampleRate();
			if (sampleRate == 44100) {
				int channels = stream.getFormat().getChannels();
				return channels == 1 ? AVProfile.L16_MONO : AVProfile.L16_STEREO;
			} else if (sampleRate == 8000) {
				return Codec.LINEAR_AUDIO;
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * Calculates size of packets for the currently opened stream.
	 * 
	 * @return the size of packets in bytes;
	 */
	private int getPacketSize() {
		int packetSize = (int) (period * format.getChannels() * format.getSampleSizeInBits() * format.getSampleRate() / 8000);
		if (packetSize < 0) {
			// For Format for which bit is AudioFormat.NOT_SPECIFIED, 160 is
			// passed
			packetSize = 160;
			if(format == AVProfile.GSM){
				//For GSM the RTP Packet size is 33
				packetSize = 33;
			}			
		}
		return packetSize;
	}

	/**
	 * Called when player failed.
	 */
	protected void failed(Exception e) {
		AudioPlayerEvent evt = new AudioPlayerEvent(this, AudioPlayerEvent.FAILED);
		sendEvent(evt);
	}

	/**
	 * Called when player started to transmitt audio.
	 */
	public void started() {
		AudioPlayerEvent started = new AudioPlayerEvent(this, AudioPlayerEvent.STARTED);
		sendEvent(started);
	}

	/**
	 * Called when player reached end of audio stream.
	 */
	protected void endOfMedia() {
		closeAudioStream();
		AudioPlayerEvent evt = new AudioPlayerEvent(this, AudioPlayerEvent.END_OF_MEDIA);
		sendEvent(evt);
	}

	/**
	 * Reads packet from currently opened stream.
	 * 
	 * @param packet
	 *            the packet to read
	 * @param offset
	 *            the offset from which new data will be inserted
	 * @return the number of actualy read bytes.
	 * @throws java.io.IOException
	 */
	private int readPacket(byte[] packet, int offset) throws IOException {
		int length = 0;

		try {
			while (length < packetSize) {
				int len = stream.read(packet, offset + length, packetSize - length);
				if (len == -1 ) {
					return length;
				}
				length += len;
			}
			return length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return length;
	}

	/**
	 * Reads packet from currently opened stream into specified buffer.
	 * 
	 * @param buffer
	 *            the buffer object to insert data to
	 * @throws java.io.IOException
	 */
	private void readPacket(Buffer buffer) throws IOException {
		buffer.setLength(readPacket((byte[]) buffer.getData(), buffer.getOffset()));
	}

	/**
	 * Perform padding buffer with zeros to aling length.
	 * 
	 * @param buffer
	 *            the buffer for padding.
	 */
	private void padding(Buffer buffer) {
		int count = packetSize - buffer.getLength();
		byte[] data = (byte[]) buffer.getData();
		int offset = buffer.getOffset() + buffer.getLength();
		for (int i = 0; i < count; i++) {
			data[i + offset] = 0;
		}

		buffer.setLength(packetSize);
	}

	/**
	 * Closes audio stream
	 */
	private void closeAudioStream() {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
		}
	}

	private void doProcess1() {
		Buffer buffer = bufferFactory.allocate();
		
		try {
			readPacket(buffer);
		} catch (IOException e) {
			failed(e);
			worker.cancel(true);
			return;
		}
		
		if (buffer.getLength() == 0) {
			eom = true;
		} else if (buffer.getLength() < packetSize) {
			padding(buffer);
		}

		buffer.setDuration(period);
		buffer.setFormat(format);
		buffer.setTimeStamp(seq * period);
		buffer.setEOM(eom);
		buffer.setSequenceNumber(seq++);

		try {
			otherParty.receive(buffer);
			errorCount = 0;
			if (eom) {
				worker.cancel(true);
				this.endOfMedia();
			}
		} catch (Exception e) {
			errorCount++;
			if (errorCount == MAX_ERRORS) {
				failed(e);
				worker.cancel(true);
			}
		}
	}

	public Format[] getFormats() {
		return formats;
	}

	public void ended() {
		closeAudioStream();
	}

	public void run() {
		doProcess1();
	}

    public void setURL(String url) {
        setFile(url);
    }

    public String getURL() {
        return null;
    }

}

package org.mobicents.media.server.impl.resource.audio;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.resource.Recorder;

/**
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class RecorderImpl extends AbstractSink implements Recorder {

	private static final transient Logger logger = Logger.getLogger(Recorder.class);

	private final static Format[] formats = new Format[] { AVProfile.PCMA, AVProfile.PCMU, AVProfile.GSM,
			Codec.LINEAR_AUDIO };

	private volatile int recordTime = Recorder.DEFAULT_RECORD_TIME;
	private String recordDir = Recorder.EMPTY_STRING;
	private FileOutputStream file;
	private Thread recorderThread = null;
	private volatile RecorderStream recorderStream;
	private volatile boolean started = false;
	private volatile boolean first = true;

	public RecorderImpl(String name) {
		super(name);
	}

	public void setRecordTime(int recordTime) {
		if (recordTime < 0) {
			throw new IllegalArgumentException("Recording time cannot be less than 1 second.");
		}
		this.recordTime = recordTime;
	}

	public void setRecordDir(String recordDir) {
		if (recordDir == null) {
			throw new IllegalArgumentException(
					"RecordDir cannot be null. Set empty String Recorder.EMPTY_STRING instead of null");
		}
		this.recordDir = recordDir;

	}

	public void start(String uri) {
		try {
			started = true;
			first = true;

			int index = uri.lastIndexOf("/");
			if (index > 0) {
				String folderStructure = uri.substring(0, index);

				java.io.File file = new java.io.File(new StringBuffer(recordDir).append("/").append(folderStructure)
						.toString());
				boolean fileCreationSuccess = file.mkdirs();
			}
                        if (recordDir != null) {
                            uri = recordDir + "/" + uri;
                        }
			if (logger.isDebugEnabled()) {
				logger.debug("RECORDING TO " + uri);
			}
			file = new FileOutputStream(uri);

			if (recorderThread != null) {
				//FIXME:
				//dispose();
			}

			// sendEvent(RecorderEventType.STARTED, "NORMAL");
		} catch (Exception e) {
			started = false;
			release();
			logger.error("Could not start recording", e);
			failed(e);
		}
	}

	private void release() {
		try {
			if (file != null) {
				file.flush();
				file.close();
			}

			if (recorderThread != null) {
				this.recorderThread = null;
			}

			if (this.recorderStream != null) {
				this.recorderStream.close();
				this.recorderStream = null;
			}

			// this.runner=null;
		} catch (Exception e) {
			logger.error("Could not close recorder file", e);
			// Do we care to raise Event?
		}
	}

	protected void failed(Exception e) {
		RecorderEvent evt = new RecorderEvent(this, RecorderEvent.FAILED);
		sendEvent(evt);
	}

	protected void durationOver() {
		RecorderEvent evt = new RecorderEvent(this, RecorderEvent.DURATION_OVER);
		sendEvent(evt);
	}

	protected void stopped() {
		RecorderEvent evt = new RecorderEvent(this, RecorderEvent.STOPPED);
		sendEvent(evt);
	}

	public void stop() {
		// If started, Stop else do nothing
		if (this.started) {
			started = false;
			release();
			stopped();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Heck its already stopped");
			}
		}
	}

	protected void completed() {
		started = false;
		release();
		durationOver();
	}

	public Format[] getFormats() {
		return formats;
	}

	public boolean isAcceptable(Format format) {
		return true;
	}

        private javax.sound.sampled.AudioFormat.Encoding getEncoding(String encodingName) {
            if (encodingName.equalsIgnoreCase("alaw")) {
                return javax.sound.sampled.AudioFormat.Encoding.ALAW;
            } else if (encodingName.equalsIgnoreCase("ulaw")) {
                return javax.sound.sampled.AudioFormat.Encoding.ULAW;
            } else {
                return javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
            }
        }
        
	public void receive(Buffer buffer) {
		if (!started) {
			buffer.dispose();
			return;
		}

		if (first) {
			first = false;

			AudioFormat fmt = (AudioFormat) buffer.getFormat();

			boolean signed = false;
			boolean bigEndian = false;
			float sampleRate = (float) fmt.getSampleRate();
			int sampleSizeInBits = fmt.getSampleSizeInBits();

			// recordinglength is number of bytes to be received.
			// TODO : If length never reached, app should call .stop() at some
			// point. Or should we also implement the Timer?
			long recordinglength = ((long) sampleRate * this.recordTime * sampleSizeInBits) / 8;

			recorderStream = new RecorderStream(this, recordinglength);
			if (fmt.getSigned() == 1) {
				signed = true;
			}

			if (fmt.getEndian() == 1) {
				bigEndian = true;
			}

                        
			javax.sound.sampled.AudioFormat fmt1 = new javax.sound.sampled.AudioFormat(
                                getEncoding(fmt.getEncoding()), sampleRate, sampleSizeInBits,
					fmt.getChannels(), 1,8000,bigEndian);
			AudioInputStream audioStream = new AudioInputStream(recorderStream, fmt1, recordinglength);

			this.recorderThread = new Thread(new RecorderRunnable(audioStream));
			this.recorderThread.start();
		}

		recorderStream.buffers.add(buffer);
		recorderStream.available += (buffer.getLength() - buffer.getOffset());

		if (recorderStream.blocked) {
			recorderStream.blocked = false;
			recorderStream.semaphore.release();
		}
	}

	private class RecorderRunnable implements Runnable {

		AudioInputStream audioStream = null;

		public RecorderRunnable(AudioInputStream audioStream) {
			this.audioStream = audioStream;
		}

		// TODO : Add support for GSM and SPEEX
		public void run() {
			try {
				AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
			} catch (IOException e) {
				if (started) {
					logger.error("Audio stream write error", e);
					//FIXME"
					//dispose();
					failed(e);
				}
			} catch (Exception e) {
				if (started) {
					logger.error("Audio stream generic error", e);
					//FIXME:
					//dispose();
					failed(e);
				}
			}
		}
	}

}

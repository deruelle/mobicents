/*
 * Recorder.java
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
package org.mobicents.media.server.impl.jmf.recorder;

import java.io.FileOutputStream;
import java.io.IOException;

import org.mobicents.media.server.impl.common.events.RecorderEventType;
import org.mobicents.media.server.impl.ivr.*;
import java.util.ArrayList;
import java.util.List;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.protocol.FileTypeDescriptor;
import org.mobicents.media.protocol.PushBufferStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.common.events.RecorderEventType;

/**
 * 
 * @author Oleg Kulikov
 */
public class Recorder {

	private String mediaType;
	private Format audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 8,
			1, AudioFormat.BIG_ENDIAN, AudioFormat.SIGNED);
	private List<RecorderListener> listeners = new ArrayList();
	private Logger logger = Logger.getLogger(Recorder.class);
	private int recordTime = 60;
	private FileOutputStream file;

	private Thread recorderThread = null;

	// private RecorderRunnable runner=null;
	public Recorder(String mediaType) {
		this.mediaType = FileTypeDescriptor.BASIC_AUDIO;
	}

	public Recorder(AudioFileFormat.Type mediaType, int recordTime) {
		this.recordTime = recordTime;
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.server.spi.ivr.IVREndpoint#record(URL)
	 */
	private void record(String uri, PushBufferStream stream) throws Exception {
		if (recorderThread != null) {
			//System.out.println("Recorder thread == " + recorderThread);
			dispose();
		}
		RecorderStream recorderStream = new RecorderStream(stream);
		javax.sound.sampled.AudioFormat fmt = new javax.sound.sampled.AudioFormat(
				8000, 16, 1, true, false);
		AudioInputStream audioStream = new AudioInputStream(recorderStream,
				fmt, 8000 * recordTime);
		// AudioInputStream audioStream =
		// AudioSystem.getAudioInputStream(recorderStream);
		file = new FileOutputStream(uri);
		this.recorderThread = new Thread(new RecorderRunnable(audioStream));
		this.recorderThread.start();

	}

	public void start(String file, PushBufferStream stream) {
		try {
			record(file, stream);
			sendEvent(RecorderEventType.STARTED, "NORMAL");
		} catch (Exception e) {
			dispose();
			logger.error("Could not start recording", e);
			sendEvent(RecorderEventType.FACILITY_ERROR, e.getMessage());
		}
	}

	private void dispose() {
		try {
			if (file != null) {
				file.flush();
				file.close();
			}

			if (recorderThread != null) {
				this.recorderThread.stop();
				this.recorderThread = null;
			}
			// this.runner=null;
		} catch (Exception e) {
			logger.error("Could not close recorder file", e);
			sendEvent(RecorderEventType.FACILITY_ERROR, e.getMessage());
		}
	}

	public void stop() {
		dispose();
		sendEvent(RecorderEventType.STOP_BY_REQUEST, "NORMAL");
	}

	protected synchronized void sendEvent(RecorderEventType eventID, String msg) {
		RecorderEvent evt = new RecorderEvent(this, eventID, msg);
		new Thread(new EventQueue(evt)).start();
	}

	public void addListener(RecorderListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(RecorderListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	/**
	 * Implements async event sender
	 */
	private class EventQueue implements Runnable {

		private RecorderEvent evt;

		public EventQueue(RecorderEvent evt) {
			this.evt = evt;
		}

		public void run() {
			synchronized (listeners) {
				for (RecorderListener listener : listeners) {
					listener.update(evt);
				}
			}
		}
	}

	private class RecorderRunnable implements Runnable {
		AudioInputStream audioStream = null;

		public RecorderRunnable(AudioInputStream audioStream) {
			super();
			this.audioStream = audioStream;
		}

		public void run() {
			try {
				AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file);
			} catch (IOException e) {

				e.printStackTrace();
				sendEvent(RecorderEventType.FACILITY_ERROR, e.getMessage());
				dispose();
			}

		}

	}

}

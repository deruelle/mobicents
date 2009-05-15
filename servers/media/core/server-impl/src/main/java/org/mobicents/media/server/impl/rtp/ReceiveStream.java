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
package org.mobicents.media.server.impl.rtp;

import java.util.concurrent.ScheduledFuture;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;

/**
 * 
 * @author Oleg Kulikov
 */
public class ReceiveStream extends AbstractSource implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2277812497480986797L;
	private JitterBuffer jitterBuffer;
	private volatile ScheduledFuture readerTask;
	private Buffer frame;
	protected Format[] formats;
	private RtpSocket rtpSocket;

	/** Creates a new instance of ReceiveStream */
	public ReceiveStream(RtpSocket rtpSocket, int jitter) {
		super("ReceiveStream");
		this.rtpSocket = rtpSocket;
		jitterBuffer = new JitterBuffer(jitter, rtpSocket.timer.getHeartBeat(), this.rtpSocket.getRtpMap());
	}

	protected void push(byte[] data) {
		jitterBuffer.write(data);
	}

	public void stop() {
            System.out.println("**** STOP RECEIVER *** " + readerTask);
		rtpSocket.stopReceiver();
		if (readerTask != null) {
			readerTask.cancel(true);
		}
	}

	public void start() {
		jitterBuffer.reset();
		rtpSocket.startReceiver();
		readerTask = rtpSocket.timer.synchronize(this);
	}

	public Format[] getFormats() {
		Format[] fmts = new Format[rtpSocket.getRtpMap().size()];
		rtpSocket.getRtpMap().values().toArray(fmts);
		return fmts;
	}

	public void run() {
		frame = jitterBuffer.read();

		if (frame == null) {
			return;
		}

		if (otherParty == null) {
			return;
		}

		// The sink for ReceiveStream is Processor.Input
		try {
			otherParty.receive(frame);
		} catch (Exception e) {
		}
	}
}

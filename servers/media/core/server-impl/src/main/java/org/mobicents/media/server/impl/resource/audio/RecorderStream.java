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
package org.mobicents.media.server.impl.resource.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import org.mobicents.media.Buffer;

/**
 * 
 * @author Oleg Kulikov
 */
public class RecorderStream extends InputStream {

	protected List<Buffer> buffers = new CopyOnWriteArrayList<Buffer>();
	protected int available = 0;
	protected Semaphore semaphore = new Semaphore(0);
	protected boolean blocked = false;
	private boolean eom = false;

	private RecorderImpl recorder;
	private long recordinglength;

	private int totalCount = 0;

	public RecorderStream(RecorderImpl recorder, long recordinglength) {
		this.recorder = recorder;
		this.recordinglength = recordinglength;
	}

	@Override
	public int available() {
		return available;
	}

	@Override
	public int read() throws IOException {

		if (eom) {
			return -1;
		}

		if (buffers.isEmpty()) {
			blocked = true;
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				return -1;
			}
		}

		byte[] buff = new byte[1];
		int count = readBytes(buff);

		totalCount += count;

		if (totalCount == this.recordinglength) {
			this.recorder.completed();
		}

		return count == -1 ? -1 : buff[0] & 0xff;
	}

	@Override
	public int read(byte[] buff) {

		if (buffers.isEmpty()) {
			blocked = true;
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				return -1;
			}
		}
		return readBytes(buff);
	}

	private int readBytes(byte[] buff) {
		if (buffers.isEmpty()) {
			return -1;
		}

		int count = 0;
		while (count < buff.length && !buffers.isEmpty()) {
			Buffer buffer = buffers.get(0);
			byte[] data = (byte[]) buffer.getData();

			int remainder = buff.length - count;
			int len = Math.min(remainder, buffer.getLength() - buffer.getOffset());

			System.arraycopy(data, buffer.getOffset(), buff, count, len);
			count += len;

			buffer.setOffset(buffer.getOffset() + len);
			if (buffer.isEOM()) {
				eom = true;
				// break;
			}
			if (buffer.getOffset() == buffer.getLength()) {
				buffer = buffers.remove(0);
				buffer.dispose();
			}
		}

		available -= count;
		return count;
	}
}

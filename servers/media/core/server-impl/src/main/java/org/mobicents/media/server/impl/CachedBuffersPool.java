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
package org.mobicents.media.server.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.mobicents.media.Buffer;

/**
 * This acts as pool for {@link org.mobicents.media.Buffer}
 * 
 * @author Oleg Kulikov
 */
public class CachedBuffersPool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5608451491729785004L;

	private final static int SIZE = 2400;
	private final static int MAX_CAPACITY = 100;

	private static ArrayList<Buffer> buffers = new ArrayList<Buffer>();
	private static ReentrantLock state = new ReentrantLock();

	/**
	 * If there are any <code>Buffer</code> available in buffers ArrayList,
	 * this method will take out the first available Buffer and return. If there
	 * are no available Buffer, it creates new and returns
	 * 
	 * @return Buffer from pool
	 */
	public static Buffer allocate() {
		state.lock();
		try {
			if (buffers.size() > 0) {
				return buffers.remove(0);
			}

			Buffer buffer = new Buffer();
			buffer.setData(new byte[SIZE]);

			return buffer;
		} finally {
			state.unlock();
		}
	}

	/**
	 * This method adds the <code>Buffer</code> to buffer ArrayList. If the
	 * list is already full, this will just ignore the Buffer
	 * 
	 * @param buffer
	 */
	public static synchronized void release(Buffer buffer) {
		state.lock();
		try {
			if (buffers.size() < MAX_CAPACITY) {
				buffers.add(buffer);
			}
		} finally {
			state.unlock();
		}
	}
}

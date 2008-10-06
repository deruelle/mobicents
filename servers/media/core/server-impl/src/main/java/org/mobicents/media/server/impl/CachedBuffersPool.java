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
 *
 * @author Oleg Kulikov
 */
public class CachedBuffersPool implements Serializable {

    private final static int SIZE = 2400;
    private final static int MAX_CAPACITY = 100;
    
    private static ArrayList<Buffer> buffers = new ArrayList();
    private static ReentrantLock state = new ReentrantLock();

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

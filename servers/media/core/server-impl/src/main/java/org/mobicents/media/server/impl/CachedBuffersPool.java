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
import org.mobicents.media.Buffer;
import org.mobicents.media.server.impl.clock.Quartz;

/**
 *
 * @author Oleg Kulikov
 */
public class CachedBuffersPool implements Serializable {
    
    private static ArrayList <Buffer> buffers = new ArrayList();
    
    public static synchronized Buffer allocate() {
        if (buffers.size() > 0) {
            //System.out.println("TAKE FROM POOL");
            return buffers.remove(0);
        }
        
        Buffer buffer = new Buffer();
        buffer.setData(new byte[Quartz.HEART_BEAT * 8 * 2] );
        
        return buffer;
    }
    
    public static synchronized void release(Buffer buffer) {
            //System.out.println("!!!!!RELEASED");
        buffers.add(buffer);
    }
}

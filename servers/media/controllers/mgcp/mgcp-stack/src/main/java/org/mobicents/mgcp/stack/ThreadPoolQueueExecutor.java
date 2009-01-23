/**
 * Start time:14:49:14 2008-11-21<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.mgcp.stack;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Start time:14:49:14 2008-11-21<br>
 * Project: mobicents-media-server-controllers<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class ThreadPoolQueueExecutor extends ThreadPoolExecutor {
    
    private BlockingQueue<Runnable> queue;
    
    public ThreadPoolQueueExecutor(int threads, int maxThreads, BlockingQueue<Runnable> workQueue) {
            super(threads, maxThreads, 90, TimeUnit.SECONDS, workQueue);
            this.queue = workQueue;
    }
    
    public BlockingQueue<Runnable> getQueue() {
            return queue;
    }

}

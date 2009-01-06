/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.clock;

import java.util.concurrent.ThreadFactory;

/**
 *
 * @author kulikov
 */
public class TimerThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "MediaTimer");
            t.setPriority(Thread.MAX_PRIORITY);
            return t;
        }
}

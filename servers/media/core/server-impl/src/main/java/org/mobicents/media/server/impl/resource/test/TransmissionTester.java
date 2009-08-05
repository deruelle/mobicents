/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */

package org.mobicents.media.server.impl.resource.test;

import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * This class is used for test transmission.
 *  
 * @author kulikov
 */
public class TransmissionTester implements NotificationListener {

    private final static int TEST_DURATION = 10;
    public final static short A = 100;
    public final static double T = 0.1;
    
    private MeanderGenerator gen;
    private MeanderDetector det;
    
    private MediaSink sink;
    private MediaSource source;
    
    private int outOfSeq;
    private int evtCount;
    private boolean fmtMissmatch = false;
    
    private boolean isPassed = false;
    private String msg = "Not started yet";
    
    public TransmissionTester(Timer timer) {
        gen = new MeanderGenerator("Tester[generator]", timer);
        gen.setAmplitude(A);
        gen.setPeriod(T);
        
        det = new MeanderDetector("Tester[detector]");
        det.setAmplitude(A);
        det.setPeriod(T);
        det.addListener(this); 
    }
    
    public MeanderGenerator getGenerator() {
        return gen;
    }
    
    public MeanderDetector getDetector() {
        return det;
    }
    
    public void connect(MediaSink sink) {
        this.sink = sink;
        gen.connect(sink);
    }
    
    
    public void connect(MediaSource source) {
        this.source = source;
        det.connect(source);
    }
    
    @SuppressWarnings("static-access")
    public void start() {
        evtCount = 0;
        isPassed = false;
        
        det.start();
        gen.start();
        
        try {
            Thread.currentThread().sleep(TEST_DURATION * 1000);
        } catch (InterruptedException e) {
            msg = "Test interrupted";
            return;
        }
        
        det.stop();
        gen.stop();
        
        if (sink != null) {
            gen.disconnect(sink);
        }
        
        if (source != null) {
            det.disconnect(source);
        }
        
        int count = (int)(TEST_DURATION /T/2);
        int diff = Math.abs(count - evtCount);
        
        if (diff > 2) {
            isPassed = false;
            msg = "Signal not detected or damaged, errors=" + diff;
            return;
        }
        
        if (outOfSeq > 0) {
            isPassed = false;
            msg = "There are out of sequence packets: " + outOfSeq;
            return;
        }
        
        if (fmtMissmatch) {
            isPassed = false;
            msg = "Format missmatch detected: ";
            return;
        }
        
        isPassed = true;
        msg = "";
    }

    public boolean isPassed() {
        return isPassed;
    }
    
    public String getMessage() {
        return msg;
    }
    
    public void update(NotifyEvent event) {
        if (event.getEventID() == MeanderEvent.EVENT_MEANDER) {
            evtCount++;
        } else if (event.getEventID() == MeanderEvent.EVENT_OUT_OF_SEQUENCE){
            outOfSeq++;
        } else if (event.getEventID() == MeanderEvent.EVENT_FORMAT_MISSMATCH) {
            fmtMissmatch = true;
        }
    }
    
}


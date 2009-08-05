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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.resource.fft.SpectraAnalyzer;
import org.mobicents.media.server.impl.resource.fft.SpectrumEvent;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.events.FailureEvent;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * This class is used for testing transmission.  
 * The carrier signal is a single frequency sine.
 * 
 * @author kulikov
 */
public class TransmissionTester2 implements NotificationListener {

    private final static int TEST_DURATION = 10;
    private int MAX_ERRORS = 1;
    private final static int FREQ_ERROR = 5;
    
    private final static short A = 1000;
    private final static int f = 150;
    private SineGenerator gen;
    private SpectraAnalyzer det;
    private MediaSink sink;
    private MediaSource source;
    private ArrayList<double[]> s = new ArrayList();

    private boolean isPassed = false;
    private String msg = "Not started yet";
    
    private Semaphore semaphore = new Semaphore(0);
    
    public TransmissionTester2(Timer timer) {
        gen = new SineGenerator("sine.generator", timer);
        gen.setAmplitude(A);
        gen.setFrequency(f);
        
        det = new SpectraAnalyzer("spectra.analyzer");
        det.addListener(this);
        gen.addListener(this);
    }
    
    public SineGenerator getGenerator() {
        return gen;
    }
    
    public SpectraAnalyzer getDetector() {
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
        s.clear();
        
        det.start();
        gen.start();
        
        isPassed = true;
        
        try {
            semaphore.tryAcquire(TEST_DURATION, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            msg = "Test interrupted";
            return;
        }
        
        gen.stop();
        det.stop();
        
        if (sink != null) {
            gen.disconnect(sink);
        }
        
        if (source != null) {
            det.disconnect(source);
        }
        
        try {
            Thread.currentThread().sleep(1000);
        } catch (Exception e) {
        }
        
        if (!isPassed) {
            return;
        }
        isPassed = verify(s, new int[]{f});
    }

    private boolean verify(ArrayList<double[]> spectra, int[] F) {
        int errorCount = 0;
        if (spectra.size() == 0) {
            msg = "Data not received";
            return false;
        }
        
        for (double[] ss : spectra) {            
            int[] ext = getFreq(ss);
            if (ext.length == 0) {
                msg = "Received silence [" + spectra.size() + " seconds]";
                return false;
            }
            boolean r = checkFreq(ext, F, FREQ_ERROR);
            if (!r) {
                errorCount++;
            }
        }
        return (errorCount <= MAX_ERRORS);
    }
    
    public boolean checkFreq(int[] ext, int[] F, int error) {
        for (int i = 0; i < F.length; i++) {
            if (Math.abs(ext[i] - F[i]) > error) {
                msg = "Expected " + F[i] + " but found " + ext[i];
                return false;
            }
        }

        return true;
    }
    
    public int[] getFreq(double[] s) {
        double max = findMax(s);

        int len = s.length / 2;
        double ss[] = new double[len];

        for (int i = 0; i < len; i++) {
            ss[i] = s[i] / max;
            ss[i] = ss[i] < 0.7 ? 0 : ss[i];
        }

        double[] diff = diff(ss);
        int[] ext = findExtremums(diff);
        
        return ext;
    }
    
    private double[] diff(double[] f) {
        double[] diff = new double[f.length];
        for (int i = 0; i < f.length - 1; i++) {
            diff[i] = f[i + 1] - f[i];
        }
        return diff;
    }
    
    private double findMax(double[] f) {
        double max = f[0];
        for (int i = 1; i < f.length; i++) {
            max = Math.max(max, f[i]);
        }
        return max;
    }

    private int[] findExtremums(double[] f) {
        List<Integer> ext = new ArrayList();
        for (int i = 0; i < f.length - 1; i++) {
            if (f[i] > 0 && f[i + 1] < 0) {
                ext.add(i);
            }
        }

        int[] res = new int[ext.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = ext.get(i);
        }
        return res;
    }
    
    public boolean isPassed() {
        return isPassed;
    }
    
    public String getMessage() {
        return msg;
    }
    
    public void update(NotifyEvent event) {
        if (event.getEventID() == SpectrumEvent.SPECTRA) {
            SpectrumEvent evt = (SpectrumEvent) event;
            s.add(evt.getSpectra());
        } else if (event.getEventID() == NotifyEvent.TX_FAILED) {
            isPassed = false;
            msg = "Transmission error: " + ((FailureEvent)event).getSource();
            ((FailureEvent)event).getException().printStackTrace();
            semaphore.release();
        }
    }
}

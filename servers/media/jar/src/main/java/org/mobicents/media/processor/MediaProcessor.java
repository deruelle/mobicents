/*
 * MediaProcessor.java
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

package org.mobicents.media.processor;

import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.ClockStartedError;
import javax.media.ClockStoppedException;
import javax.media.ConfigureCompleteEvent;
import javax.media.Control;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.GainControl;
import javax.media.IncompatibleSourceException;
import javax.media.IncompatibleTimeBaseException;
import javax.media.InternalErrorEvent;
import javax.media.Manager;
import javax.media.NotConfiguredError;
import javax.media.NotRealizedError;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.StartEvent;
import javax.media.StopByRequestEvent;
import javax.media.Time;
import javax.media.TimeBase;
import javax.media.Track;
import javax.media.TransitionEvent;
import javax.media.control.TrackControl;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class MediaProcessor implements Processor {
    
    private ContentDescriptor cd;
    
    private int state = Processor.Unrealized;
    private int targetState = Processor.Unrealized;
    
    private ArrayList listeners = new ArrayList();
    
    private TimeBase timeBase = Manager.getSystemTimeBase();
    private float rate;
    
    private Time mediaTime;
    
    private long mst = 0;
    private long tbst = 0;
    
    private TrackControl trackControls[];
    private DataSource outputDs;
    
    private boolean stopped = false;
    private Thread runThread;
    
    /** Creates a new instance of MediaProcessor */
    public MediaProcessor() {
    }
    
    public abstract TrackControl[] doConfigure();
    
    public void configure() {
        if (state == Processor.Unrealized) {
            new Thread(new ConfigureThread(this)).start();
        }
    }
    
    
    public TrackControl[] getTrackControls() throws NotConfiguredError {
        if (state < Processor.Configuring) {
            throw new NotConfiguredError("");
        }
        return trackControls;
    }
    
    public abstract ContentDescriptor[] getSupportedContentDescriptors()
    throws NotConfiguredError;
    
    public ContentDescriptor setContentDescriptor(ContentDescriptor cd) throws NotConfiguredError {
        if (state < Processor.Configured) {
            throw new NotConfiguredError("");
        }
        this.cd = cd;
        return cd;
    }
    
    public ContentDescriptor getContentDescriptor() throws NotConfiguredError {
        if (state < Processor.Configured) {
            throw new NotConfiguredError("");
        }
        return cd;
    }
    
    public DataSource getDataOutput() throws NotRealizedError {
        return outputDs;
    }
    
    public Component getVisualComponent() {
        return null;
    }
    
    public GainControl getGainControl() {
        return null;
    }
    
    public Component getControlPanelComponent() {
        return null;
    }
    
    public void start() {
        try {
            outputDs.connect();
            outputDs.start();
            state = Processor.Started;
            sendEvent(new StartEvent(this,
                    Processor.Realized, Processor.Realized, Processor.Started,
                    getMediaTime(), getTimeBase().getTime()));
        } catch (IOException ex) {
            sendEvent(new InternalErrorEvent(this, ex.getMessage()));
        }
    }
    
    public void addController(Controller controller)
    throws IncompatibleTimeBaseException {
        //do nothing
    }
    
    public void removeController(Controller controller) {
        //do nothing
    }
    
    public abstract void setSource(DataSource dataSource) throws IOException, IncompatibleSourceException;
    
    public int getState() {
        return state;
    }
    
    public int getTargetState() {
        return targetState;
    }
    
    public abstract Track[] doRealize();
    
    public void realize() {
        //TODO check state
        new Thread(new RealizeThread(this)).start();
    }
    
    public void prefetch() {
            sendEvent(new TransitionEvent(this, 
                    Processor.Realized, Processor.Prefetching, Processor.Prefetched));
    }
    
    public void deallocate() {
    }
    
    public void close() {
        if (state == Processor.Started) {
            stop();
        }
        sendEvent(new ControllerClosedEvent(this, "Closed by request"));
    }
    
    public Time getStartLatency() {
        return new Time(0);
    }
    
    public Control[] getControls() {
        return null;
    }
    
    public Control getControl(String control) {
        return null;
    }
    
    public void addControllerListener(ControllerListener listener) {
        listeners.add(listener);
    }
    
    public void removeControllerListener(ControllerListener listener) {
        listeners.remove(listener);
    }
    
    public void setTimeBase(TimeBase timeBase) {
        this.timeBase = timeBase;
    }
    
    public void syncStart(Time at) {
    }
    
    public void stop() {
        try {
            outputDs.stop();
            state = Processor.Realized;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            sendEvent(new StopByRequestEvent(this,
                    Processor.Started, Processor.Started, Processor.Realized,
                    getMediaTime()));
        }
    }
    
    public void setStopTime(Time time) {
    }
    
    public Time getStopTime() {
        return new Time(0);
    }
    
    public void setMediaTime(Time time) {
    }
    
    public Time getMediaTime() {
        return new Time(mst  + (timeBase.getNanoseconds() - tbst) * rate);
    }
    
    public long getMediaNanoseconds() {
        return (long)(mst  + (timeBase.getNanoseconds() - tbst) * rate);
    }
    
    public Time getSyncTime() {
        return null;
    }
    
    public TimeBase getTimeBase() {
        return timeBase;
    }
    
    public Time mapToTimeBase(Time time) throws ClockStoppedException {
        return time;
    }
    
    public float getRate() {
        return rate;
    }
    
    public float setRate(float rate) {
        if (state == Processor.Started) {
            throw new ClockStartedError("Processor started");
        }
        
        this.rate = 1;
        return 1;
    }
    
    public Time getDuration() {
        return new Time(0);
    }
    
    public synchronized void sendEvent(ControllerEvent evt) {
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ControllerListener listener = (ControllerListener) i.next();
            listener.controllerUpdate(evt);
        }
    }
    
    private class ConfigureThread implements Runnable {
        
        private MediaProcessor dsp;
        
        public ConfigureThread(MediaProcessor dsp) {
            this.dsp = dsp;
        }
        
        public void run() {
            dsp.state = Processor.Configuring;
            dsp.targetState = Processor.Configured;
            
            sendEvent(new TransitionEvent(dsp, 
                    Processor.Unrealized, Processor.Configuring, Processor.Configured));
            
            trackControls = doConfigure();
            System.out.println("********* " + trackControls);
            state = Processor.Configured;
            
            sendEvent(new ConfigureCompleteEvent(dsp,
                    Processor.Configuring, Processor.Configured, Processor.Configured));
        }
    }
    
    private class RealizeThread implements Runnable {
        
        private MediaProcessor dsp;
        
        public RealizeThread(MediaProcessor dsp) {
            this.dsp = dsp;
        }
        
        public void run() {
            dsp.state = Processor.Realizing;
            dsp.targetState = Processor.Realized;
            
            sendEvent(new TransitionEvent(dsp, 
                    Processor.Configured, Processor.Realizing, Processor.Realized));
            
            try {
                outputDs = new RawDataSource(dsp, doRealize(), trackControls);
                state = Processor.Realized;
                sendEvent(new RealizeCompleteEvent(dsp,
                        Processor.Realizing, Processor.Realized, Processor.Realized));
            } catch (Exception ex) {
                state = Processor.Unrealized;
                targetState = Processor.Unrealized;
                sendEvent(new InternalErrorEvent(dsp, ex.getMessage()));
            }
        }
    }

    private class PrefetchThread implements Runnable {
        
        private MediaProcessor dsp;
        
        public PrefetchThread(MediaProcessor dsp) {
            this.dsp = dsp;
        }
        
        public void run() {
            dsp.state = Processor.Prefetching;
            dsp.targetState = Processor.Prefetched;
            
            sendEvent(new TransitionEvent(dsp, 
                    Processor.Realized, Processor.Prefetching, Processor.Prefetched));
            
            try {
                sendEvent(new TransitionEvent(dsp, 
                        Processor.Realized, Processor.Prefetched, Processor.Prefetched));
            } catch (Exception ex) {
                state = Processor.Unrealized;
                targetState = Processor.Unrealized;
                sendEvent(new InternalErrorEvent(dsp, ex.getMessage()));
            }
        }
    }
    
    private class RunThread implements Runnable {
        
        private MediaProcessor dsp;
        
        public RunThread(MediaProcessor dsp) {
            this.dsp = dsp;
        }
        
        public void run() {
            sendEvent(new StartEvent(dsp,
                    Processor.Realized, Processor.Realized, Processor.Started,
                    getMediaTime(), getTimeBase().getTime()));
            while (!stopped) {
                
            }
            
            sendEvent(new StopByRequestEvent(dsp,
                    Processor.Started, Processor.Started, Processor.Realized,
                    getMediaTime()));
        }
    }
}

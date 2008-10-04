/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.mscontrol.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.impl.events.BaseRequestedEvent;
import org.mobicents.mscontrol.impl.events.BaseRequestedSignal;

/**
 * Used to provide backward compatibility with separated signal generation and 
 * event detection.
 * 
 * @author Oleg Kulikov
 */
public class PendingQueue implements Serializable {
    private final static int DELAY = 500;
    
    private Timer timer = new Timer();
    private TimerTask sender;    
    private boolean timerStarted;
    
    private ArrayList<MsRequestedSignal> requestedSignals = new ArrayList();
    private ArrayList<MsRequestedEvent> requestedEvents = new ArrayList();
    
    private Endpoint endpoint;
    private String connectionID;
    
    private ReentrantLock state = new ReentrantLock();
    
    protected PendingQueue(Endpoint endpoint, String connectionID) {
        this.endpoint = endpoint;
        this.connectionID = connectionID;
    }
    
    public void append(MsRequestedSignal requestedSignal) {
        state.lock();
        try {
            if (requestedSignals.isEmpty() && !timerStarted) {
                startTimer();
            }
            requestedSignals.add(requestedSignal);
        } finally {
            state.unlock();
        }
    }

    public void append(MsRequestedEvent requestedEvent) {
        state.lock();
        try {
            if (requestedEvents.isEmpty() && !timerStarted) {
                startTimer();
            }
            requestedEvents.add(requestedEvent);
        } finally {
            state.unlock();
        }
    }
    
    private void startTimer() {
        sender = new Sender();
        timer.schedule(sender, DELAY);
        timerStarted = true;
    }
    
    private class Sender extends TimerTask {

        @Override
        public void run() {
            RequestedSignal[] s = new RequestedSignal[requestedSignals.size()];
            int i = 0;
            for (MsRequestedSignal rs : requestedSignals) {
                s[i++] = ((BaseRequestedSignal)rs).convert();
            }
            requestedSignals.clear();
            
            RequestedEvent[] e = new RequestedEvent[requestedEvents.size()];
            i = 0;
            for (MsRequestedEvent re : requestedEvents) {
                e[i++] = ((BaseRequestedEvent)re).convert();
            }
            requestedEvents.clear();
            
            endpoint.execute(s, e, connectionID);
            timer.purge();
        }
        
    }
}

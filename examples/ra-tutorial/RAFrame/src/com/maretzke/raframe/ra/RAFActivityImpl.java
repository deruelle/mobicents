/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: RAFActivity.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 5th August 2005, 15:17
 * Changed: 13th September 2005, 10:30 (introduce RAFActivity interface)
 * Version: 1.0
 */
package com.maretzke.raframe.ra;

import com.maretzke.raframe.message.Message;
import com.maretzke.raframe.ratype.RAFActivity;

/**
 * The RAFActivityImpl implements the RAFActivity interface. For further information,
 * please refer to the description of the interface RAFActivity.
 *
 * @author Michael Maretzke
 */
public class RAFActivityImpl implements RAFActivity {
    // possible states of the resource adaptor according to the 
    // protocol definition
    private final static int NOTEXISTING = 0;
    private final static int ACTIVE = 1;
    private final static int ENDED = 2;
    private int state;
    // timestamp of creation of activity
    private long initTime;
    // counter for the various commands
    private int initCounter;
    private int anyCounter;
    private int endCounter;
    
    public RAFActivityImpl() {
        initTime = System.currentTimeMillis();
        state = NOTEXISTING;
    }
    
    public void initReceived() { 
        if (isValid(Message.INIT)) {
            initCounter++;
            state = ACTIVE;
        }         
    }
    public void anyReceived() { 
        if (isValid(Message.ANY)) {
            anyCounter++;
            state = ACTIVE;
        }         
    }
    public void endReceived() { 
        if (isValid(Message.END)) {
            endCounter++; 
            state = ENDED;            
        }
    }
    
    public int getInitCounter() { return initCounter; }
    public int getAnyCounter() { return anyCounter; }
    public int getEndCounter() { return endCounter; }
    
    public long getStartTime() { return initTime; }
    
    /**
     * Checks if an incoming command is valid according to the 
     * defined statemachine. The statemachine of the resource adaptor
     * is described above.
     *
     * @param command the integer representation of the command
     */
    public boolean isValid(int command) {
        if ((state == NOTEXISTING) && (command == Message.INIT)) 
            return true;
        if ((state == ACTIVE) && ((command == Message.ANY) || (command == Message.END)))
            return true;
        return false;
    }
}

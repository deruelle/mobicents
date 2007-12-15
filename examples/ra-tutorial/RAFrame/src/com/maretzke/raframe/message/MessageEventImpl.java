/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: MessageEventImpl.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 3rd August 2005, 10:00
 * Changed: 13th September 2005, 10:20 (introduce MessageEvent interface)
 * Version: 1.0
 */
package com.maretzke.raframe.message;

import java.util.EventObject;

/**
 * The MessageEvent wraps an Message object and adds the footprint of the requesting
 * object to it.
 * The implementation class MessageEventImpl implements the interface MessageEvent.
 *
 * @author Michael Maretzke
 */
public class MessageEventImpl extends EventObject implements MessageEvent {
    
    private Message message;
    
    /**
     * The factory method to generate a new instance of MessageEvent.
     * 
     * @param obj the generating object's reference
     * @param message message to attach to this event object
     * @return a newly created MessageEvent object
     */
    public static MessageEvent getInstance(Object obj, Message message) {
        return new MessageEventImpl(obj, message);
    }
    
    /**
     * Creates a new instance of MessageEvent 
     */
    private MessageEventImpl(Object source, Message message) {
        super(source);
        this.message = message;
    }
    
    public Message getMessage() {
        return message;
    }    
}

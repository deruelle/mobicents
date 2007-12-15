/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: MessageEvent.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 13nd September 2005, 10:19
 * Version: 1.0
 */
package com.maretzke.raframe.message;

/**
 * The MessageEvent wraps an Message object and adds the footprint of the requesting
 * object to it.  
 *
 * @author Michael Maretzke
 */
public interface MessageEvent {
    /**
     * Access the wrapped Message object which is contained in the implementation
     * object.
     *
     * @return the contained Message object.
     */
    public Message getMessage();
    
    /**
     * The object on which the Event initially occurred.
     *
     * @return   The object on which the Event initially occurred.
     */
    public Object getSource();
}


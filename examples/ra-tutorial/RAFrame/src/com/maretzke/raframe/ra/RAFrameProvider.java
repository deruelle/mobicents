/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: RAFrameProvider.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 13th September 2005, 11:45
 * Version: 1.0
 */
package com.maretzke.raframe.ra;

import com.maretzke.raframe.message.MessageFactory;
import com.maretzke.raframe.message.Message;
import com.maretzke.raframe.ratype.RAFrameResourceAdaptorSbbInterface;

/**
 * RAFrameProvider extends the interface RAFrameResourceAdaptorSbbInterface
 * and provides functionalities of the underlying resource adaptor to the Sbb. 
 * <br> 
 * For further information, please refer to JAIN SLEE Specification 1.0, Final 
 * Release Page 239 and following pages.
 * 
 * @see com.maretzke.raframe.ratype.RAFrameResourceAdaptorSbbInterface# com.maretzke.raframe.ratype.RAFrameResourceAdaptorSbbInterface
 * @author Michael Maretzke
 */
public interface RAFrameProvider extends RAFrameResourceAdaptorSbbInterface { 
    /**
     * send() is a functionality of the resource adaptor exposed to the Sbb. 
     * Due to the architecture of the Sbb having only access to the RAFrameProvider
     * instead of the RAFrameResourceAdaptor directly, the resource adaptor developer
     * is able to limit the privileges of a Sbb to invoke methods of the resource adaptor
     * directly. 
     *
     * @param message is the message to send
     */
    public void send(Message message);
       
    /**
     * The MessageFactory is needed inside the SBB to create Message objects, which are 
     * sent through the resource adaptor.
     */
    public abstract MessageFactory getMessageFactory();
    
    /**
     * The RAFrameProvider gives access to certain methods of the resource adaptor. The
     * Sbb may e.g. send Message objects through the resource adaptor.
     */
    public RAFrameResourceAdaptorSbbInterface getRAFrameProvider();   
}


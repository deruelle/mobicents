/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: RAFrameProviderImpl.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 5th August 2005, 10:42
 * Changed: 13th September 2005, 11:45 (introduce RAFrameProvider interface)
 * Version: 1.0
 */
package com.maretzke.raframe.ra;

import org.apache.log4j.Logger;
import com.maretzke.raframe.message.MessageFactory;
import com.maretzke.raframe.message.Message;
import com.maretzke.raframe.ratype.RAFrameResourceAdaptorSbbInterface;

/**
 * RAFrameProviderImpl implements the interface RAFrameProvider
 * and provides functionalities of the underlying resource adaptor to the Sbb. 
 * <br> 
 * For further information, please refer to the interface RAFrameProvider.
 * 
 * @author Michael Maretzke
 */
public class RAFrameProviderImpl implements RAFrameResourceAdaptorSbbInterface { 
    private static Logger logger = Logger.getLogger(RAFrameProviderImpl.class);
    private MessageFactory messageFactory;
    // reference to the RAFrame resource adaptor
    private RAFrameResourceAdaptor ra;
    
    /**
     * Create an instance of RAFrameProvicer
     * 
     * @param ra the parent resource adaptor
     * @param messageFactory the associated message factory
     * @return a new created instance of RAFrameProvider
     */
    public RAFrameProviderImpl(RAFrameResourceAdaptor ra, MessageFactory messageFactory) {
        this.ra = ra;
        this.messageFactory = messageFactory;
    }

    public RAFrameResourceAdaptor getRAFrameRA() {
        logger.debug("getRAFrameRA() called.");
        return ra;
    }
    
    /**
     * send() is a functionality of the resource adaptor exposed to the Sbb. 
     * Due to the architecture of the Sbb having only access to the RAFrameProvider
     * instead of the RAFrameResourceAdaptor directly, the resource adaptor developer
     * is able to limit the privileges of a Sbb to invoke methods of the resource adaptor
     * directly. 
     *
     * @param message is the message to send
     */
    public void send(Message message) {
        logger.debug("Sending the message to the stack");
        ra.send(message.getId() + ": " + message.getCommand());
    }
    
    // implements RAFrameResourceAdaptorSbbInterface
    public RAFrameResourceAdaptorSbbInterface getRAFrameProvider() {
        return this;
    }
    
    // implements RAFrameResourceAdaptorSbbInterface
    public MessageFactory getMessageFactory() {
        logger.debug("getMessageFactory() called.");
        return messageFactory;
    }       
}

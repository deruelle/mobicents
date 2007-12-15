/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: MessageFactoryImpl.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 4th August 2005, 12:36
 * Version: 1.0
 */
package com.maretzke.raframe.message;

/**
 * MessageFactoryImpl implements the functions of MessageFactory.
 *
 * @author Michael Maretzke
 */
public class MessageFactoryImpl implements MessageFactory {
    
    public MessageFactoryImpl() {
    }
    
    public Message createMessage(String id, String command) {
        return MessageImpl.getInstance(id, command);
    }

    public MessageEvent createMessageEvent(Object obj, Message message) {
        return MessageEventImpl.getInstance(obj, message);
    }
}

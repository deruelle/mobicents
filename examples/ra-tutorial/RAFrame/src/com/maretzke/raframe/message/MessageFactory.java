/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: MessageFactory.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 4th August 2005, 12:31
 * Version: 1.0
 */
package com.maretzke.raframe.message;

/**
 * The MessageFactory interface defines the methods supported by the Factory.
 * Currently, the MessageFactory is able to create:<br>
 * Message objects<br>
 * MessageEvent objects<br> 
 *
 * @author Michael Maretzke
 */
public interface MessageFactory {    
    public Message createMessage(String id, String command);
    public MessageEvent createMessageEvent(Object obj, Message message);
}

/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: MessageParser.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 9th August 2005, 17:11
 * Version: 1.0
 */
package com.maretzke.raframe.message;

/**
 * The MessageParser interface needs to be implemented by concrete Message parser
 * implementations. 
 *
 * @author Michael Maretzke
 */
public interface MessageParser {
    public Message parse(String message) throws IncorrectRequestFormatException;
}

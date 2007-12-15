/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: IncorrectRequestFormatException.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 3rd August 2005, 11:21
 * Version: 1.0
 */
package com.maretzke.raframe.message;

/**
 * IncorrectRequestFormatException is thrown by 
 * com.maretzke.raframe.message.MessageParser if the information to parse does
 * not follow the defined rules of a message.<br>
 *
 * @author Michael Maretzke
 */
public class IncorrectRequestFormatException extends Exception {
    
    public IncorrectRequestFormatException() {
    }
    
    public IncorrectRequestFormatException(String reason) {
        super(reason);
    }     
}

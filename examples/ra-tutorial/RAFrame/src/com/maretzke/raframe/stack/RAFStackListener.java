/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: RAFStackListener.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 3rd August 2005, 11:04
 * Version: 1.0
 */
package com.maretzke.raframe.stack;

/**
 * RAFStackListener is an interface which needs to be implemented to receive
 * information from the RAFStack implementation. It follows the 
 * publish/subscribe pattern. 
 *
 * @author Michael Maretzke
 */
public interface RAFStackListener {
    public void onEvent(String incomingData);
}

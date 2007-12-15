/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: RAFrameActivityContextInterfaceFactory.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 2nd August 2005, 15:52
 * Version: 1.0
 */
package com.maretzke.raframe.ratype;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;

/**
 * The RAFrameActivityContextInterfaceFactory interface defines the 
 * activity context interface factory as described in Section 7.6.1 of the
 * JAIN SLEE Specification, Final Release Page 91. The Sbb uses this object
 * to access the activity context of a specific resource adaptors activity 
 * object. 
 * <br>
 * The RAFrameAcitvityContextInterfaceFactory is referenced in the deployment
 * descriptor file "resource-adaptor-type-jar.xml" in the tag 
 * <activity-context-interface-factory-interface>, sub-tag 
 * <activity-context-interface-factory-interface-name>:
 * com.maretzke.raframe.RAFrameActivityContextInterfaceFactory
 * <br>
 * A Sbb references this object through a link in the deployment descriptor
 * file "sbb-jar.xml". The tag <resource-adaptor-type-binding> contains a 
 * sub-tag <activity-context-interface-factory-name> where a JNDI name for 
 * this object is defined: "slee/resources/RAFrameRA/raframeacif".
 * <br>
 * The class com.maretzke.raframe.ra.RAFrameActivityContextInterfaceFactoryImpl
 * implements this interface.
 * For further information, please refer to JAIN SLEE Specification 1.0, Final 
 * Release Page 91 and 239 and following pages.
 *
 * @author Michael Maretzke
 */
public interface RAFrameActivityContextInterfaceFactory {
    public ActivityContextInterface getActivityContextInterface(String callID)
        throws NullPointerException, UnrecognizedActivityException, FactoryException;
}
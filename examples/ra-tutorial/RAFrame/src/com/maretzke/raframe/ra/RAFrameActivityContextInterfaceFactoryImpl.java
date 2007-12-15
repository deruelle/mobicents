/*
 * Project: RAFrameRA Resource Adaptor Framework - An example Resource Adaptor
 *          for Mobicents - the open source JAIN SLEE implementation.
 *          See www.mobicents.org for more detailed information on Mobicents.
 *
 * File: RAFrameActivityContextInterfaceFactoryImpl.java
 * Author: Michael Maretzke
 * License: Distributable under LGPL license - see terms of license at gnu.org
 * Created: 3rd August 2005, 10:17
 * Version: 1.0
 */
package com.maretzke.raframe.ra;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;
import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;
import com.maretzke.raframe.ratype.RAFrameActivityContextInterfaceFactory;

/**
 * RAFrameActivityContextInterfaceFactoryImpl implements the interface
 * RAFrameActivityContextInterfaceFactory. 
 * <br>
 * For further information on the interface or the implementing class, please 
 * refer to the documentation of RAFrameActivityContextInterfaceFactory.
 * <br>
 * For further information, please refer to JAIN SLEE Specification 1.0, Final 
 * Release Page 91 and 239 and following pages.
 *
 * @see com.maretzke.raframe.ratype.RAFrameActivityContextInterfaceFactory# com.maretzke.raframe.ratype.RAFrameActivityContextInterfaceFactory
 * @author Michael Maretzke
 */
public class RAFrameActivityContextInterfaceFactoryImpl implements RAFrameActivityContextInterfaceFactory, ResourceAdaptorActivityContextInterfaceFactory {
    private static Logger logger = Logger.getLogger(RAFrameActivityContextInterfaceFactoryImpl.class);    
    // reference to the SLEE for further usage
    private SleeContainer serviceContainer; 
    // the JNDI name of the ActivityContextInterfaceFactory object
    private final String jndiName;
    
    public RAFrameActivityContextInterfaceFactoryImpl(SleeContainer serviceContainer, String name) {
        this.serviceContainer = serviceContainer;
        this.jndiName = "java:slee/resources/" + name + "/raframeacif";        
        logger.debug("RAFrameActivityContextInterfaceFactoryImpl.jndiName = " + jndiName);
    }
    
    // implements RAFrameActivityContextInterfaceFactory
    public ActivityContextInterface getActivityContextInterface(String id) throws NullPointerException, UnrecognizedActivityException, FactoryException {
        logger.debug("RAFrameActivityContextInterfaceFactoryImpl.getActivityContextInterface(" + id + ") called.");
        if (id == null)
            throw new NullPointerException("RaFrameActivityContextInterfaceFactoryImpl.getActivityContextInterface(): id is null.");
        return new ActivityContextInterfaceImpl(this.serviceContainer, id); 
    }
    
    // implements ResourceAdaptorActivityContextInterfaceFactory
    public String getJndiName() {
        return jndiName;
    }    
}
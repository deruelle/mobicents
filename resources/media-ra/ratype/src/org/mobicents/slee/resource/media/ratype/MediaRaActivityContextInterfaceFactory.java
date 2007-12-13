/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ratype;

import javax.slee.ActivityContextInterface;
import javax.slee.FactoryException;
import javax.slee.UnrecognizedActivityException;

import org.mobicents.slee.resource.media.ratype.MediaSession;

/**
 * Implements ActivityContextInterfaceFactory interface.
 *
 * @author Victor Hugo
 * @author Oleg Kulikov
 */


public interface MediaRaActivityContextInterfaceFactory {

    public ActivityContextInterface getActivityContextInterface(MediaSession mediaSession)
    	throws NullPointerException, UnrecognizedActivityException, FactoryException; 
    public ActivityContextInterface getActivityContextInterface(MediaConnection connection)
    	throws NullPointerException, UnrecognizedActivityException, FactoryException; 
    public ActivityContextInterface getActivityContextInterface(MediaContext context)
    	throws NullPointerException, UnrecognizedActivityException, FactoryException; 
}
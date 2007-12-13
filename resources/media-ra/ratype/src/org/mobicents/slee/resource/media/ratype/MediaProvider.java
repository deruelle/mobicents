/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ratype;

import org.mobicents.slee.resource.media.ratype.MediaSession;

/**
 * Give us the chance of creating a new Session.
 * 
 * @author Victor Hugo
 * @author Oleg Kulikov
 */
public interface MediaProvider {
	public MediaConnection createConnection(int topology);
        
	/**
	 * Create a new Media Session with its unique Session ID.
	 * @return Media Session
	 */
	public MediaSession getNewMediaSession();
        
}		
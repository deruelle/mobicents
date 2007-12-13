/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform       *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 ***************************************************/
package org.mobicents.slee.resource.media.ra;

import org.mobicents.slee.resource.media.ratype.MediaSession;

public interface EventListener {
	
	/**
	 * Used to fire a Media Event (Dtmf, EndMediaStream 
	 * or Session Result event).
	 * @param event
	 * @param activity The activity will be the Media Session
	 */
	public void onEvent(Object event, MediaSession activity);
}
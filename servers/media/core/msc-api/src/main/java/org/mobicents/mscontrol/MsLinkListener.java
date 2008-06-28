/*
 * MsLinkListener.java
 *
 * The Simple Media Server Control API
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.mscontrol;

import java.io.Serializable;

/**
 * The class interested in receiving the MsLinkEvent should implement this
 * interface. When ever there is change in state of MsLink, instance of
 * MsLinkEvent is fired.
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsLinkListener extends Serializable {

	/**
	 * This method is called when MsLinkEventID.LINK_CREATED is fired. MsLink is
	 * created by calling MsSession.createLink(MsLinkMode mode)
	 * 
	 * @param evt
	 */
	public void linkCreated(MsLinkEvent evt);

	/**
	 * This method is called when MsLinkEventID.LINK_JOINED is fired. Fired when
	 * MsLink.join(String a, String b) is called and joining of two Endpoint is
	 * successful
	 * 
	 * @param evt
	 */
	public void linkJoined(MsLinkEvent evt);

	/**
	 * This method is called when MsLinkEventID.LINK_DROPPED is fired. Fired
	 * when MsLink.release() is called
	 * 
	 * @param evt
	 */
	public void linkDropped(MsLinkEvent evt);

	/**
	 * This method is called when MsLinkEventID.LINK_FAILED is fired. Fired when
	 * MsLink.join(String a, String b) is called and joining of two Endpoint
	 * fails
	 * 
	 * @param evt
	 */
	public void linkFailed(MsLinkEvent evt);
}

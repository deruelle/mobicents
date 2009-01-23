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
 * The class interested in receiving the {@link MsLinkEvent} should implement
 * this interface. When ever there is change in state of {@link MsLink},
 * instance of {@link MsLinkEvent} is fired.
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsLinkListener extends Serializable {

	/**
	 * This method is called when <code>MsLinkEventID.LINK_CREATED</code> is
	 * fired. {@link MsLink} is created by calling
	 * 
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * MsSession msSession;
	 * ...	 
	 * msSession.createLink(mode);
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * @param evt
	 */
	public void linkCreated(MsLinkEvent evt);

	/**
	 * This method is called when <code>MsLinkEventID.LINK_CONNECTED</code> is
	 * fired. Fired when
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * MsLink msLink;
	 * ...	 
	 * msLink.join("media/trunk/PacketRelay/enp-2", "media/trunk/Announcement/$");
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * is called and joining of two Endpoint is successful
	 * 
	 * @param evt
	 */
	public void linkConnected(MsLinkEvent evt);

	/**
	 * This method is called when <code>MsLinkEventID.LINK_DISCONNECTED</code> is fired. Fired
	 * when
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * MsLink msLink;
	 * ...	 
	 * msLink.release();
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * is called
	 * 
	 * @param evt
	 */
	public void linkDisconnected(MsLinkEvent evt);

	/**
	 * This method is called when <code>MsLinkEventID.LINK_FAILED</code> is fired. Fired when
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * MsLink msLink;
	 * ...	 
	 * msLink.join("media/trunk/IVR/$", "media/trunk/Conference/enp-1");
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * is called and joining of two Endpoint fails
	 * 
	 * @param evt
	 */
	public void linkFailed(MsLinkEvent evt);
        
        public void modeHalfDuplex(MsLinkEvent evt);
        public void modeFullDuplex(MsLinkEvent evt);
}

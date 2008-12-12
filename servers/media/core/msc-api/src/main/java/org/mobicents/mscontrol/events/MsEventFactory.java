/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */

package org.mobicents.mscontrol.events;

import java.io.Serializable;

/**
 * <p>
 * Application may ask to be notified about certain events occurring in an
 * endpoint (e.g., DTMF) by including the name of the event in a
 * <code>org.mobicents.mscontrol.events.MsEventIdentifier</code> parameter and calling
 * <code>createRequestedEvent</code><br/> 
 * </p>
 * <p>
 * A Call Agent may also request certain signals to be applied to an endpoint
 * (e.g., Play Announcement) by supplying the name of the event in a
 * <code>MsEventIdentifier</code> parameter and calling createRequestedSignal
 * <br/>
 * </p>
 * 
 * 
 * @author Oleg Kulikov
 */
public interface MsEventFactory extends Serializable {

	/**
	 * Returns instance of {@link org.mobicents.mscontrol.events.MsRequestedEvent} to be passed to
	 * {@link org.mobicents.mscontrol.MsEndpoint#execute}
	 * 
	 * @param eventID
	 * @return instance of <code>MsRequestedEvent</code>
	 */
	public MsRequestedEvent createRequestedEvent(MsEventIdentifier eventID);

	/**
	 * Returns instance of {@link org.mobicents.mscontrol.events.MsRequestedSignal} to be passed to
	 * {@link org.mobicents.mscontrol.MsEndpoint#execute}
	 * 
	 * @param eventID
	 * @return instance of {@link org.mobicents.mscontrol.events.MsRequestedSignal}
	 */
	public MsRequestedSignal createRequestedSignal(MsEventIdentifier eventID);
}

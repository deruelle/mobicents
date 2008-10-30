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

package org.mobicents.mscontrol;

import java.io.Serializable;

import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;

/**
 * <p>
 * Media Server contain endpoints on which the Call-Agent/Application can
 * create, modify and delete connections in order to establish and control media
 * sessions with other multimedia endpoints. <code>MsEndpoint</code>
 * represents the Endpoint in media server. Once the <code>MsConnection</code>
 * is created, application can get reference to <code>MsEndpoint</code> by
 * calling <code>MsConnection.getEndpoint()</code>.
 * </p>
 * <p>
 * Endpoints in media server is broadly divided in two categories 1)
 * <code>VirtualEndpoint</code> for example Announcement Endpoint, IVR
 * Endpoint, Packet Relay Endpoint etc and 2) PhysicalEndpoint like Fax line,
 * DS0, TDM etc
 * </p>
 * Reference of <code>MsEndpoint</code> is useful to detect the events like
 * DTMF or apply signal like Play Announcement on <code>MsConnection</code> or
 * <code>MsLink</code>
 * 
 * @author Oleg Kulikov
 * @author amit.bhayani
 */
public interface MsEndpoint extends Serializable {

	/**
	 * Returns the fully qualified Endpoint Name
	 * 
	 * @return The endpoint name specific to this <code>MsEndpoint</code>.
	 *         For example '<code>media/trunk/IVR/enp-3</code>'
	 */
	public String getLocalName();

	/**
	 * Call this method to Generate event or request Signal on Endpoint
	 * directly. Mainly used for Physical Endpoint.
	 * 
	 * @param signals
	 *            array of MsRequestedSignal to be applied on Endpoint
	 * @param events
	 *            array of MsRequestedEvent to be detected on Endpoint
	 */
	public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events);

	/**
	 * Call this method to Generate event or request Signal on MsConnection in
	 * Endpoint.
	 * 
	 * @param signals
	 *            array of MsRequestedSignal to be applied on Endpoint
	 * @param events
	 *            array of MsRequestedEvent to be detected on Endpoint
	 * @param connection
	 *            MsConnection on which events/signals need to be applied
	 */
	public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events, MsConnection connection);

	/**
	 * Call this method to Generate event or request Signal on MsLink in Endpint
	 * 
	 * 
	 * @param signals
	 *            array of MsRequestedSignal to be applied on Endpoint
	 * @param events
	 *            array of MsRequestedEvent to be detected on Endpoint
	 * @param link
	 *            {@link MsLink} on which events/signals need to be applied
	 */
	public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events, MsLink link);

	/**
	 * Register MsNotificationListener to listen for <code>MsNotifyEvent</code>
	 * which are fired due to events detected by Endpoints like DTMF. Use above
	 * execute methods to register for event, passing appropriate
	 * <code>MsRequestedEvent</code>
	 * 
	 * @param listener
	 */
	public void addNotificationListener(MsNotificationListener listener);

	/**
	 * Remove the NotificationListener
	 * 
	 * @param listener
	 */
	public void removeNotificationListener(MsNotificationListener listener);

	/**
	 * Register MsConnectionListener to listen for changes in MsConnection state
	 * handled by this Endpoint
	 * 
	 * @param listener
	 */
	public void addConnectionListener(MsConnectionListener listener);

	/**
	 * Removes the MsConnectionListener
	 * 
	 * @param listener
	 */
	public void removeConnectionListener(MsConnectionListener listener);
	
	/**
	 * This method gives the array of Supported Packages for given Endpoint.
	 * 
	 * @return String[]
	 */
	public String[] getSupportedPackages();	
	
	
}

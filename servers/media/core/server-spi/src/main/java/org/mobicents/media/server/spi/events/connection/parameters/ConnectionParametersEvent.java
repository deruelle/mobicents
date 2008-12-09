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

package org.mobicents.media.server.spi.events.connection.parameters;

import org.mobicents.media.server.spi.events.EventIdentifier;
import org.mobicents.media.server.spi.events.pkg.ConnectionParameters;
import org.mobicents.media.server.spi.events.pkg.DTMF;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author Oleg Kulikov
 */
public class ConnectionParametersEvent implements NotifyEvent {

	protected int octetsSent, octetsReceived, packetsReceived, packetsSent,
			packetsLost, jitter;

	public ConnectionParametersEvent() {

	}

	public ConnectionParametersEvent(int octetsSent, int octetsReceived,
			int packetsReceived, int packetsSent, int packetsLost, int jitter) {
		super();
		this.octetsSent = octetsSent;
		this.octetsReceived = octetsReceived;
		this.packetsReceived = packetsReceived;
		this.packetsSent = packetsSent;
		this.packetsLost = packetsLost;
		this.jitter = jitter;
	}

	public EventIdentifier getEventID() {
		return ConnectionParameters.ConnectionsParameters;
	}

	public int getJitter() {
		return jitter;
	}

	public int getOctetsReceived() {
		return octetsReceived;
	}

	public int getOctetsSent() {
		return octetsSent;
	}

	public int getPacketsLost() {
		return packetsLost;
	}

	public int getPacketsReceived() {
		return packetsReceived;
	}

	public int getPacketsSent() {
		return packetsSent;
	}

	public void setJitter(int jitter) {
		this.jitter = jitter;
	}

	public void setOctetsReceived(int octetsReceived) {
		this.octetsReceived = octetsReceived;
	}

	public void setOctetsSent(int octetsSent) {
		this.octetsSent = octetsSent;
	}

	public void setPacketsLost(int packetsLost) {
		this.packetsLost = packetsLost;
	}

	public void setPacketsReceived(int packetsReceived) {
		this.packetsReceived = packetsReceived;
	}

	public void setPacketsSent(int packetsSent) {
		this.packetsSent = packetsSent;
	}

}

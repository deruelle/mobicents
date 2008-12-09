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

package org.mobicents.mscontrol.impl.events.connection.parameters;

import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.connection.parameters.MsConnectionParametersNotifyEvent;
import org.mobicents.mscontrol.events.pkg.ConnectionParameters;

/**
 *
 * @author Oleg Kulikov
 */
public class ConnectionParametersNotifyEventImpl implements MsConnectionParametersNotifyEvent {

	protected int octetsSent, octetsReceived, packetsReceived, packetsSent,
	packetsLost, jitter;

	
	protected  Object source;
    
    public ConnectionParametersNotifyEventImpl(Object source) {
        this.source = source;
       
    }
	
    public ConnectionParametersNotifyEventImpl(int octetsSent,
			int octetsReceived, int packetsReceived, int packetsSent,
			int packetsLost, int jitter, Object source) {
		super();
		this.octetsSent = octetsSent;
		this.octetsReceived = octetsReceived;
		this.packetsReceived = packetsReceived;
		this.packetsSent = packetsSent;
		this.packetsLost = packetsLost;
		this.jitter = jitter;
		this.source = source;
	}
    
	public int getOctetsSent() {
		return octetsSent;
	}

	public int getOctetsReceived() {
		return octetsReceived;
	}

	public int getPacketsReceived() {
		return packetsReceived;
	}

	public int getPacketsSent() {
		return packetsSent;
	}

	public int getPacketsLost() {
		return packetsLost;
	}

	public int getJitter() {
		return jitter;
	}

	public MsEventIdentifier getEventID() {
		return ConnectionParameters.ConnectionParameters;
	}

	public Object getSource() {
		return source;
	}

}

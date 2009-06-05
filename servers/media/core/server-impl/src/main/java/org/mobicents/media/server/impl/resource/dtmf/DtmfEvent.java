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
package org.mobicents.media.server.impl.resource.dtmf;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * 
 * @author kulikov
 */
public class DtmfEvent implements NotifyEvent {
	public final static int DTMF_0 = 0;
	public final static int DTMF_1 = 1;
	public final static int DTMF_2 = 2;
	public final static int DTMF_3 = 3;
	public final static int DTMF_4 = 4;
	public final static int DTMF_5 = 5;
	public final static int DTMF_6 = 6;
	public final static int DTMF_7 = 7;
	public final static int DTMF_8 = 8;
	public final static int DTMF_9 = 9;
	public final static int DTMF_A = 10;
	public final static int DTMF_B = 11;
	public final static int DTMF_C = 12;
	public final static int DTMF_D = 13;
	public final static int DTMF_HASH = 14;
	public final static int DTMF_STAR = 15;



	private Endpoint endpoint;
	private Connection connection;
	private String resourceName;
	private int eventID;

	public DtmfEvent(Endpoint endpoint, Connection connection, String resourceName, int eventID) {
		this.eventID = eventID;
                this.resourceName = resourceName;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public Connection getConnection() {
		return connection;
	}


	public int getEventID() {
		return eventID;
	}

    public String getResourceName() {
        return resourceName;
    }

}

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

package org.mobicents.mscontrol.impl;

import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.impl.events.BaseRequestedEvent;
import org.mobicents.mscontrol.impl.events.BaseRequestedSignal;

/**
 *
 * @author Oleg Kulikov
 */
public class MsEndpointImpl implements MsEndpoint {
    protected Endpoint server;
    
    protected MsEndpointImpl(Endpoint endpoint) {
        this.server = endpoint;
    }
    
    public String getLocalName() {
        return server.getLocalName();
    }
    
    protected Endpoint getEndpoint() {
        return server;
    }

    public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events) {
        MsProviderImpl.submit(new Tx(signals, events, null, null));
    }
    
    public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events, MsConnection connection) {
        MsProviderImpl.submit(new Tx(signals, events, connection.getId(), null));
    }
    
    public void execute(MsRequestedSignal[] signals, MsRequestedEvent[] events, MsLink link) {
        String connectionID = ((MsLinkImpl) link).getConnectionID(getLocalName());
        MsProviderImpl.submit(new Tx(signals, events, connectionID, (MsLinkImpl)link));
    }
    
    private class Tx implements Runnable {
        private MsRequestedSignal[] signals;
        private MsRequestedEvent[] events;
        private String connectionID;
        private NotificationListener listener;
        
        protected Tx(MsRequestedSignal[] signals, MsRequestedEvent[] events, String connectionID, NotificationListener listener) {
            this.events = events;
            this.signals = signals;
            this.connectionID = connectionID;
            this.listener = listener;
        }
        
        public void run() {
            RequestedSignal[] s = new RequestedSignal[signals.length];
            for (int i = 0; i < signals.length; i++) {
                s[i] = ((BaseRequestedSignal)signals[i]).convert();
            }
            
            RequestedEvent[] evt = new RequestedEvent[events.length];
            for (int i = 0; i < events.length; i++) {
                evt[i] = ((BaseRequestedEvent) events[i]).convert();
                evt[i].setHandler(listener);
            }
            server.execute(s, evt, connectionID);            
        }
    }
}

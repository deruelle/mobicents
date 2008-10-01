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

package org.mobicents.mscontrol.impl.events;

import org.mobicents.media.server.spi.events.EventFactory;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsRequestedEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class DefaultRequestedEvent extends BaseRequestedEvent implements MsRequestedEvent {

    private String id;
    private MsEventAction action;
    
    public String getID() {
        return id;
    }
    
    public void setID(String id) {
        this.id = id;
    }
    
    @Override
    public RequestedEvent convert() {
        EventFactory factory = new EventFactory();
        RequestedEvent evt;
        try {
            evt = factory.createRequestedEvent(id); 
            return evt;
        } catch (ClassNotFoundException ex) {
           return null;
        }
    }


    public MsEventAction getAction() {
        return action;
    }

    public void setEventAction(MsEventAction action) {
        this.action = action;
    }

}

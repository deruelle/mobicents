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
package org.mobicents.media.server.impl;

import org.mobicents.media.Component;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 * Implementation for standard events.
 * 
 * @author kulikov
 */
public class NotifyEventImpl implements NotifyEvent {
    
    private BaseComponent component;
    private int eventID;
    
    public NotifyEventImpl(BaseComponent component, int eventID) {
        this.component = component;
        this.eventID = eventID;
    }
    
    public int getEventID() {
        return eventID;
    }

    public Component getSource() {
        return component;
    }

}

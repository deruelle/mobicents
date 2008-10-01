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

package org.mobicents.media.server.spi.events.announcement;

import org.mobicents.media.server.spi.events.AbstractRequestedEvent;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnRequestedEvent extends AbstractRequestedEvent {

    private String eventID;
    private String url;
    
    public AnnRequestedEvent(String eventID) {
        this.eventID = eventID;
    }
    
    public String getID() {
        return eventID;
    }

    public boolean matches(NotifyEvent event) {
        return event.getEventID().equals(eventID);
    }
    
    /**
     * Gets the url of the announcement.
     * 
     * @return the url string.
     */
    public String getURL() {
        return url;
    }
    
    /**
     * Modify the url which points to the announcement.
     * 
     * @param url the string url.
     */
    public void setURL(String url) {
        this.url = url;
    }

}

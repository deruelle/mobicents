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

package org.mobicents.media.server.spi.events.dtmf;

import org.mobicents.media.server.spi.events.AbstractRequestedEvent;
import org.mobicents.media.server.spi.events.EventIdentifier;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.pkg.DTMF;

/**
 *
 * @author Oleg Kulikov
 */
public class DtmfRequestedEvent extends AbstractRequestedEvent {
    
    private String mask;
    
    
    public EventIdentifier getID() {
        return DTMF.DTMF;
    }

    public boolean matches(NotifyEvent event) {
        return event.getEventID().equals(DTMF.DTMF);
    }
    
    /**
     * Gets the digit mask.
     * 
     * @return regular expression string.
     */
    public String getDigitMask() {
        return mask;
    }
    
    /**
     * Apply digit mask
     * 
     * @param digitMask the regular exprression which describes the dtmf sequence
     */
    public void setDigitMask(String mask) {
        this.mask = mask;
    }

    
}

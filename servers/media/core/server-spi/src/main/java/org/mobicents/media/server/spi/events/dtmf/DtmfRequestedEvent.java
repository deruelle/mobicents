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
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class DtmfRequestedEvent extends AbstractRequestedEvent {
    
    private String eventID;
    private String mask;
    
    public DtmfRequestedEvent(String eventID) {
        this.eventID = eventID;
    }
    
    public String getID() {
        return eventID;
    }

    public boolean matches(NotifyEvent event) {
        if (!(event instanceof DtmfEvent)) {
            return false;
        }
        DtmfEvent dtmfEvent = (DtmfEvent) event;
        /*switch (eventID) {
            case DTMF_0    : return dtmfEvent.getSequence().matches("0");
            case DTMF_1    : return dtmfEvent.getSequence().matches("1");
            case DTMF_2    : return dtmfEvent.getSequence().matches("2");
            case DTMF_3    : return dtmfEvent.getSequence().matches("3");
            case DTMF_4    : return dtmfEvent.getSequence().matches("4");
            case DTMF_5    : return dtmfEvent.getSequence().matches("5");
            case DTMF_6    : return dtmfEvent.getSequence().matches("6");
            case DTMF_7    : return dtmfEvent.getSequence().matches("7");
            case DTMF_8    : return dtmfEvent.getSequence().matches("8");
            case DTMF_9    : return dtmfEvent.getSequence().matches("9");
            case DTMF_STAR : return dtmfEvent.getSequence().matches("*");
            case DTMF_HASH : return dtmfEvent.getSequence().matches("#");
            case DTMF_SEQ  : {
                //TODO collect digit and check seq
                return false;
            }
        }
         */ 
        return false;
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

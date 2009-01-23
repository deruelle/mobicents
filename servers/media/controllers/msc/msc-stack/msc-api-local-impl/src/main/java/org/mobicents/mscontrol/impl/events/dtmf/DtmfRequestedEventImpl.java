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
package org.mobicents.mscontrol.impl.events.dtmf;

import org.mobicents.media.server.spi.events.EventFactory;
import org.mobicents.media.server.spi.events.RequestedEvent;
import org.mobicents.media.server.spi.events.dtmf.DtmfRequestedEvent;
import org.mobicents.mscontrol.events.MsEventAction;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.dtmf.MsDtmfRequestedEvent;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.impl.events.BaseRequestedEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class DtmfRequestedEventImpl extends BaseRequestedEvent implements MsDtmfRequestedEvent {

    private final static String EVENT_ID = "org.mobicents.media.events.dtmf.DTMF";
    private String pattern;
    private MsEventAction action;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public MsEventIdentifier getID() {
        return DTMF.TONE;
    }

    public MsEventAction getAction() {
        return action;
    }

    public void setEventAction(MsEventAction action) {
        this.action = action;
    }

    @Override
    public RequestedEvent convert() {
        EventFactory factory = new EventFactory();
        try {
            DtmfRequestedEvent evt = (DtmfRequestedEvent) factory.createRequestedEvent(
                    DTMF.TONE.getPackageName(), DTMF.TONE.getEventName());
            evt.setDigitMask(pattern);
            return evt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

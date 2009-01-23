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
package org.mobicents.mscontrol.impl.events.announcement;

import org.mobicents.media.server.spi.events.EventFactory;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.media.server.spi.events.announcement.PlayRequestedSignal;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.ann.MsPlayRequestedSignal;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.mscontrol.impl.events.BaseRequestedSignal;

/**
 *
 * @author Oleg Kulikov
 */
public class PlayRequestedSignalImpl extends BaseRequestedSignal implements MsPlayRequestedSignal {

    private String url;

    public MsEventIdentifier getID() {
        return MsAnnouncement.PLAY;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    @Override
    public RequestedSignal convert() {
        EventFactory factory = new EventFactory();
        PlayRequestedSignal signal = (PlayRequestedSignal) factory.createRequestedSignal(
                MsAnnouncement.PLAY.getPackageName(), MsAnnouncement.PLAY.getEventName());
        signal.setURL(url);
        return signal;
    }
}

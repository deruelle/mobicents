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

package org.mobicents.media.server.spi.events;

import java.io.Serializable;
import java.util.HashMap;
import org.mobicents.media.server.spi.events.announcement.AnnouncementPkgFactory;
import org.mobicents.media.server.spi.events.dtmf.DtmfPkgFactory;
import org.mobicents.media.server.spi.events.pkg.Announcement;
import org.mobicents.media.server.spi.events.pkg.DTMF;

/**
 *
 * @author Oleg Kulikov
 */
public class EventFactory implements Serializable {
    
    private HashMap<String,PkgFactory> pkgFactories = new HashMap();
    
    public EventFactory() {
        pkgFactories.put(Announcement.PACKAGE_NAME, new AnnouncementPkgFactory());
        pkgFactories.put(DTMF.PACKAGE_NAME, new DtmfPkgFactory());
    }
    
    public RequestedSignal createRequestedSignal(String packageName, String signalName) {
        PkgFactory factory = pkgFactories.get(packageName);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown package: " + packageName);
        }
        RequestedSignal s = factory.createRequestedSignal(signalName);
        if (s == null) {
            throw new IllegalArgumentException("Unknown signal name: " + signalName);
        }
        return s;
    }

    public RequestedEvent createRequestedEvent(String packageName, String eventName) {
        PkgFactory factory = pkgFactories.get(packageName);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown package: " + packageName);
        }
        RequestedEvent s = factory.createRequestedEvent(eventName);
        if (s == null) {
            throw new IllegalArgumentException("Unknown signal name: " + eventName);
        }
        return s;
    }
    
}

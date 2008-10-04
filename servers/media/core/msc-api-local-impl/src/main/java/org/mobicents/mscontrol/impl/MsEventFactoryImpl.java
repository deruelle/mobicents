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

import java.util.HashMap;
import org.mobicents.mscontrol.events.MsEventFactory;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.MsRequestedEvent;
import org.mobicents.mscontrol.events.MsRequestedSignal;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.mscontrol.impl.events.MsPackage;
import org.mobicents.mscontrol.impl.events.announcement.MsAnnouncementPackage;
import org.mobicents.mscontrol.impl.events.dtmf.DtmfPackage;

/**
 *
 * @author Oleg Kulikov
 */
public class MsEventFactoryImpl implements MsEventFactory {
    
    private static HashMap<String, MsPackage> packages = new HashMap();
    static {
        packages.put(MsAnnouncement.PACKAGE_NAME, new MsAnnouncementPackage());
        packages.put(DTMF.PACKAGE_NAME, new DtmfPackage());
    }
    
    public MsRequestedSignal createRequestedSignal(MsEventIdentifier signalID) {
        MsPackage pkg = packages.get(signalID.getPackageName());
        if (pkg != null) {
            return pkg.createRequestedSignal(signalID);
        }
        return null;
    }
    
    public MsRequestedEvent createRequestedEvent(MsEventIdentifier eventID) {
        MsPackage pkg = packages.get(eventID.getPackageName());
        if (pkg != null) {
            return pkg.createRequestedEvent(eventID);
        }
        return null;        
    }
    
}

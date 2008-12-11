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
import org.mobicents.mscontrol.events.pkg.ConnectionParameters;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.mscontrol.events.pkg.MsAudio;
import org.mobicents.mscontrol.events.pkg.MsLine;
import org.mobicents.mscontrol.impl.events.MsPackage;
import org.mobicents.mscontrol.impl.events.announcement.MsAnnouncementPackage;
import org.mobicents.mscontrol.impl.events.audio.MsAudioPackage;
import org.mobicents.mscontrol.impl.events.connection.parameters.ConnectionParametersPackage;
import org.mobicents.mscontrol.impl.events.dtmf.DtmfPackage;
import org.mobicents.mscontrol.impl.events.line.MsLinePackage;

/**
 *
 * @author Oleg Kulikov
 */
public class MsEventFactoryImpl implements MsEventFactory {
    
	//Let us create the HashMap with exact bucket size of 5 as default is 16
    private static HashMap<String, MsPackage> packages = new HashMap<String, MsPackage>(5, 1);
    static {
        packages.put(MsAnnouncement.PACKAGE_NAME, new MsAnnouncementPackage());
        packages.put(DTMF.PACKAGE_NAME, new DtmfPackage());
        packages.put(MsAudio.PACKAGE_NAME, new MsAudioPackage());
        packages.put(ConnectionParameters.PACKAGE_NAME, new ConnectionParametersPackage());
        packages.put(MsLine.PACKAGE_NAME, new MsLinePackage());
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

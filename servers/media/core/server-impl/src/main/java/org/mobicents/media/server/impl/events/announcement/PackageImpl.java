/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.announcement;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.events.EventPackage;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.media.server.spi.events.announcement.PlayRequestedSignal;


/**
 *
 * @author Oleg Kulikov
 */
public class PackageImpl implements EventPackage {
    
    public PackageImpl() {
    }

    public AbstractSignal getSignal(RequestedSignal requestedSignal) {
        if (requestedSignal.getID().endsWith("PLAY")) {            
            return new AnnSignal(((PlayRequestedSignal)requestedSignal).getURL());
        }
        return null;
    }

}

    
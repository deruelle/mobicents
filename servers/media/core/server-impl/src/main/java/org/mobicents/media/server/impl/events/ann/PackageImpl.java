/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.ann;

import org.mobicents.media.server.spi.events.EventDetector;
import org.mobicents.media.server.spi.events.EventPackage;
import org.mobicents.media.server.spi.events.Options;
import org.mobicents.media.server.spi.events.Signal;


/**
 *
 * @author Oleg Kulikov
 */
public class PackageImpl implements EventPackage {
    
    public PackageImpl() {
    }

    public Signal getSignal(String signalName, Options options) {
        if (signalName.equals("PLAY")) {
            return new AnnSignal(options);
        }
        return null;
    }


    public EventDetector getDetector(String signalID, Options options) {
        return null;
    }
}

    
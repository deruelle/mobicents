/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.events.au;


import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.spi.events.EventDetector;
import org.mobicents.media.server.spi.events.EventPackage;
import org.mobicents.media.server.spi.events.Options;
import org.mobicents.media.server.spi.events.Signal;

/**
 * 
 * @author Oleg Kulikov
 */
public class PackageImpl implements EventPackage {

    private BaseEndpoint endpoint;

    public PackageImpl() {
    }

    public Signal getSignal(String signalID, Options options) {
        return new PlayRecordSignal(options);
    }

    public EventDetector getDetector(String signalID, Options options) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.events.au;


import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.events.EventPackage;
import org.mobicents.media.server.spi.events.RequestedSignal;

/**
 * 
 * @author Oleg Kulikov
 */
public class PackageImpl implements EventPackage {

    private BaseEndpoint endpoint;

    public PackageImpl() {
    }


    public AbstractSignal getSignal(RequestedSignal requestedSignal) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

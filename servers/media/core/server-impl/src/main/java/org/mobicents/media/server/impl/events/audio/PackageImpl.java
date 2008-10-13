/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.events.audio;


import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.events.EventPackage;
import org.mobicents.media.server.spi.events.RequestedSignal;
import org.mobicents.media.server.spi.events.audio.RecordRequestedSignal;
import org.mobicents.media.server.spi.events.pkg.Audio;

/**
 * 
 * @author Oleg Kulikov
 */
public class PackageImpl implements EventPackage {

    private BaseEndpoint endpoint;

    public PackageImpl() {
    }


    public AbstractSignal getSignal(RequestedSignal requestedSignal) {
        if (requestedSignal.getID().equals(Audio.RECORD)) {            
            return new RecorderSignal(((RecordRequestedSignal)requestedSignal).getFile());
        }
        return null;
    }

}

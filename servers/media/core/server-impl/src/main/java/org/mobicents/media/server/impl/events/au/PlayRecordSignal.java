/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.au;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.spi.events.RequestedSignal;

/**
 *
 * @author Oleg Kulikov
 */
public class PlayRecordSignal extends AbstractSignal {

    private Recorder recorder;
    
    public PlayRecordSignal(RequestedSignal signal) {
        super();
        recorder = new Recorder("wav");
    }
    

    @Override
    public void apply(BaseConnection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void apply(BaseEndpoint connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

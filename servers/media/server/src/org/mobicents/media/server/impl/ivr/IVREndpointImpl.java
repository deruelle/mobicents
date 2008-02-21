/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.server.impl.ivr;

import javax.media.format.AudioFormat;
import javax.media.protocol.FileTypeDescriptor;
import org.mobicents.media.server.impl.ann.AnnEndpointImpl;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.BaseResourceManager;
import org.mobicents.media.server.impl.Signal;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.UnknownSignalException;
import org.mobicents.media.server.spi.events.AU;

/**
 *
 * @author Oleg Kulikov
 */
public class IVREndpointImpl extends AnnEndpointImpl {

    
    protected AudioFormat audioFormat = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1);
    protected String mediaType = FileTypeDescriptor.WAVE;
    private Signal signal;
    
    private transient Logger logger = Logger.getLogger(IVREndpointImpl.class);
    
    /** Creates a new instance of IVREndpointImpl */
    public IVREndpointImpl(String localName) {
        super(localName);
    }
    
    
    @Override
    public BaseResourceManager initResourceManager() {
        return new IVRResourceManager();
    }
    
    /**
     * (Non Java-doc).
     *
     * @see org.mobicents.server.spi.BaseEndpoint#play(int, String NotificationListener, boolean.
     */
    @Override
    public void play(int signalID, String[] params, String connectionID,
            NotificationListener listener, boolean keepAlive) throws UnknownSignalException {

        //disable current signal
        if (signal != null) {
            signal.stop();
        }

        if (params == null) {
            return;
        }
        
        switch (signalID) {
            case AU.PLAY_RECORD :
                signal = new PlayRecordSignal(this, listener, params);
                signal.start();
            default:
                super.play(signalID, params, connectionID, listener, keepAlive);
        }
    }
    
}

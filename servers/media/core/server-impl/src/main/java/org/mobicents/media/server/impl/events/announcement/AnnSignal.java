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
package org.mobicents.media.server.impl.events.announcement;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;

/**
 * 
 * @author Oleg Kulikov
 */
public class AnnSignal extends AbstractSignal {

    private String file;
    private AudioPlayer player;
    
    public AnnSignal(String file) {
        this.file = file;
    }

    public String getID() {
        return "PLAY";
    }

    @Override
    public void apply(BaseConnection connection) {
        player = (AudioPlayer) getMediaSource(MediaResource.AUDIO_PLAYER, connection);
        player.setFile(file);
        player.start();
    }

    @Override
    public void apply(BaseEndpoint endpoint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cancel() {        
        player.stop();
    }
}

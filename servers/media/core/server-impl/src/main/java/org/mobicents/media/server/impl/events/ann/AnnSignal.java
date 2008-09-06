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

package org.mobicents.media.server.impl.events.ann;

import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.dsp.Processor;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.Options;
import org.mobicents.media.server.spi.events.announcement.AnnParams;

/**
 * 
 * @author Oleg Kulikov
 */
public class AnnSignal extends AbstractSignal implements PlayerListener {

    private AudioPlayer player;
    private Processor dsp;
    
    public AnnSignal(Options options) {
        player = new AudioPlayer();
        player.addListener(this);
        dsp = new Processor();
        this.options = options;
    }
    
    public String getID() {
        return "PLAY";
    }

    public void connect(MediaSink sink) {
        //dsp.getOutput().connect(sink);
        //dsp.getInput().connect(player);
        player.connect(sink);
    }

	public String getID() {
		return "PLAY";
	}

	public void connect(MediaSink sink) throws IOException {
		player.connect(sink);
	}

	public void disconnect(MediaSink sink) {
		player.disconnect(sink);
	}

	public void start() {
		player.setFile((String) options.get(AnnParams.URL));
		player.start();
	}

    public void update(PlayerEvent event) {
        NotifyEvent evt = null;
        switch (event.getEventType()) {
            case STARTED :
                //evt = new NotifyEvent(this, "org.mobicents.media.ann.STARTED");
                break;
            case END_OF_MEDIA :
            case STOP_BY_REQUEST :
                evt = new NotifyEvent(this, "org.mobicents.media.ann.COMPLETE");
                break;
            case FACILITY_ERROR :
                evt = new NotifyEvent(this, "org.mobicents.media.ann.FACILITY_ERROR");
                break;
        }
        if (evt != null) {
            sendEvent(evt);
        }
    }

	public Format getFormat() {
		return null;
	}

	public void update(PlayerEvent event) {
		System.out.println("**** PLAYER " + event.getEventType());
		NotifyEvent evt = null;
		switch (event.getEventType()) {
		case STARTED:
			// evt = new NotifyEvent(this, "org.mobicents.media.ann.STARTED");
			break;
		case END_OF_MEDIA:
		case STOP_BY_REQUEST:
			evt = new NotifyEvent(this, "org.mobicents.media.ann.COMPLETE");
			break;
		case FACILITY_ERROR:
			evt = new NotifyEvent(this, "org.mobicents.media.ann.FAIL");
			break;
		}
		if (event != null && evt != null) {
			sendEvent(evt);
		}
	}

	public Format[] getFormats() {
		return AudioPlayer.formats;
	}

}

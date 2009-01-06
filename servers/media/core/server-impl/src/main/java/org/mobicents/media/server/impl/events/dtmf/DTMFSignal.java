package org.mobicents.media.server.impl.events.dtmf;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;

public class DTMFSignal extends AbstractSignal {

    private String tone = null;

    public DTMFSignal(String tone) {
        this.tone = tone;
    }

    @Override
    public void apply(BaseConnection connection) {
        DtmfGenerator gen = (DtmfGenerator) getMediaSource(MediaResource.DTMF_GENERATOR, connection);
        gen.start();
        try {
            Thread.currentThread().sleep(50);
        } catch (Exception e) {
        }
        gen.stop();
    }

    @Override
    public void apply(BaseEndpoint connection) {
    }

    @Override
    public void cancel() {
    }
}

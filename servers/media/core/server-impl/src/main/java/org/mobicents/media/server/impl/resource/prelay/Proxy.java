/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.resource.prelay;

import java.util.concurrent.ScheduledFuture;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.rtp.BufferFactory;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author kulikov
 */
public class Proxy {

    private final static Format[] DEFAULT_FORMATS = new Format[]{
        AVProfile.PCMA, AVProfile.PCMU, AVProfile.SPEEX,
        AVProfile.GSM, AVProfile.G729, AVProfile.DTMF, Codec.LINEAR_AUDIO
    };
    private Input input;
    private Output output;
    private String connectionID;
    private boolean isSinkConnected;
    private boolean isSourceConnected;
    private Timer timer;
    private BufferFactory bufferFactory = new BufferFactory(2, "");
    private long seq;
    private long timestamp;
    private long time;
    private int silencePeriod;

    private SilenceDetector silenceDetector;
    private ScheduledFuture worker;
    private ScheduledFuture silenceWorker;
    
    public Proxy(String name, Endpoint endpoint) {
        input = new Input(name + ".input");
        output = new Output(name + ".output");
        this.timer = endpoint.getTimer();
        this.silencePeriod = timer.getHeartBeat() * 5;
        silenceDetector = new SilenceDetector();
    }

    public MediaSink getInput() {
        return input;
    }

    public MediaSource getOutput() {
        return output;
    }

    public String getConnectionID() {
        return connectionID;
    }

    public void setConnectionID(String connectionID) {
        this.connectionID = connectionID;
    }

    public boolean isIsSinkConnected() {
        return isSinkConnected;
    }

    public boolean isIsSourceConnected() {
        return isSourceConnected;
    }

    public boolean isConnectedTo(MediaSink sink) {
        return output.isConnectedTo(sink);
    }

    public boolean isConnectedTo(MediaSource source) {
        return input.isConnectedTo(source);
    }

    private void startSilenceGen() {
        if (silenceWorker == null) {
            silenceWorker = timer.synchronize(output);
        }
    }

    private void stopSilenceGen() {
        if (silenceWorker != null) {
            silenceWorker.cancel(false);
            silenceWorker = null;
        }
    }
    
    private void reset() {
        time = 0;
        seq = 0;
    }
    private class Input extends AbstractSink {

        public Input(String name) {
            super(name);
        }

        @Override
        public void connect(MediaSource source) {
            super.connect(source);
            isSourceConnected = true;
            if (isSinkConnected) {
                worker = timer.synchronize(silenceDetector);
            }
        }

        @Override
        public void disconnect(MediaSource source) {
            super.disconnect(source);
            if (!output.isConnected()) {
                connectionID = null;
            }
            isSourceConnected = false;
            if (!isSinkConnected && worker != null) {
                worker.cancel(false);
                stopSilenceGen();
                reset();
            }
        }

        public boolean isConnectedTo(MediaSource source) {
            return otherParty == source;
        }

        public Format[] getOtherPartyFormats() {
            return otherParty.getFormats();
        }

        public Format[] getFormats() {
            Format[] formats = output.isConnected() ? output.getOtherPartyFormats() : null;
            return formats != null && formats.length > 0 ? formats : DEFAULT_FORMATS;
        }

        public boolean isAcceptable(Format format) {
            return output.isConnected() ? output.isAcceptable(format) : true;
        }

        public void receive(Buffer buffer) {
            time = System.currentTimeMillis();
            
            seq = buffer.getSequenceNumber();
            timestamp = buffer.getTimeStamp();
            
            stopSilenceGen();
            
            if (output.isConnected()) {
                output.delivery(buffer);
            }
        }
    }

    private class Output extends AbstractSource implements Runnable {

        
        public Output(String name) {
            super(name);
        }

        public void start() {
        }

        public void stop() {
        }

        public void run() {
                Buffer buffer = bufferFactory.allocate();
                buffer.setLength(320);
                buffer.setFormat(Codec.LINEAR_AUDIO);
                buffer.setSequenceNumber(seq++);
                buffer.setSequenceNumber(timestamp += timer.getHeartBeat());
                buffer.setDuration(20);

                delivery(buffer);
        }

        @Override
        public void connect(MediaSink sink) {
            super.connect(sink);
            this.start();
            isSinkConnected = true;
            if (isSourceConnected) {
                worker = timer.synchronize(silenceDetector);
            }
        }

        @Override
        public void disconnect(MediaSink sink) {
            this.stop();
            super.disconnect(sink);
            if (!input.isConnected()) {
                connectionID = null;
            }
            isSinkConnected = false;
            if (isSourceConnected && worker != null) {
                worker.cancel(false);
                stopSilenceGen();
                reset();
            }
        }

        public boolean isConnectedTo(MediaSink sink) {
            return otherParty == sink;
        }

        public Format[] getOtherPartyFormats() {
            return otherParty.getFormats();
        }

        public boolean isAcceptable(Format fmt) {
            return otherParty.isAcceptable(fmt);
        }

        public Format[] getFormats() {
            Format[] formats = input.isConnected() ? input.getOtherPartyFormats() : null;
            return formats != null && formats.length > 0 ? formats : DEFAULT_FORMATS;
        }

        public void delivery(Buffer buffer) {
            if (otherParty != null && otherParty.isAcceptable(buffer.getFormat())) {
                otherParty.receive(buffer);
            }
        }
    }
    
    private class SilenceDetector implements Runnable {

        public void run() {
            long now = System.currentTimeMillis();
            if ((now - time) > silencePeriod) {
                startSilenceGen();
            }
        }
        
    }
}

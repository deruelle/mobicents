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

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import org.mobicents.media.Buffer;
import org.mobicents.media.BufferFactory;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 * This is an implementation of the Packet Relay proxy.
 * 
 * @author kulikov
 */
public class PacketRelayProxy {

    public final static Format NOISE_FORMAT = Codec.LINEAR_AUDIO;
    
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
    private BufferFactory bufferFactory = new BufferFactory(2);
    private long seq;
    private long timestamp;
    
    private long time;
    private int silencePeriod;
    
    private SilenceDetector silenceDetector;
    private NoiseGenerator noiseGenerator;
    
    private ScheduledFuture worker;
    private ScheduledFuture silenceWorker;
    
    private Buffer buff;
    
    /**
     * Creates new instance of Packet Relay proxy.
     * 
     * @param name the name of this proxy.
     * @param endpoint the endpoint executed this proxy.
     */
    public PacketRelayProxy(String name, Endpoint endpoint) {
        input = new Input(name + ".input");
        output = new Output(name + ".output");
        timer = endpoint.getTimer();
        silencePeriod = timer.getHeartBeat() * 5;
        silenceDetector = new SilenceDetector();
        noiseGenerator = new NoiseGenerator();
    }

    /**
     * Gets an input of this proxy.
     * 
     * @return media sink object which acts as input for this proxy.
     */
    public MediaSink getInput() {
        return input;
    }

    /**
     * Gets an output of this proxy.
     * 
     * @return media source object which acts as output for this proxy.
     */
    public MediaSource getOutput() {
        return output;
    }

    /**
     * Gets the associated connection.
     * 
     * @return the identifier of the connection.
     */
    public String getConnectionID() {
        return connectionID;
    }

    /**
     * Makes association between Connection and this proxy.
     * 
     * @param connectionID the identifier of associated connection.
     */
    public void setConnectionID(String connectionID) {
        this.connectionID = connectionID;
    }

    /**
     * Indicates if a media sink is connected to this proxy.
     * 
     * @return tue if some media sink is connected to this proxy.
     */
    public boolean isIsSinkConnected() {
        return isSinkConnected;
    }

    /**
     * Indicates if a media source is connected to this proxy.
     * 
     * @return true if some media source is connected to this proxy.
     */
    public boolean isIsSourceConnected() {
        return isSourceConnected;
    }

    /**
     * Indicates if the specified media sink is connected to this proxy.
     * 
     * @return true if specified media sink is connected to this proxy.
     */
    public boolean isConnectedTo(MediaSink sink) {
        return output.isConnectedTo(sink);
    }

    /**
     * Indicates if the specified media source is connected to this proxy.
     * 
     * @return true if specified media source is connected to this proxy.
     */
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

    /** 
     * Resets the paramets of stream.
     */
    private void reset() {
        time = 0;
        seq = 0;
        timestamp = 0;
        
        silenceDetector.stop();
        noiseGenerator.stop();
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
                silenceDetector.start();
            }
        }

        @Override
        public void disconnect(MediaSource source) {
            super.disconnect(source);
            if (!output.isConnected()) {
                connectionID = null;
            }
            
            isSourceConnected = false;
            
            if (!isSinkConnected) {
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

        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
            time = System.currentTimeMillis();

            seq++;
            timestamp += timer.getHeartBeat();

            //if packet arrived stop silence detector
            noiseGenerator.stop();
            
            if (output.isConnected()) {
                //update time stamp and sequence number
                buffer.setTimeStamp(timestamp);
                buffer.setSequenceNumber(seq);
                
                buff = buffer;
                output.run();
            }
        }
    }

    private class Output extends AbstractSource implements Runnable {

        public Output(String name) {
            super(name);
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void connect(MediaSink sink) {
            super.connect(sink);
            this.start();
            
            isSinkConnected = true;
            
            if (isSourceConnected) {
                silenceDetector.start();
            }
        }

        @Override
        public void disconnect(MediaSink sink) {
            super.disconnect(sink);
            if (!input.isConnected()) {
                connectionID = null;
            }
            
            isSinkConnected = false;
            
            if (isSourceConnected) {
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
                try {
                    otherParty.receive(buffer);
                } catch (IOException e) {
                }
            }
        }

        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
            buffer.copy(buff);
        }
    }

    private class SilenceDetector implements Runnable {

        private ScheduledFuture worker;
        
        /**
         * Starts detection
         */
        public void start() {
            if (worker == null) {
                worker = timer.synchronize(this);
            }
        }
        
        /**
         * Terminates detection
         */
        public void stop() {
            if (worker != null) {
                worker.cancel(false);
            }
        }
        
        public void run() {
            //if time elased from last arrived from "other side" packet is
            //exceed silence period we are staring a confortable noise generator
            long now = System.currentTimeMillis();
            if ((now - time) > silencePeriod) {
                noiseGenerator.start();
            }
        }
    }

    /**
     * Generates confortable noise.
     * 
     * We use this sound generator as default source.
     */
    private class NoiseGenerator implements Runnable {
        
        private ScheduledFuture worker;
        
        /**
         * Starts sound transmission
         */
        public void start() {
            if (worker == null) {
                worker = timer.synchronize(this);
            }
        }
        
        /**
         * Terminates sound transmission
         */
        public void stop() {
            if (worker != null) {
                worker.cancel(false);
            }
        }
        
        public void run() {
            Buffer buffer = bufferFactory.allocate();
            
            //clean data
            byte[] data = (byte[]) buffer.getData();
            for (int i = 0; i < 320; i++) {
                data[i] = 0;
            }
                        
            buffer.setLength(320);
            buffer.setFormat(Codec.LINEAR_AUDIO);
            buffer.setDuration(20);
            
            //sends data
            output.delivery(buffer);
        }
    }
}

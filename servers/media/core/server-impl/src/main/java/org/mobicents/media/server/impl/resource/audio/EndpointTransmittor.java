/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.audio;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;
import org.mobicents.media.server.spi.dsp.Codec;

/**
 *
 * @author kulikov
 */
public class EndpointTransmittor extends BaseComponent implements Inlet, Outlet {

    private final static long SILENCE = 80;
    private Format format = Codec.LINEAR_AUDIO;
    
    private Input input;
    private Output output;
    
    private volatile LinkedBlockingQueue<Buffer> queue = new LinkedBlockingQueue();
    
    private boolean isAcceptable = false;
    private Format fmt;
    
    private volatile long silence;
    private boolean isTransmittSilence;
    
    private Logger logger = Logger.getLogger(EndpointTransmittor.class);
    
    public EndpointTransmittor(String name, Timer timer) {
        super(name);
        input = new Input(name);
        output = new Output(name, timer);
    }
    
    public MediaSink getInput() {
        return input;
    }

    public void start() {
        input.start();
        output.start();
    }

    public void stop() {
        input.stop();
        output.stop();
    }


    @Override
    public void setEndpoint(Endpoint endpoint) {
        input.setEndpoint(endpoint);
        output.setEndpoint(endpoint);
    }
    
    @Override
    public void setConnection(Connection connection) {        
        input.setConnection(connection);
        output.setConnection(connection);
    }
    
    public MediaSource getOutput() {
        return output;
    }

    private Format[] combine(Format[] fmts, Format f) {
        for (int i = 0; i < fmts.length; i++) {
            if (fmts[i].matches(f)) {
                return fmts;
            }
        }
        Format[] formats = new Format[fmts.length + 1];
        System.arraycopy(fmts, 0, formats, 0, fmts.length);
        formats[fmts.length] = f;
        return formats;
    }
    
    private class Input extends AbstractSink {

        public Input(String name) {
            super(name);
        }
        
        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
            Buffer buff = new Buffer();
            buff.copy(buffer);
            queue.offer(buff);
        }

        public Format[] getFormats() {
            return output.getOtherPartyFormats();
        }

        public boolean isAcceptable(Format format) {
//            if (fmt != null && fmt.matches(format)) {
//                return isAcceptable;
//            }
            Format[] supported = getFormats();
            for (int i = 0; i < supported.length; i++) {
                if (format.matches(supported[i])) {
                    fmt = format;
                    isAcceptable = true;
                    return true;
                }
            }
            
            fmt = format;
            return false;
        }
        
        private Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }
    }
    
    private class Output extends AbstractSource {

        private boolean isSilence = false;
        
        public Output(String name, Timer timer) {
            super(name);
            setSyncSource(timer);
        }

        private Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }
        
        @Override
        public void evolve(Buffer buffer, long sequenceNumber) {
            if (!queue.isEmpty()) {
                if (logger.isDebugEnabled() && silence != 0) {
                    logger.debug(this  + " start audio data");
                    this.isSilence = false;
                }
                Buffer buff = queue.poll(); 
                buffer.copy(buff);
                buffer.setSequenceNumber(sequenceNumber);
                buffer.setDiscard(false);
                silence = 0;
            } else if (silence > SILENCE) {
                if (logger.isDebugEnabled() && !this.isSilence) {
                    logger.debug(this  + " start audio silence");
                    this.isSilence = true;
                }
                buffer.setOffset(0);
                buffer.setLength(320);
                buffer.setFormat(format);
                buffer.setSequenceNumber(sequenceNumber);
                buffer.setTimeStamp(System.currentTimeMillis());
                buffer.setDiscard(false);
            } else {
                silence += getSyncSource().getHeartBeat();
                buffer.setDiscard(true);
            }
        }

        public Format[] getFormats() {
            return combine(input.getOtherPartyFormats(), format);
        }

    }

    public void connect(MediaSource source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void disconnect(MediaSource source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void connect(MediaSink sink) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void disconnect(MediaSink sink) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

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

package org.mobicents.media.server.impl.resource.zap;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.impl.FailureEventImpl;
import org.mobicents.media.server.spi.ResourceGroup;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class BChannel extends BaseComponent implements ResourceGroup {

    private AbstractSink sink;
    private AbstractSource source;
    
    private RandomAccessFile file;
    private Format format;
    
    public BChannel(String name) {
        super(name);
        source = new BChannelSource(name);
        sink = new BChannelSink(name);
    }
    
    public Format getFormat() {
        return format;
    }
    
    public void setFormat(Format format) {
        this.format = format;
    }
    
    public void start() {
        try {
            file = new RandomAccessFile(getName(), "rw");
            source.start();
            sink.start();
        } catch (Exception e) {
            failed(NotifyEvent.START_FAILED, e);
        }
    }

    public void stop() {
        source.stop();
        sink.stop();
        if (file != null) {
            try {
                file.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Sends failure notification.
     * 
     * @param eventID
     *            failure event identifier.
     * @param e
     *            the exception caused failure.
     */
    protected void failed(int eventID, Exception e) {
        FailureEventImpl failed = new FailureEventImpl(this, eventID, e);
        sendEvent(failed);
    }
    
    public MediaSink getSink() {
        return sink;
    }

    public MediaSource getSource() {
        return source;
    }

    private class BChannelSource extends AbstractSource {

        public BChannelSource(String name) {
            super(name);
        }
        
        @Override
        public void evolve(Buffer buffer, long timestamp, long sequenceNumber) {
            byte[] data = (byte[]) buffer.getData();
            try {
                int len = file.read(data);
                buffer.setOffset(0);
                buffer.setLength(len);
                buffer.setSequenceNumber(sequenceNumber);
                buffer.setTimeStamp(timestamp);
                buffer.setFormat(format);
            } catch (IOException e) {
                buffer.setFlags(Buffer.FLAG_DISCARD);
            }
        }

        public Format[] getFormats() {
            return new Format[]{format};
        }
        
    }
    
    private class BChannelSink extends AbstractSink {

        public BChannelSink(String name) {
            super(name);
        }
        
        @Override
        public void onMediaTransfer(Buffer buffer) throws IOException {
            byte[] data = (byte[]) buffer.getData();
            file.write(data, buffer.getOffset(), buffer.getLength());
        }

        public Format[] getFormats() {
            return new Format[]{format};
        }

        public boolean isAcceptable(Format fmt) {
            return format.equals(fmt);
        }
        
    }
}

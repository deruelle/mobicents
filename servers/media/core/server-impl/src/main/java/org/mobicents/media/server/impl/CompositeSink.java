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
package org.mobicents.media.server.impl;

import java.io.IOException;
import org.mobicents.media.Buffer;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.resource.Channel;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.resource.UnknownComponentException;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 *
 * @author kulikov
 */
public class CompositeSink extends BaseComponent implements MediaSink {

    private MediaSink sink;
    private Channel channel;
    
    private ChannelFactory channelFactory;
    private ComponentFactory sinkFactory;
    
    private boolean started = false;
    private boolean isConnected = false;
    
    private Format fmt;
    private boolean isAcceptable;
    
    public CompositeSink(String name) {
        super(name);
    }
    
    public ComponentFactory getSinkFactory() {
        return sinkFactory;
    }
    
    public void setSinkFactory(ComponentFactory sinkFactory) throws ResourceUnavailableException {
        this.sinkFactory = sinkFactory;
        sink = (MediaSink)sinkFactory.newInstance(getEndpoint());
        if (channel != null) {
            channel.connect(sink);
        }
    }
    
    public ChannelFactory getChannelFactory() {
        return channelFactory;
    }

    public void setChannelFactory(ChannelFactory channelFactory) throws ResourceUnavailableException {
        this.channelFactory = channelFactory;
        channel = channelFactory.newInstance(getEndpoint());
        if (sink != null) {
            channel.connect(sink);
        }
    }
    
    public void start() {
        sink.start();
        channel.start();
        started = true;
    }

    public void stop() {
        sink.stop();
        channel.stop();
        started = false;
    }

    public Format[] getFormats() {
        return channel.getOutputFormats();
    }

    public boolean isAcceptable(Format format) {
        if (fmt != null && fmt.matches(format)) {
            return isAcceptable;
        }
        
        Format[] formats = getFormats();
        for (int i = 0; i < formats.length; i++) {
            if (formats[i].matches(format)) {
                fmt = format;
                isAcceptable = true;
                return true;
            }
        }
        
        return false;
    }

    public void connect(MediaSource source) {
        channel.connect(source);
        this.isConnected = true;
    }

    public void disconnect(MediaSource source) {
        channel.disconnect(source);
        this.isConnected = false;
    }

    public void receive(Buffer buffer) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isStarted() {
        return started;
    }

    public long getPacketsReceived() {
        return sink.getPacketsReceived();
    }

    public long getBytesReceived() {
        return sink.getBytesReceived();
    }

    public void connect(Outlet outlet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void disconnect(Outlet outlet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isMultipleConnectionsAllowed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

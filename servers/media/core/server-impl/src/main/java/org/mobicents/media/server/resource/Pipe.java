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

package org.mobicents.media.server.resource;

import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;

/**
 * Pipe is part of a channel and joins individual components.
 * 
 * @author kulikov
 */
public class Pipe {
    
    private MediaSink sink;
    private MediaSource source;
    
    private Channel channel;

    /**
     * Coinstructs new pipe.
     * 
     * @param channel the owner of this pipe;
     */
    protected Pipe(Channel channel) {
        this.channel = channel;
    }
    
    /**
     * Joins source with sink
     * 
     * @param inletName the name of source component.
     * @param outletName the name of sink component.
     * @throws UnknownComponentException if inletName or outletName is unknown
     */
    public void open(String inletName, String outletName) throws UnknownComponentException {
        if (!channel.sources.containsKey(inletName)) {
            throw new UnknownComponentException(inletName);
        }
        
        source = channel.sources.get(inletName);
        if (!channel.sinks.containsKey(outletName)) {
            source = null;
            throw new UnknownComponentException(outletName);
        }
        
        sink = channel.sinks.get(outletName);
        sink.connect(source);
    }
    
    /**
     * Closes this pipe if it was open and do nothing if pipe was not open.
     */
    public void close() {
        if (sink != null && source != null) {
            sink.disconnect(source);
            sink = null;
            source = null;
        }
    }
}

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

import java.util.Collection;
import java.util.HashMap;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;

/**
 *
 * @author kulikov
 */
public abstract class AbstractSinkSet extends AbstractSink implements MediaSink {

    private HashMap<String, AbstractSink> sinks = new HashMap();
    
    public AbstractSinkSet(String name) {
        super(name);
    }
    
    public Collection<AbstractSink> getStreams() {
        return sinks.values();
    }
    
    @Override
    public boolean isMultipleConnectionsAllowed() {
        return true;
    }
    
    @Override
    public void connect(MediaSource source) {
        if (!source.isMultipleConnectionsAllowed()) {
            AbstractSink sink = createSink(source);
            sink.setEndpoint(getEndpoint());
            sink.setConnection(getConnection());
            sink.connect(source);
        
            sink.start();
            sinks.put(source.getId(), sink);
        } else {
            throw new IllegalArgumentException(source + 
                    " allows muliple connection and this component also");
        }
    }
    
    @Override
    public void disconnect(MediaSource source) {
        AbstractSink sink = sinks.remove(source.getId());
        if (sink == null) {
            throw new IllegalArgumentException(source + " is not connected to " + this);
        }
        sink.stop();
        sink.disconnect(source);
        destroySink(sink);
        sink.setEndpoint(null);
        sink.setConnection(null);
    }

    
    public int getActiveSinkCount() {
        return sinks.size();
    }

    public abstract AbstractSink createSink(MediaSource otherParty);
    public abstract void destroySink(AbstractSink sink);
}

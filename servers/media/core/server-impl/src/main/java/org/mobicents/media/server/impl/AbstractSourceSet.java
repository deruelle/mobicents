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

/**
 *
 * @author kulikov
 */
public abstract class AbstractSourceSet extends AbstractSource {

    private HashMap<String, AbstractSource> sources = new HashMap();
    
    public AbstractSourceSet(String name) {
        super(name);
    }
    
    public Collection<AbstractSource> getStreams() {
        return sources.values();
    }

    @Override
    public boolean isMultipleConnectionsAllowed() {
        return true;
    }
    
    @Override
    public void connect(MediaSink sink) {
        if (sink == null) {
            throw new IllegalArgumentException("Other party can not be nul");
        }
        AbstractSource source = createSource(sink);
        source.connect(sink);
        
        source.start();
        sources.put(sink.getId(), source);
        
    }

    @Override
    public void disconnect(MediaSink otherParty) {
        AbstractSource source = sources.remove(otherParty.getId());
        if (source == null) {
            throw new IllegalArgumentException(otherParty + " is not connected to " + this);
        }
        source.stop();
        source.disconnect(otherParty);
    }

    public abstract AbstractSource createSource(MediaSink otherParty);
    public abstract void destroySource(AbstractSource source);
    
    public int getActiveSourceCount() {
        return sources.size();
    }
}

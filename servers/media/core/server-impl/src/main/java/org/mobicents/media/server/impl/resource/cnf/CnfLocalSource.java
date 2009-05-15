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
package org.mobicents.media.server.impl.resource.cnf;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.resource.Demultiplexer;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class CnfLocalSource extends AbstractSource {

    private Endpoint endpoint;
    protected ConcurrentHashMap<String, AudioMixer> mixers = new ConcurrentHashMap();
    protected ConferenceBridge bridge;
    
    public CnfLocalSource(Endpoint endpoint, String name) {
        super(name);
        this.endpoint = endpoint;
    }

    public CnfLocalSource(String name, ConferenceBridge bridge) {
        super(name);
        this.bridge = bridge;
        this.endpoint = bridge.endpoint;
    }
    
    public ConcurrentHashMap<String, AudioMixer> getMixers() {
        return this.mixers;
    }
    
    public ConferenceBridge getBridge() {
        return bridge;
    }
    
    @Override
    public void connect(MediaSink sink) {
        System.out.println("Connecting" + sink + " to endpoint source for " + sink.getConnection()) ;
        AudioMixer mixer = new AudioMixer(endpoint, "demux." + getName());
        mixers.put(sink.getId(), mixer);
        mixer.setConnection(sink.getConnection());
        mixer.getOutput().connect(sink);
        mixer.start();
        
        Collection<Demultiplexer> list = bridge.localSink.splitters.values();
        for (Demultiplexer splitter: list) {
            if (!splitter.getConnection().getId().equals(sink.getConnection().getId())) {
                splitter.connect(mixer);
            }
        }
        //super.connect(sink);
        this.otherParty = null;
    }

    @Override
    public void disconnect(MediaSink sink) {
        String id = sink.getConnection().getId();
        
        AudioMixer mixer = mixers.remove(sink.getId());
        if (mixer != null) {
            mixer.stop();
            mixer.getOutput().disconnect(sink);
            Collection<Demultiplexer> list = bridge.localSink.splitters.values();
            for (Demultiplexer splitter: list) {
                if (!splitter.getConnection().getId().equals(sink.getConnection().getId())) {
                    splitter.disconnect(mixer);
                }
            }
        }
        super.disconnect(sink);
    }

    public void start() {
    }

    public void stop() {
    }

    public Format[] getFormats() {
        return ConferenceBridge.FORMATS;
    }
    
    private class LocalSource extends AbstractSource {

        protected AudioMixer mixer;
        
        public LocalSource(String name, Endpoint endpoint) {
            super(name);
            mixer = new AudioMixer(endpoint, name + ".mixer");
        }
        
        public void start() {
            mixer.start();
        }

        public void stop() {
            mixer.stop();
        }

        public Format[] getFormats() {
            return mixer.getFormats();
        }
        
        public void addParty(MediaSource stream) {
            mixer.connect(stream);
        }
        
        public void removeParty(MediaSource stream) {
            mixer.connect(stream);
        }
        
    }
}

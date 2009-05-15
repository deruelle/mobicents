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
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.resource.Demultiplexer;

/**
 *
 * @author kulikov
 */
public class CnfLocalSink extends AbstractSink {

    protected ConcurrentHashMap<String, Demultiplexer> splitters = new ConcurrentHashMap();
    private ConferenceBridge bridge;
    
    public CnfLocalSink(String name) {
        super(name);
    }
    
    public CnfLocalSink(String name, ConferenceBridge bridge) {        
        super(name);
        this.bridge = bridge;
    }

    public ConferenceBridge getBridge() {
        return bridge;
    }
    
    @Override
    public void connect(MediaSource source) {
        System.out.println("Connection" + source + " to endpoint sink for " + source.getConnection()) ;
        Demultiplexer demux = new Demultiplexer("demux." + source.getName());
        splitters.put(source.getId(), demux);
        demux.setConnection(source.getConnection());
        demux.getInput().connect(source);
        demux.start();
        
        Collection <AudioMixer> list = bridge.localSource.mixers.values();
        for (AudioMixer mixer: list) {
            if (!mixer.getConnection().getId().equals(source.getConnection().getId())) {
                mixer.connect(demux);
            }
        }
                
        //super.connect(source);
        this.otherParty = null;
    }

    @Override
    public void disconnect(MediaSource source) {
        Demultiplexer demux = splitters.remove(source.getId());
        if (demux != null) {
            demux.stop();
            demux.getInput().disconnect(source);
            
            Collection <AudioMixer> list = bridge.localSource.mixers.values();
            for (AudioMixer mixer: list) {
                if (!mixer.getConnection().getId().equals(source.getConnection().getId())) {
                    mixer.disconnect(demux);
                }
            }
        }        
        super.disconnect(source);
    }

    public Format[] getFormats() {
        return ConferenceBridge.FORMATS;
    }

    public boolean isAcceptable(Format format) {
        return format.matches(ConferenceBridge.FORMATS[0]);
    }

    public void receive(Buffer buffer) {
        System.out.println("Receive  " + buffer);
    }
}

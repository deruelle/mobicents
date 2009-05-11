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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.mobicents.media.Component;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;

/**
 * Channel is used to join media source with media sink with 
 * customized media path.
 * 
 * For customization of media path inner pipes are used where each pipe has c
 * onfigured its inlet and outlet.
 * 
 * @author kulikov
 */
public class Channel {

    //holders for components classified by its type
    //one component can be holded in more then one map
    protected HashMap<String, MediaSource> sources;
    protected HashMap<String, MediaSink> sinks;
    protected HashMap<String, Inlet> inlets;
    protected HashMap<String, Outlet> outlets;
    
    //The list of internal pipes
    private List<Pipe> pipes = new ArrayList();
    
    //The external sink to which this channel is attached
    private MediaSink sink;
    //The external source to which this channel is attached
    private MediaSource source;
    
    //The component connected to extenal source.
    private MediaSink intake;
    
    //The component connected to extenal sink.
    private MediaSource exhaust;

    private Endpoint endpoint;
    private Connection connection;
    
    private boolean directLink = true;
    /**
     * Constructs new channel with specified components.
     * 
     * @param sources the map of components of type MediaSource
     * @param sinks the map of components of type MediaSink
     * @param inlets the map of components of type Inlet
     * @param outlets the map of components of type Outlet
     */
    protected Channel(
            HashMap<String, MediaSource> sources,
            HashMap<String, MediaSink> sinks,
            HashMap<String, Inlet> inlets,
            HashMap<String, Outlet> outlets) {
        this.sources = sources;
        this.sinks = sinks;
        this.inlets = inlets;
        this.outlets = outlets;
    }
    
    public Format[] getFormats() {
        return null;
    }
    
    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void setEndpoint(Endpoint endpoint) {
        
    }
    
    public void setConnection(Connection connection) {
        this.connection = connection;
        Collection<MediaSource> list = sources.values();        
        for (MediaSource s : list) {
            s.setConnection(connection);
        }
        Collection<MediaSink> list1 = sinks.values();        
        for (MediaSink s : list1) {
            s.setConnection(connection);
        }
        
        Collection<Inlet> list2 = inlets.values();        
        for (Inlet s : list2) {
            s.setConnection(connection);
        }

        Collection<Outlet> list3 = outlets.values();        
        for (Outlet s : list3) {
            s.setConnection(connection);
        }
    }
    
    public void start() {
//        if (source != null) {
//            source.start();
//        }
        
        Collection <MediaSource> list = sources.values();
        for (MediaSource s : list) {
            s.start();
        }
    }
    
    public void stop() {
        if (source != null) {
            source.stop();
        }
        
        Collection <MediaSource> list = sources.values();
        for (MediaSource s : list) {
            s.stop();
        }
    }
    /**
     * Opens pipes between source and sink.
     * 
     * @param pipe the pipe to be opened
     * @param inlet the name of the source
     * @param outlet the name of the destination.     * 
     * @throws org.mobicents.media.server.resource.UnknownComponentException
     * if name of the sink or source is not known.
     */
    public void openPipe(Pipe pipe, String inlet, String outlet) throws UnknownComponentException {
        directLink = false;
        //when inlet is null pipe acts as intake for the channel
        //so component with name outlet will be connected to external source
        //we have to consider it as intake.
        if (inlet == null && outlet != null) {
            if (sinks.containsKey(outlet)) {
                intake = sinks.get(outlet);
            }  else throw new UnknownComponentException(outlet);
        } 
        //when outlet is null then pipe acts as exhaust for the channel
        //the component with name inlet will be connected to an external sink
        //we are assigning this component to exhaust varibale
        else if (inlet != null && outlet == null) {
            if (sources.containsKey(inlet)) {
                exhaust = sources.get(inlet);
            } else throw new UnknownComponentException(inlet);
        } 
        //it is an internal pipe. just join to components and save 
        //pipe in the list
        else {
            pipe.open(inlet, outlet);
            pipes.add(pipe);
        }
    }
    
    /**
     * Closes specified pipe.
     * 
     * @param pipe the pipe to be closed.
     */
    public void closePipe(Pipe pipe) {
        pipes.remove(pipe);
        pipe.close();
    }
    
    /**
     * Connects source and sink to each other with out intermediate pipes.
     */
    private Format[] directLink() {
        if (source != null & sink != null) {
            source.connect(sink);
            return getSubset(source.getFormats(), sink.getFormats());
        } else return new Format[0];
    }

    /**
     * Connects channel to a sink.
     * 
     * @param sink the sink to connect to
     */
    public Format[] connect(MediaSink sink) {
        this.sink = sink;
        if (directLink) {
            return directLink();
        } else {
            exhaust.connect(sink);
            return getSubset(exhaust.getFormats(), sink.getFormats());
        }
    }

    /**
     * Disconnects channel from a sink.
     * 
     * @param sink the sink to connect from
     */
    public void disconnect(MediaSink sink) {
        if (pipes.isEmpty()) {
            if (this.source != null) {
                source.disconnect(sink);
            }
        } else {
            exhaust.disconnect(sink);
        }
        this.sink = null;
    }

    /**
     * Connects channel to a source.
     * 
     * @param source the source to connect to
     */
    public Format[] connect(MediaSource source) {
        this.source = source;
        if (directLink) {
            return directLink();
        } else {
            intake.connect(source);
            return getSubset(intake.getFormats(), source.getFormats());
        }
    }

    /**
     * Disconnects channel from a source.
     * 
     * @param source the source to connect from
     */
    public void disconnect(MediaSource source) {
        if (pipes.isEmpty()) {
            if (this.sink != null) {
                sink.disconnect(source);
            }
        } else {
            intake.disconnect(source);
        }
        this.source = null;
    }

    public Component getComponent(String name) {
        if (sources.containsKey(name)) {
            return sources.get(name);
        } else if (sinks.containsKey(name)) {
            return sinks.get(name);
        } else if (inlets.containsKey(name)) {
            return inlets.get(name);
        } else {
            return outlets.get(name);
        }
    }
    
    public Component getComponent(int resourceType) {
        Collection<MediaSource> list = sources.values();        
        for (MediaSource s : list) {
            if (s.getResourceType() == resourceType) {
                return s;
            }
        }
        Collection<MediaSink> list1 = sinks.values();        
        for (MediaSink s : list1) {
            if (s.getResourceType() == resourceType) {
                return s;
            }
        }
        
        Collection<Inlet> list2 = inlets.values();        
        for (Inlet s : list2) {
            if (s.getResourceType() == resourceType) {
                return source;
            }
        }

        Collection<Outlet> list3 = outlets.values();        
        for (Outlet s : list3) {
            if (s.getResourceType() == resourceType) {
                return source;
            }
        }
        
        return null;
    }
    
    public void close() {
        
        for (Pipe pipe : pipes) {
            closePipe(pipe);
        }
        
        pipes.clear();
        sources.clear();
        sinks.clear();
        outlets.clear();
        inlets.clear();
    }
    
    private Format[] getSubset(Format[] f1, Format[] f2) {
        if (f1.length == 0) {
            return f2;
        }
        
        if (f2.length == 0) {
            return f1;
        }
        
        ArrayList<Format> list = new ArrayList();        
        for (int i = 0; i < f1.length; i++) {
            for (int j = 0; j < f2.length; j++) {
                if (f1[i].matches(f2[j])) {
                    list.add(f1[i]);
                }
            }
        }
        Format[] f = new Format[list.size()];
        list.toArray(f);
        
        return f;
    }
}

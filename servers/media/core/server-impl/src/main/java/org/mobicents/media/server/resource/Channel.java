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
import org.mobicents.media.server.impl.resource.Proxy;
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
    private Proxy localPipe = new Proxy("local-pipe");
    
    //The external sink to which this channel is attached
//    private MediaSink sink;
    //The external source to which this channel is attached
//    private MediaSource source;
    
    //The component connected to extenal source.
    private MediaSink intake;
    
    //The component connected to extenal sink.
    private MediaSource exhaust;

    private Endpoint endpoint;
    private Connection connection;
    
    private boolean directLink = true;
    
    private Channel txChannel;
    private Channel rxChannel;
    
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
        
        this.intake = localPipe.getInput();
        this.exhaust = localPipe.getOutput();
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
        
        intake.setConnection(connection);
        exhaust.setConnection(connection);
    }
    
    public void start() {
/*        Collection <MediaSource> list = sources.values();
        for (MediaSource s : list) {
            s.start();
        }
        
        Collection <MediaSink> list2 = sinks.values();
        for (MediaSink s : list2) {
            s.start();
        }
*/        
        Collection <Inlet> list3 = inlets.values();
        for (Inlet s : list3) {
            if (s instanceof Outlet || s instanceof MediaSource) {
                s.getInput().start();
                s.start();
            }
        }
        
        Collection <Outlet> list4 = outlets.values();
        for (Outlet s : list4) {
            if (s instanceof Inlet || s instanceof MediaSink) {
                s.getOutput().start();
                s.start();
            }
        }
        
        intake.start();
        exhaust.start();
    }
    
    public void stop() {
        Collection <MediaSource> list = sources.values();
        for (MediaSource s : list) {
            s.stop();
        }
        
        Collection <MediaSink> list2 = sinks.values();
        for (MediaSink s : list2) {
            s.stop();
        }
        
        Collection <Inlet> list3 = inlets.values();
        for (Inlet s : list3) {
            s.getInput().stop();
        }
        
        Collection <Outlet> list4 = outlets.values();
        for (Outlet s : list4) {
            s.getOutput().stop();
        }
        
        intake.stop();
        exhaust.stop();
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
    
    public Format[] getInputFormats() {
        return intake.getFormats();
    }
    
    public Format[] getOutputFormats() {
        return exhaust.getFormats();
    }
    /**
     * Connects channel to a sink.
     * 
     * @param sink the sink to connect to
     */
    public Format[] connect(MediaSink sink) {
        //sink may be connected if this channel is not connected to other channel
        //as source of media
        if (rxChannel != null) {
            throw new IllegalStateException("Channel is connected as source for other channel");
        }

        exhaust.connect(sink);
        return this.getSubset(exhaust.getFormats(), sink.getFormats());
    }

    /**
     * Disconnects channel from a sink.
     * 
     * @param sink the sink to connect from
     */
    public void disconnect(MediaSink sink) {
        //sink may be connected if this channel is not connected to other channel
        //as source of media
        if (rxChannel != null) {
            throw new IllegalStateException("Channel is connected as source for other channel");
        }
        
        exhaust.disconnect(sink);
    }

    /**
     * Connects channel to a source.
     * 
     * @param source the source to connect to
     */
    public Format[] connect(MediaSource source) {
        //source may be connected if this channel is not connected to other channel
        //as source of media
        if (txChannel != null) {
            throw new IllegalStateException("Channel is connected as sink for other channel");
        }
        
        intake.connect(source);
        return getSubset(intake.getFormats(), source.getFormats());
    }

    /**
     * Disconnects channel from a source.
     * 
     * @param source the source to connect from
     */
    public void disconnect(MediaSource source) {
        //source may be connected if this channel is not connected to other channel
        //as source of media
        if (txChannel != null) {
            throw new IllegalStateException("Channel is connected as sink for other channel");
        }
        intake.disconnect(source);
    }

    /**
     * Establish a connection with other channel
     * 
     * @param other
     */
    public void connect(Channel rxChannel) {
        this.rxChannel = rxChannel;
        rxChannel.txChannel = this;
        
        exhaust.connect(rxChannel.intake);
    }

    /**
     * Deletes connection with other channel
     * 
     * @param other
     */
    public void disconnect(Channel channel) {
        if (rxChannel != null && rxChannel == channel) {
            exhaust.disconnect(rxChannel.intake);
            rxChannel.txChannel = null;
            rxChannel = null;
        } else if (txChannel != null && txChannel == channel) {
            intake.disconnect(txChannel.exhaust);
            txChannel.rxChannel = null;
            txChannel = null;
        } else {
            throw new IllegalArgumentException("Channels " + this + " and " + channel + " was never connected");
        }
    }

    /**
     * Searches the component with specified name that can be explicitly added 
     * to this channel.
     * 
     * @param name the name of the component to find.
     * @return component that was found or null if component with specified name
     * absent.
     */
    public Component getComponent(String name) {
        //search result depends from the order of maps observation!
        //inlet/outlet implicitly registers input/output in source or sink map
        //and as result can be returned implicit sink/source of the complex 
        //component. To prevent such kind of search error let's start to search 
        //from inlets/outlets and look into sink,source only if explicitly added 
        //component was not found yet       
        if (inlets.containsKey(name)) {
            return inlets.get(name);
        } else if (outlets.containsKey(name)){
            return outlets.get(name);
        } else if (sources.containsKey(name)) {
            return sources.get(name);
        } else if (sinks.containsKey(name)) {
            return sinks.get(name);
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
/*        if (f1.length == 0) {
            return f2;
        }
        
        if (f2.length == 0) {
            return f1;
        }
*/        
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
    
    public long getPacketsTransmitted() {
        return exhaust.getPacketsTransmitted();
    }
/*    private class LocalPipe extends BaseComponent implements Inlet, Outlet {
    
        private LocalInput input;
        private LocalOutput output;
        
        
        private class LocalInput extends AbstractSink implements MediaSink {
            
            public LocalInput(String name) {
                super(name);
            }
            
            protected Format[] getOtherPartyFormats() {
                return otherParty != null ? otherParty.getFormats() : new Format[0];
            }
            
            public Format[] getFormats() {
                return output.getOtherPartyFormats();
            }

            public boolean isAcceptable(Format format) {
                return output.isAcceptable(format);
            }

            public void onMediaTransfer(Buffer buffer) {
                output.send(buffer);
            }
            
        }
        
        private class LocalOutput extends AbstractSource implements MediaSource {

            public LocalOutput(String name) {
                super(name);
            }
            
            public void start() {
            }

            public void stop() {
            }

            protected Format[] getOtherPartyFormats() {
                return otherParty != null ? otherParty.getFormats() : new Format[0];
            }
            
            protected boolean isAcceptable(Format f) {
                return otherParty != null && otherParty.isAcceptable(f);
            }
            
            public Format[] getFormats() {
                return input.getOtherPartyFormats();
            }
            
            public void send(Buffer buffer) {
                if (otherParty != null) {
                    this.otherParty.receive(buffer);
                }
            }

            @Override
            public void evolve(Buffer buffer, long sequenceNumber) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }
        
        public LocalPipe(String name) {
            super(name);
                input = new LocalInput("input." + name);
                output = new LocalOutput("output." + name);
        }
        
        public MediaSink getInput() {
            return input;
        }


        public MediaSource getOutput() {
            return output;
        }

        public void start() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void stop() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
*/    
}

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
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;
import org.mobicents.media.server.spi.Endpoint;

/**
 * Factory class for creating channels.
 * 
 * @author kulikov
 */
public class ChannelFactory {
    
    private List<PipeFactory> pipes;
    private List<ComponentFactory> factories;
    
    private ArrayList<Channel> channels = new ArrayList();
    
    private int maxSize = 50;
    private int coreSize = 10;
    
    private volatile boolean started = false;
    private static final Logger logger = Logger.getLogger(ChannelFactory.class);
    
    /**
     * Returns new channel.
     * 
     * if there is unused channels in the cache the existing channels will be 
     * returned and new instance other wise.
     * 
     * @return
     * @throws org.mobicents.media.server.resource.UnknownComponentException
     */
    public Channel newInstance(Endpoint endpoint) throws UnknownComponentException {
        if (!started) {
            throw new IllegalStateException("Factory is not started");
        }
        if (!channels.isEmpty()) {
            return channels.remove(0);
        } else {
            return createNewChannel(endpoint);
        }
    }
   
    /**
     * Constructs new channel instance.
     * 
     * @return channel instance.
     * @throws org.mobicents.media.server.resource.UnknownComponentException
     */
    private Channel createNewChannel(Endpoint endpoint) throws UnknownComponentException {
        //creating components
        HashMap<String, MediaSource> sources = new HashMap();
        HashMap<String, MediaSink> sinks = new HashMap();
        HashMap<String, Inlet> inlets = new HashMap();
        HashMap<String, Outlet> outlets = new HashMap();
        
        for (ComponentFactory factory: factories) {
            Component component = factory.newInstance(endpoint);
            
            if (component instanceof MediaSink) {
                sinks.put(component.getName(), (MediaSink)component);
            }
            
            if (component instanceof MediaSource) {
                sources.put(component.getName(),(MediaSource)component);
            }
            
            if (component instanceof Inlet) {
                sinks.put(component.getName(), ((Inlet)component).getInput());
                inlets.put(component.getName(), (Inlet)component);
            }
            
            if (component instanceof Outlet) {
                sources.put(component.getName(), ((Outlet)component).getOutput());
                outlets.put(component.getName(), (Outlet)component);
            }
        }
        
        Channel channel = new Channel(sources, sinks, inlets, outlets);
        
        //creating pipes
        for (PipeFactory pipeFactory : pipes) {
            pipeFactory.openPipe(channel);
        }
        
        return channel;
    }

    /**
     * Get amount of prestarted channels.
     * 
     * @return the amount of prestarted channels.
     */
    public int getCoreSize() {
        return coreSize;
    }

    /**
     * Get maximum size of prestarted channels.
     * 
     * @return pool's max size.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Define amount of initialy prestarted channels.
     * 
     * @param coreSize the number of prestarted channels.
     */
    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    /**
     * Define max pools size;
     * 
     * @param maxSize the maximum size of pool.
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    
    /**
     * Modify pipe list.
     * 
     * @param pipes the list of pipes beans defining media flow path
     */
    public void setPipes(List<PipeFactory> pipes) {
        this.pipes = pipes;
    }
    
    /**
     * Gets the existing list of pipes.
     * 
     * @return the list of pipes.
     */
    public List getPipes() {
        return this.pipes;
    }
    
    /**
     * Gets the list of components which will be placed into the new channel.
     * 
     * @return the list of media component.
     */
    public List getComponents() {
        return factories;
    }
    
    /**
     * Sets the list of components which will be placed into the new channel.
     * 
     * @return the list of media component.
     */
    public void setComponents(List components) {
        this.factories = components;
    }
    
    /**
     * Informs factory that specified channel not longer used.
     * 
     * @param channel the reference to unused channel.
     */
    public void release(Channel channel) {
        if (started && channels.size() < maxSize) {
            channels.add(channel);
        }
    }
    
    /**
     * Starts this factory.
     * 
     */
    public void start() throws Exception {        
        started = true;
        if (factories == null) {
            factories = new ArrayList();
        }
        if (pipes == null) {
            pipes = new ArrayList();
        }
        logger.info("Started, core size = " + coreSize);
    }
    
    /**
     * Stop this factory.
     */
    public void stop() {
        started = false;
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.remove(i);
            channel.close();
        }
    }
  
}

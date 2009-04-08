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
import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.Outlet;

/**
 *
 * @author kulikov
 */
public class ChannelFactory {
    
    private List<PipeFactory> pipes;
    private List<ComponentFactory> factories;
    
    public Channel newInstance() throws UnknownComponentException {
        //creating components
        HashMap<String, MediaSource> sources = new HashMap();
        HashMap<String, MediaSink> sinks = new HashMap();
        HashMap<String, Inlet> inlets = new HashMap();
        HashMap<String, Outlet> outlets = new HashMap();
        
        for (ComponentFactory factory: factories) {
            Component component = factory.newInstance("");
            
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
    
    public void setPipes(List<PipeFactory> pipes) {
        this.pipes = pipes;
    }
    
    public List getPipes() {
        return this.pipes;
    }
    
    public List getComponents() {
        return factories;
    }
    
    public void setComponents(List components) {
        this.factories = components;
    }
    
    public void release(Channel channel) {
    }
}

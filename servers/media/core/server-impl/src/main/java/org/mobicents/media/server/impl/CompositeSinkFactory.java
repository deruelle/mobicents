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

import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;
import org.mobicents.media.server.resource.ChannelFactory;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class CompositeSinkFactory implements ComponentFactory {

    private String name;
    
    private ChannelFactory channelFactory;
    private ComponentFactory sinkFactory;
    
    public String getName() {
        return name;
    }
    
    public void setNam(String name) {
        this.name = name;
    }
    
    public ComponentFactory getSinkFactory() {
        return sinkFactory;
    }
    
    public void setSinkFactory(ComponentFactory sinkFactory) {
        this.sinkFactory = sinkFactory;
    }
    
    public ChannelFactory getChannelFactory() {
        return channelFactory;
    }

    public void setChannelFactory(ChannelFactory channelFactory) {
        this.channelFactory = channelFactory;
    }
    
    public Component newInstance(Endpoint endpoint) {
        CompositeSink compositeSink = new CompositeSink(name);
        return compositeSink;
    }

}

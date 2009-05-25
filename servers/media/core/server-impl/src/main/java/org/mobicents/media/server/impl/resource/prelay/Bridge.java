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
package org.mobicents.media.server.impl.resource.prelay;

import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class Bridge {
    protected final static Format DEFAULT_FORMAT[] = new Format[0];
    
    protected Proxy[] proxies = new Proxy[2];
    private EndpointImpl endpoint;
    
    protected PacketRelaySource source;
    protected PacketRelaySink sink;
    
    public Bridge(Endpoint endpoint) {
        this.endpoint = (EndpointImpl) endpoint;
        
        proxies[0] = new Proxy("packet.relay");
        proxies[1] = new Proxy("packet.relay");
        
        PacketRelaySinkFactory sinkFactory = (PacketRelaySinkFactory) this.endpoint.getSinkFactory();
        PacketRelaySourceFactory sourceFactory = (PacketRelaySourceFactory) this.endpoint.getSourceFactory();
        
        source = new PacketRelaySource(sourceFactory.getName(), this);
        sink = new PacketRelaySink(sinkFactory.getName(), this);
    }
    
    public PacketRelaySource getSource() {
        return source;
    }
    
    public PacketRelaySink getSink() {
        return sink;
    }
}

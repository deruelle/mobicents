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

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.spi.Connection;

/**
 *
 * @author kulikov
 */
public class PacketRelaySink extends AbstractSink {

    private Bridge bridge;
    
    public PacketRelaySink(String name, Bridge bridge) {
        super(name);
        this.bridge = bridge;
    }
    
    public Bridge getBridge() {
        return bridge;
    }
    
    @Override
    public void connect(MediaSource source) {
        Connection connection = source.getConnection();
        if (bridge.proxies[0].isIsSourceConnected()) {
            bridge.proxies[1].getInput().connect(source);
        } else if (bridge.proxies[0].getConnectionID() == null) {
            bridge.proxies[0].getInput().connect(source);
            bridge.proxies[0].setConnectionID(connection.getId());
        } else if (bridge.proxies[0].getConnectionID().matches(connection.getId())) {
            //source of this connection already connected to this proxy
            //connecting to another
            bridge.proxies[1].getInput().connect(source);
        } else {
            bridge.proxies[0].getInput().connect(source);
        }
    }
    
    @Override
    public void disconnect(MediaSource source) {
        Connection connection = source.getConnection();
        if (bridge.proxies[0].getConnectionID() == null) {
            bridge.proxies[0].getInput().disconnect(source);
            bridge.proxies[0].setConnectionID(connection.getId());
        } else if (bridge.proxies[0].getConnectionID().matches(connection.getId())) {
            //source of this connection already connected to this proxy
            //connecting to another
            bridge.proxies[1].getInput().disconnect(source);
        } else {
            bridge.proxies[0].getInput().disconnect(source);
        }
    }
    
    public Format[] getFormats() {
        return Bridge.DEFAULT_FORMAT;
    }

    public boolean isAcceptable(Format format) {
        return true;
    }

    public void receive(Buffer buffer) {
    }

}

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

package org.mobicents.media;

import java.io.Serializable;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.NotificationListener;

/**
 * Represents media component.
 * 
 * @author kulikov
 */
public interface Component extends Serializable {
    public final static int AUDIO_PLAYER = 10;
    public final static int AUDIO_RECORDER = 11;
    
    public final static int DTMF_DETECTOR = 20;
    public final static int DTMF_GENERATOR = 21;        
    
    /**
     * Defines the integer identificator for type of this resource. 
     * 
     * @return an integer constant.
     */
    public int getResourceType();
    public void setResourceType(int resType);
    
    /**
     * Gets the name of the component.
     * 
     * @return name of this component;
     */
    public String getName();
    
    public Endpoint getEndpoint();
    public void setEndpoint(Endpoint endpoint);
    
    public Connection getConnection();
    public void setConnection(Connection connection);
    
    /**
     * Registers new notfications listener 
     * 
     * @param listener the listener object.
     */
    public void addListener(NotificationListener listener);
    
    /**
     * Unregisters new notfications listener 
     * 
     * @param listener the listener object.
     */
    public void removeListener(NotificationListener listener);
    
}

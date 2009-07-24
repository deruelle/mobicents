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
 * <i>Component</i> is an Object that is responsible for any media 
 * data processing. 
 * 
 * Examples of components are the audio player, recoder, 
 * DTMF detector, etc. The <code>Component</code> is a supper class for all 
 * media processing components.
 * 
 * @author kulikov
 */
public interface Component extends Serializable {
    
    /**
     * Gets the unique identifier of this component.
     * 
     * @return
     */
    public String getId();
    
    /**
     * Gets the name of the component.
     * The component of same type can share same name.
     * 
     * @return name of this component;
     */
    public String getName();
    
    /**
     * Starts media processing.
     */
    public void start();
    
    /**
     * Terminates media processing.
     */
    public void stop();
    
    /**
     * Gets the reference to endpoint to which this component belongs
     * 
     * @return the endpoint reference.
     */
    public Endpoint getEndpoint();
    
    /**
     * Sets reference to the endpoint to which this component belongs.
     * 
     * @param endpoint the reference to the endpoint.
     */
    public void setEndpoint(Endpoint endpoint);
    
    /**
     * Reference to the connection to which this component belongs.
     * 
     * @return the connection instance.
     */    
    public Connection getConnection();
    
    /**
     * Set reference to the connection to which this component belongs.
     * 
     * @param connection the connection instance.
     */
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
    
    /**
     * Resets any transmission stats.
     */
    public void resetStats();
    
}

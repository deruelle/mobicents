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

import org.mobicents.media.server.spi.SyncSource;

/**
 * Abstracts a read interface that pushes data in the form of Buffer objects.
 *  
 * This interface allows a source stream to transfer data in the form of an 
 * entire media chunk to the user of this source stream.
 * 
 * @author Oleg Kulikov
 */
public interface MediaSource extends Component, Runnable {
    
    /**
     * Gets the source used for synchronization of processing
     * 
     * @return timer instance.
     */
    public SyncSource getSyncSource();

    /**
     * Assign the source for synchronization of the processing.
     * 
     * @param syncSource the source of synchronization.
     */
    public void setSyncSource(SyncSource syncSource);
    
    /**
     * Gets the packetization period.
     * 
     * @return the value of currently used packetization period in milliseconds.
     * 
     */
    public int getPeriod();
    
    /**
     * Assigns new packetization period.
     * 
     * @param period the value of packetization period in milliseconds.
     */
    public void setPeriod(int period);
    
    /**
     * Joins this source with media sink.
     * 
     * @param sink the media sink to join with.
     */
    public void connect(MediaSink sink);
    
    /**
     * Drops connection between this source and media sink.
     * 
     * @param sink the sink to disconnect.
     */
    public void disconnect(MediaSink sink);
    

    /**
     * Flags which indicates that multiple connections to this sink are allowed.
     * 
     * @return true if multiple connections are allowed
     */
    public boolean isMultipleConnectionsAllowed();

    /**
     * Connects this source with specified inlet.
     * 
     * @param inlet the inlet to connect with.
     */
    public void connect(Inlet inlet);
    
    /**
     * Disconnects this source with specified inlet.
     * 
     * @param inlet the inlet to disconnect from.
     */
    public void disconnect(Inlet inlet);
    
    /**
     * Get possible formats in which this source can stream media.
     * 
     * @return an array of Format objects.
     */
    public Format[] getFormats();    
    
    /**
     * Gets the state of the component.
     * 
     * @return  true if component is connected to other component.
     */
    public boolean isConnected();
    
    /**
     * Gets true if component is transmitting media.
     * 
     * @return true if component is transmitting media.
     */
    public boolean isStarted();
    
    /**
     * Shows the number of packets received by this medis sink since last start.
     * 
     * @return the number of packets.
     */
    public long getPacketsTransmitted();
    
    /**
     * Shows the number of bytes received by this sink since last start;
     * 
     * @return the number of bytes.
     */
    public long getBytesTransmitted();
    
}

/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media;

import java.io.IOException;


/**
 * Implements the media consumer.
 * 
 * @author Oleg Kulikov
 */
public interface MediaSink extends Component {
    
    /**
     * Get possible formats which this consumer can handle.
     * 
     * @return an array of Format objects.
     */
    public Format[] getFormats();    
    
    /**
     * Checks is the specified format is acceptable by this source.
     * This method is used by DEMUX to perform proper demultiplexing.
     * 
     * @param format the format to check.
     * @return true if this source can handle specified format.
     */
    public boolean isAcceptable(Format format);
    
    /**
     * Joins this media sink with media source.
     * The concrete media sink can allow to join with multiple sources
     * 
     * @param source the media source to join with.
     */
    public void connect(MediaSource source);
    
    /**
     * Breaks connection with media source.
     * The concrete media sink can allow to join with multiple sources so
     * this method requires the explicit source for disconnection.
     * 
     * @param source the source to disconnect from.
     */
    public void disconnect(MediaSource source);
    
    /**
     * This methos is called by media source when new media is available
     * 
     * @param buffer the Buffer object which contains the next portion of media.
     */
    public void receive(Buffer buffer) throws IOException;
    
    /**
     * Gets the state of the component.
     * 
     * @return  true if component is connected to other component.
     */
    public boolean isConnected();
    
    /**
     * Gets true if component is able to receive media.
     * 
     * @return true if component is able to receive media.
     */
    public boolean isStarted();
    
    /**
     * Shows the number of packets received by this medis sink since last start.
     * 
     * @return the number of packets.
     */
    public long getPacketsReceived();
    
    /**
     * Shows the number of bytes received by this sink since last start;
     * 
     * @return the number of bytes.
     */
    public long getBytesReceived();
    
}

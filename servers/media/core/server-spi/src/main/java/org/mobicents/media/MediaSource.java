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

/**
 * Abstracts a read interface that pushes data in the form of Buffer objects.
 *  
 * This interface allows a source stream to transfer data in the form of an 
 * entire media chunk to the user of this source stream.
 * 
 * @author Oleg Kulikov
 */
public interface MediaSource extends Component {
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
     * Starts media producing.
     */
    public void start();
    
    /**
     * Terminates media producing.
     */
    public void stop();
    
    /**
     * Get possible formats in which this source can stream media.
     * 
     * @return an array of Format objects.
     */
    public Format[] getFormats();    
    
    
    //public void dispose();
    
}

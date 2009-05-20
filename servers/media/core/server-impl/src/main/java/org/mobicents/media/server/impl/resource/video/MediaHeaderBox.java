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
package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * The media header declares overall information that is media-independent, 
 * and relevant to characteristics of the media in a track.
 * 
 * Box Type: mdhd
 * Container: Media Box (mdia)
 * Mandatory: Yes
 * Quantity: Exactly one
 * 
 * @author kulikov
 */
public class MediaHeaderBox extends FullBox {

    /** 
     * is an integer that declares the creation time of the presentation 
     * (in seconds since midnight, Jan. 1, 1904, in UTC time 
     */
    private long creationTime;
    /**
     * is an integer that declares the most recent time the presentation was 
     * modified (inseconds since midnight, Jan. 1, 1904, in UTC time)
     */
    private long modificationTime;
    /**
     * is an integer that specifies the time-scale for the entire presentation; 
     * this is the number of time units that pass in one second. For example, a 
     * time coordinate system that measures time in sixtieths of a second has a 
     * time scale of 60.
     */
    private int timescale;
    /**
     * is an integer that declares length of the presentation (in the indicated 
     * timescale). This property is derived from the presentation’s tracks: 
     * the value of this field corresponds to the duration of the longest track 
     * in the presentation
     */
    private long duration;
    
    private int language;
    
    public MediaHeaderBox(long size, String type) {
        super(size, type);
    }
    
    /**
     * Gets the creation time of the media in this track.
     * 
     * @return creation time
     */
    public Date getCreationTime() {
        return new Date(creationTime);
    }

    /**
     * Gets the modification time of the media in this track.
     * 
     * @return creation time
     */
    public Date getModificationTime() {
        return new Date(modificationTime);
    }

    /**
     * Gets the time scale of the media in this track.
     * 
     * @return the number of time units that pass in one second. For example, a 
     * time coordinate system that measures time in sixtieths of a second has a 
     * time scale of 60.
     */
    public int getTimeScale() {
        return timescale;
    }
    
    /**
     * Gets duration of the media in this track.
     * 
     * @return an integer that declares length of the media in this track (in the indicated 
     * timescale). 
     */
    public long getDuration() {
        return duration;
    }
    
    @Override
    protected int load(DataInputStream fin) throws IOException {
        super.load(fin);        
        if (this.getVersion() == 1) {
            this.creationTime = read64(fin);
            this.modificationTime = read64(fin);
            this.timescale = fin.readInt();
            this.duration = read64(fin);
        } else {
            this.creationTime = fin.readInt();
            this.modificationTime = fin.readInt();
            this.timescale = fin.readInt();
            this.duration = fin.readInt();
        }
        
        language = (fin.readByte() << 8) | fin.readByte();
        fin.readByte();
        fin.readByte();
        
        return (int) getSize();
    }
    
}

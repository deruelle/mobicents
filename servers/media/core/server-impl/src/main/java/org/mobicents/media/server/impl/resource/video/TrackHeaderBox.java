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

/**
 *
 * @author kulikov
 */
public class TrackHeaderBox extends FullBox {
    
    private long creationTime;
    private long modificationTime;
    private long duration;
    private int trackID;
    private int layer;
    private int alternateGroup;
    private int volume;
    private int[] matrix = new int[9];
    private int width;
    private int height;
    
    public TrackHeaderBox(long size, String type) {
        super(size, type);
    }

    @Override
    protected int load(DataInputStream fin) throws IOException {
        super.load(fin);        
        if (this.getVersion() == 1) {
            this.creationTime = read64(fin);
            this.modificationTime = read64(fin);
            this.trackID = fin.readInt();
            fin.readInt(); //spare
            this.duration = read64(fin);
        } else {
            this.creationTime = fin.readInt();
            this.modificationTime = fin.readInt();
            this.trackID = fin.readInt();
            fin.readInt(); //spare
            this.duration = fin.readInt();
        }

        //reserved
        fin.readInt();
        fin.readInt();
        
        //reading layer. 
        layer = (fin.readByte() << 8) | fin.readByte();
        alternateGroup = (fin.readByte() << 8) | fin.readByte();
        
        //reading volume. it is a fixed 8.8 number
        volume = fin.readByte() + fin.readByte()/10;
        
        //skip reserved 16bits
        fin.readByte();
        fin.readByte();
        
        
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = fin.readInt();
        }
        
        width = fin.readInt();
        height = fin.readInt();
        
        return (int)getSize();
    }
    
}

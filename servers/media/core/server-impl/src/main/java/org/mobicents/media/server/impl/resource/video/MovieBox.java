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
 * The metadata for a presentation is stored in the single Movie Box which 
 * occurs at the top-level of a file.
 * 
 * Normally this box is close to the beginning or end of the file, though this 
 * is not required.
 * 
 * Box Type: moov
 * Container: File
 * Mandatory: Yes
 * Quantity: Exactly one
 * 
 * @author kulikov
 */
public class MovieBox extends Box {

    private MovieHeaderBox mvhd;
    private TrackBox track;
    
    public MovieBox(long size, String type) {
        super(size, type);
    }
    
    @Override
    protected int load(DataInputStream in) throws IOException {
        //loading movie header
        int len = readLen(in);
        String type = readType(in);
        
        if (!type.equals("mvhd")) {
            throw new IOException("Movie Header Box expected");
        }
        
        mvhd = new MovieHeaderBox(len, type);
        mvhd.load(in);
        
        //loading track
        len = readLen(in);
        type = readType(in);
        
        if (!type.equals("trak")) {
            throw new IOException("Track box expected");
        }
        
        track = new TrackBox(len, type);
        track.load(in);
        
        return (int) this.getSize();
    }

}

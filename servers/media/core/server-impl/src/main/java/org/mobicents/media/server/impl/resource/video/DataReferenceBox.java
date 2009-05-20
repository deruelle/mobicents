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
 * The data reference object contains a table of data references (normally URLs) 
 * that declare the location(s) of the media data used within the presentation. 
 * The data reference index in the sample description ties entries in this 
 * table to the samples in the track. A track may be split over several 
 * sources in this way. If the flag is set indicating that the data is in the 
 * same file as this box, then no string (not even an empty one) shall be 
 * supplied in the entry field.
 * 
 * Container: Data Information Box (dinf)
 * Mandatory: Yes
 * Quantity: Exactly one
 * 
 * @author kulikov
 */
public class DataReferenceBox extends FullBox {
    
    private int entryCount;
    private Box[] dataEntry;
    
    public DataReferenceBox(long size, String type) {
        super(size, type);
    }

    @Override
    protected int load(DataInputStream fin) throws IOException {
        super.load(fin);
        entryCount = fin.readInt();
        dataEntry = new Box[entryCount];
        for (int i = 0; i < entryCount; i++) {
            int len = readLen(fin);
            String type = readType(fin);
            
            if (type.equals("url ")) {
                dataEntry[i] = new DataEntryUrlBox(len, type);
                dataEntry[i].load(fin);
            } else if (type.equals("urn")) {
                dataEntry[i] = new DataEntryUrnBox(len, type);
                dataEntry[i].load(fin);
            } else {
                throw new IOException("Unexpected box type: " + type);
            }
        }
        return (int) getSize();
    }
    
}

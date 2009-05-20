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
 * The data information box contains objects that declare the location of the
 * media information in a track.
 * 
 * Box Type: dinf
 * Container: Media Information Box (minf) or Meta Box (meta)
 * Mandatory: Yes (required within minf box) and No (optional within meta box)
 * Quantity: Exactly one
 * 
 * @author kulikov
 */
public class DataInformationBox extends Box {

    private DataReferenceBox dref;
    
    public DataInformationBox(long size, String type) {
        super(size, type);
    }

    @Override
    protected int load(DataInputStream fin) throws IOException {
        int len = readLen(fin);
        String type = readType(fin);
        
        if (!type.equals("dref")) {
            throw new IOException();
        }
        dref = new DataReferenceBox(len, type);
        dref.load(fin);
        
        return (int) getSize();
    }

    public DataReferenceBox getDref() {
        return dref;
    }
    
    
}

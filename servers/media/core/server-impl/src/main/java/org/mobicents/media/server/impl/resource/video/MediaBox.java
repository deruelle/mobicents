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
public class MediaBox extends Box {

    private MediaHeaderBox header;
    private HandlerReferenceBox handler;
    private MediaInformationBox inf;
    
    public MediaBox(long size, String type) {
        super(size, type);
    }

    @Override
    protected int load(DataInputStream fin) throws IOException {
        int len = readLen(fin);
        String type = readType(fin);
        
        if (!type.equals("mdhd")) {
            throw new IOException("Media header box is expected");
        }
        
        header = new MediaHeaderBox(len, type);
        header.load(fin);
        
        len = readLen(fin);
        type = readType(fin);
        
        if (!type.equals("hdlr")) {
            throw new IOException("Handler box is mandatory");
        }
        
        handler = new HandlerReferenceBox(len, type);
        handler.load(fin);

        len = readLen(fin);
        type = readType(fin);
        
        if (!type.equals("minf")) {
            throw new IOException("Handler box is mandatory");
        }
        
        inf = new MediaInformationBox(len, type);
        inf.load(fin);
        
        return (int) getSize();
    }
    
}

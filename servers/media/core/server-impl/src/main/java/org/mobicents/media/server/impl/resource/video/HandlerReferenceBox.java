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
 * This box within a Media Box declares the process by which the media-data in 
 * the track is presented, and thus, the nature of the media in a track. 
 * For example, a video track would be handled by a video handler.
 * This box when present within a Meta Box, declares the structure or format of 
 * the meta box contents. There is a general handler for metadata streams 
 * of any type; the specific format is identified by the sample entry, as for 
 * video or audio, for example. If they are in text, then a MIME format is 
 * supplied to document their format; if in XML, each sample is a complete 
 * XML document, and the namespace of the XML is also supplied.
 * 
 * 
 * Box Type: hdlr
 * Container: Media Box (mdia) or Meta Box (meta)
 * Mandatory: Yes
 * Quantity: Exactly one
 * 
 * @author kulikov
 */
public class HandlerReferenceBox extends FullBox {
    
    private String handlerType;
    private String name;
    
    public HandlerReferenceBox(long size, String type) {
        super(size, type);
    }

    public String getHandlerType() {
        return handlerType;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    protected int load(DataInputStream fin) throws IOException {
        super.load(fin);
        
        fin.readInt();
        
        handlerType = readType(fin);
        
        fin.readInt();
        fin.readInt();
        fin.readInt();
        
        name = readText(fin);
        return (int) getSize();
    }    
}

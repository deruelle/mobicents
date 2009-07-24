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
 * This box contains all the objects that declare characteristic information of 
 * the media in the track.
 * 
 * Box Type: minf
 * Container: Media Box (mdia)
 * Mandatory: Yes
 * Quantity: Exactly one
 * 
 * @author kulikov
 */
public class MediaInformationBox extends Box {

    private SoundMediaHeaderBox soundHeader;
    private VideoMediaHeaderBox videoHeader;
    private HintMediaHeaderBox hintHeader;
    private NullMediaHeaderBox nullMediaHeader;
    private DataInformationBox dinf;
    private SampleTableBox stbl;
    
    public MediaInformationBox(long size, String type) {
        super(size, type);
    }

    @Override
    protected int load(DataInputStream fin) throws IOException {
        int count = 8;
        while (count < getSize()) {
            int len = readLen(fin);
            String type = readType(fin);            
            if (type.equals("vmhd")) {
                videoHeader = new VideoMediaHeaderBox(len, type);
                count += videoHeader.load(fin);
            } else if (type.equals("smhd")) {
                soundHeader = new SoundMediaHeaderBox(len, type);
                count += soundHeader.load(fin);
            } else if (type.equals("hmhd")) {
                hintHeader = new HintMediaHeaderBox(len, type);
                count += hintHeader.load(fin);
            } else if (type.equals("nmhd")) {
                nullMediaHeader = new NullMediaHeaderBox(len, type);
                count += nullMediaHeader.load(fin);
            } else if (type.equals("dinf")) {
                dinf = new DataInformationBox(len, type);
                count += dinf.load(fin);
            } else if (type.equals("stbl")) {
                stbl = new SampleTableBox(len, type);
                count += stbl.load(fin);
            } else throw new IOException("Unknown box=" + type);
        }
        return (int) getSize();
    }
    
}

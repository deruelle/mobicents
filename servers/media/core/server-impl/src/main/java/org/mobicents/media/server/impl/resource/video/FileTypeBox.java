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
public class FileTypeBox extends Box {
    
    private String majorBrand;
    private int minorVersion;
    private String[] compatibleBrands;
    
    public FileTypeBox(long size, String type) {
        super(size, type);
    }

    private byte[] read(DataInputStream in) throws IOException {
        byte[] buff = new byte[4];
        for (int i = 0; i < buff.length; i++) {
            buff[i] = in.readByte();
        }
        return buff;
    }
    
    public String getMajorBrand() {
        return this.majorBrand;
    }
    
    public int getMinorVersion() {
        return this.minorVersion; 
    }
    
    public String[] getCompatibleBrands() {
        return this.compatibleBrands;
    }
    
    @Override
    protected int load(DataInputStream fin) throws IOException {
        this.majorBrand = new String(read(fin));
        this.minorVersion = fin.readInt();
    
        long remainder = getSize() - 16;
        int count = (int)(remainder / 4);
        
        compatibleBrands = new String[count];
        for (int i = 0; i < count; i++) {
            compatibleBrands[i] = new String(read(fin));
        }
        return (int)getSize();
    }
    
}

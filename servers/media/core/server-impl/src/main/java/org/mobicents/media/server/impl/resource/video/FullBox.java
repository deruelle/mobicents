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
public class FullBox extends Box {

    /** is an integer that specifies the version of this format of the box. */
    private int version;
    /** is a map of flags */
    private int flags;
    
    public FullBox(long size, String type) {
        super(size, type);
    }

    /**
     * Gets the version of the format of the box.
     * 
     * @return the integer format identifier.
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Gets the map of flags.
     * 
     * @return the indeteger where loweresrt 24 bits are map of flags.
     */
    public int getFlags() {
        return flags;
    }
    
    protected long read64(DataInputStream fin) throws IOException {
        return (fin.readInt() << 32) | fin.readInt();
    }
    
    @Override
    protected int load(DataInputStream fin) throws IOException {
        this.version = fin.readByte();
        this.flags = (fin.readByte() << 16) | (fin.readByte() << 8) | fin.readByte();
        return 4;
    }
    
}

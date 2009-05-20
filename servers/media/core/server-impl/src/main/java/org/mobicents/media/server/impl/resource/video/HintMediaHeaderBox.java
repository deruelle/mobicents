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
 * The hint media header contains general information, independent of the 
 * protocol, for hint tracks. (A PDU is a Protocol Data Unit.)
 * 
 * @author kulikov
 */
public class HintMediaHeaderBox extends FullBox {

    private int maxPDUSize;
    private int avgPDUSize;
    private int maxBitRate;
    private int avgBitRate;
    
    public HintMediaHeaderBox(long size, String type) {
        super(size, type);
    }

    public int getAvgBitRate() {
        return avgBitRate;
    }

    public int getAvgPDUSize() {
        return avgPDUSize;
    }

    public int getMaxBitRate() {
        return maxBitRate;
    }

    public int getMaxPDUSize() {
        return maxPDUSize;
    }
    
    
    @Override
    protected int load(DataInputStream fin) throws IOException {
        super.load(fin);
        maxPDUSize = (fin.readByte() << 8) | fin.readByte();
        avgPDUSize = (fin.readByte() << 8) | fin.readByte();
        maxBitRate = fin.readInt();
        avgBitRate = fin.readInt();
        fin.readInt();
        return (int) getSize();
    }    
    
}

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
 * The sound media header contains general presentation information, 
 * independent of the coding, for audio media. This header is used for all 
 * tracks containing audio.
 * 
 * @author kulikov
 */
public class SoundMediaHeaderBox extends FullBox {

    private double balance;
    
    public SoundMediaHeaderBox(long size, String type) {
        super(size, type);
    }
    
    /**
     * Gets the balance of the audio track in stereo space.
     * 
     * @return number that places mono audio tracks in a stereo space
     */
    public double getBalance() {
        return balance;
    }
    
    @Override
    protected int load(DataInputStream fin) throws IOException {
        super.load(fin);
        balance = fin.readByte() + fin.readByte()/10;
        
        fin.readByte();
        fin.readByte();
        
        return (int) getSize();
    }    
}

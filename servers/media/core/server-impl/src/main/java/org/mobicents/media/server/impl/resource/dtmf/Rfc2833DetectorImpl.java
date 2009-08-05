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
package org.mobicents.media.server.impl.resource.dtmf;

import java.io.IOException;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.resource.DtmfDetector;

/**
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class Rfc2833DetectorImpl extends DtmfBuffer implements DtmfDetector {

    public final static Format[] FORMATS = new Format[]{AVProfile.DTMF};
    private int volume;
    public final static String[] TONE = new String[] {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#", "A",
        "B", "C", "D"
    };

    
    public Rfc2833DetectorImpl(String name) {
        super(name);
    }

    public void onMediaTransfer(Buffer buffer) throws IOException {
        byte[] data = (byte[]) buffer.getData();
        int offset = buffer.getOffset();
        
        String digit = TONE[data[offset]];
        double v = data[offset + 1] & 0x3F;
        if (v >= Math.abs(volume)) {
            push(digit);
        }
    }

    public Format[] getFormats() {
        return FORMATS;
    }

    public boolean isAcceptable(Format format) {
        return AVProfile.DTMF.matches(format);
    }

    public void setVolume(int level) {
        this.volume = level;
    }

    public int getVolume() {
        return volume;
    }
}

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

package org.mobicents.media.server.impl.rtp;

import java.io.Serializable;

/**
 *
 * @author Oleg Kulikov
 */
public class RtpHeader implements Serializable {
    private int version = 2;
    private boolean padding = false;
    private boolean extensions = false;
    private int cc = 0;
    private boolean marker = false;
    private int payloadType;
    private int seqNumber;
    private int timestamp;
    private long ssrc;

    public RtpHeader(byte payloadType, int seqNumber, int timestamp, long ssrc) {
        this.payloadType = payloadType;
        this.seqNumber = seqNumber;
        this.timestamp = timestamp;
        this.ssrc = ssrc;
    }
    
    public int getPayloadType() {
        return payloadType;
    }

    public int getSeqNumber() {
        return this.seqNumber;
    }

    public int getTimestamp() {
        return timestamp;
    }
    
}

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

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;

/**
 * 
 * @author Oleg Kulikov
 */
public class ReceiveStream extends AbstractSource {

    /**
     * 
     */
    private Buffer frame;
    protected Format[] formats;
    private RtpSocket rtpSocket;
    private RtpDepacketizer depacketizer;
    
    /** Creates a new instance of ReceiveStream */
    public ReceiveStream(RtpSocket rtpSocket, int jitter) {
        super("ReceiveStream");
        setSyncSource(rtpSocket.timer);
        this.rtpSocket = rtpSocket;
        depacketizer = new RtpDepacketizer(jitter, getSyncSource().getHeartBeat(), rtpSocket.getRtpMap());
    }

    protected void push(RtpPacket rtpPacket) {
        depacketizer.push(rtpPacket);
    }

    @Override
    public void beforeStart() {
        depacketizer.reset();
    }

    public Format[] getFormats() {
        Format[] fmts = new Format[rtpSocket.getRtpMap().size()];
        rtpSocket.getRtpMap().values().toArray(fmts);
        return fmts;
    }

    public void evolve(Buffer buffer, long seq) {
        depacketizer.evolve(buffer);
    }
}

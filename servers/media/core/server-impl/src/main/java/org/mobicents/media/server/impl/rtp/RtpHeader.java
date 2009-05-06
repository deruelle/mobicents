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

	private byte[] bin = new byte[12];

	public void RtpHeader() {
	}

	public void init(byte[] bin) {
		int b = bin[0] & 0xff;
		version = (b & 0x0C) >> 6;
		padding = (b & 0x20) == 0x020;
		extensions = (b & 0x10) == 0x10;
		cc = b & 0x0F;

		b = bin[1] & 0xff;

		marker = (b & 0x80) == 0x80;
		payloadType = b & 0x7F;

		seqNumber = (bin[2] & 0xff) << 8;
		seqNumber = seqNumber | (bin[3] & 0xff);

		int p = (bin[4] << 24);
		int q = (bin[5] << 16);
		int r = (bin[6] << 8);
		int s = (bin[7]);

		timestamp = p | q | r | s;

		p = (bin[4] << 24);
		q = (bin[5] << 16);
		r = (bin[6] << 8);
		s = (bin[7]);

		ssrc = p | q | r | s;
	}
	
	public void init(boolean marker, byte payloadType, int seqNumber, int timestamp, long ssrc) {
		this.payloadType = payloadType;
		this.seqNumber = seqNumber;
		this.timestamp = timestamp;
		this.ssrc = ssrc;
		this.marker = marker;

		bin[0] = (byte) (0x80);
		
		bin[1] = marker ? (byte) (payloadType | 0x80) : (byte) (payloadType & 0x7f);		
		//bin[1] = (payloadType);
		bin[2] = ((byte) ((seqNumber & 0xFF00) >> 8));
		bin[3] = ((byte) (seqNumber & 0x00FF));

		bin[4] = ((byte) ((timestamp & 0xFF000000) >> 24));
		bin[5] = ((byte) ((timestamp & 0x00FF0000) >> 16));
		bin[6] = ((byte) ((timestamp & 0x0000FF00) >> 8));
		bin[7] = ((byte) ((timestamp & 0x000000FF)));

		bin[8] = ((byte) ((ssrc & 0xFF000000) >> 24));
		bin[9] = ((byte) ((ssrc & 0x00FF0000) >> 16));
		bin[10] = ((byte) ((ssrc & 0x0000FF00) >> 8));
		bin[11] = ((byte) ((ssrc & 0x000000FF)));
	}

	public void init(byte payloadType, int seqNumber, int timestamp, long ssrc) {
		this.payloadType = payloadType;
		this.seqNumber = seqNumber;
		this.timestamp = timestamp;
		this.ssrc = ssrc;

		bin[0] = (byte) (0x80);
		bin[1] = (payloadType);
		bin[2] = ((byte) ((seqNumber & 0xFF00) >> 8));
		bin[3] = ((byte) (seqNumber & 0x00FF));

		bin[4] = ((byte) ((timestamp & 0xFF000000) >> 24));
		bin[5] = ((byte) ((timestamp & 0x00FF0000) >> 16));
		bin[6] = ((byte) ((timestamp & 0x0000FF00) >> 8));
		bin[7] = ((byte) ((timestamp & 0x000000FF)));

		bin[8] = ((byte) ((ssrc & 0xFF000000) >> 24));
		bin[9] = ((byte) ((ssrc & 0x00FF0000) >> 16));
		bin[10] = ((byte) ((ssrc & 0x0000FF00) >> 8));
		bin[11] = ((byte) ((ssrc & 0x000000FF)));
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

	public byte[] toByteArray() {
		return bin;
	}

	public boolean getMarker() {
		return this.marker;
	}
}

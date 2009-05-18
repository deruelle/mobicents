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
	private long timestamp;
	private long ssrc;

	private int initMarker = 0;
	private static final int _END_OF_INIT = 11;

	private byte[] bin = new byte[12];

	public void RtpHeader() {
	}

	public void init(byte[] bin) {
		this.initPartialy(bin);
		//this.bin = bin;
		this.initMarker = _END_OF_INIT;
	}

	public int getPayloadType() {
		return payloadType;
	}

	public int getSeqNumber() {
		return this.seqNumber;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getVersion() {
		return version;
	}

	public boolean isPadding() {
		return padding;
	}

	public boolean isExtensions() {
		return extensions;
	}

	public int getCc() {
		return cc;
	}

	public long getSsrc() {
		return ssrc;
	}

	public byte[] toByteArray() {
		return bin;
	}

	public boolean getMarker() {
		return this.marker;
	}

	public void setMarker(boolean marker) {
		this.marker = marker;
	}

	/**
	 * Initialized header with passed data. Header is 12 bytes long. this method
	 * takes byte[] and makes header of first 12.
	 * 
	 * @param presumableHeaderData
	 * @return NUmber indicatind bytes:
	 *         <ul>
	 *         <li>less than zero - indicates number of requried bytes</li>
	 *         <li>zero - no more bytes are needed, passed array has been
	 *         consumed fully</li>
	 *         <li>greater than zero - number of trailing bytes that were not
	 *         consumed.</li>
	 *         </ul>
	 */
	public int initPartialy(byte[] presumableHeaderData) {

		int index = 0;
		for (; index < presumableHeaderData.length; index++, initMarker++) {
			if (initMarker == 0) {
				int b = presumableHeaderData[initMarker] & 0xff;
				version = (b & 0x0C) >> 6;
				padding = (b & 0x20) == 0x020;
				extensions = (b & 0x10) == 0x10;
				cc = b & 0x0F;
				bin[initMarker] = presumableHeaderData[index];
				continue;
			}
			
			if (initMarker == 1) {
				int b = presumableHeaderData[index] & 0xff;

				marker = (b & 0x80) == 0x80;
				payloadType = b & 0x7F;
				bin[initMarker] = presumableHeaderData[index];
				continue;
			}
			if (initMarker == 2) {

				seqNumber = (presumableHeaderData[index] & 0xff) << 8;
				bin[initMarker] = presumableHeaderData[index];
				continue;
			}
			if (initMarker == 3) {

				seqNumber = seqNumber | (presumableHeaderData[index] & 0xff);
				bin[initMarker] = presumableHeaderData[index];
				continue;
			}

			if (initMarker == 4) {
				int p = ((presumableHeaderData[index] & 0xFF)<< 24);
				bin[initMarker] = presumableHeaderData[index];
				timestamp = p;
				continue;
			}
			if (initMarker == 5) {
				int q = ((presumableHeaderData[index] & 0xFF) << 16);
				bin[initMarker] = presumableHeaderData[index];
				timestamp |= q ;
				continue;
			}

			if (initMarker == 6) {
				int r = ((presumableHeaderData[index] & 0xFF) << 8);
				bin[initMarker] = presumableHeaderData[index];
				timestamp |= r ;
				continue;
			}
			if (initMarker == 7) {
				int s = ((presumableHeaderData[index] & 0xFF));
				bin[initMarker] = presumableHeaderData[index];
				timestamp |= s  ;
				continue;
			}
			if (initMarker == 8) {

				int p = ((presumableHeaderData[index] & 0xFF) << 24);
				bin[initMarker] = presumableHeaderData[index];
				this.ssrc = p ;
				continue;
			}
			if (initMarker == 9) {

				int q = ((presumableHeaderData[index] & 0xFF) << 16);
				bin[initMarker] = presumableHeaderData[index];
				this.ssrc |= q ;
				continue;
			}
			if (initMarker == 10) {

				int r = ((presumableHeaderData[index] & 0xFF) << 8);
				bin[initMarker] = presumableHeaderData[index];
				this.ssrc |= r ;
				continue;
			}
			if (initMarker == 11) {
				int s = ((presumableHeaderData[index] & 0xFF));
				bin[initMarker] = presumableHeaderData[index];
				this.ssrc |= s ;
				// To much :)
				break;
			}

		}
		int ret = -1;
		if (initMarker == _END_OF_INIT) {

			ret = presumableHeaderData.length - index - 1;

			// return _END_OF_INIT-initMarker;
		} else {
			ret = initMarker-12 ;
			// return presumableHeaderData.length-index;
		}

		return ret;
		// int b = bin[0] & 0xff;
		// version = (b & 0x0C) >> 6;
		// padding = (b & 0x20) == 0x020;
		// extensions = (b & 0x10) == 0x10;
		// cc = b & 0x0F;
		//
		// b = bin[1] & 0xff;
		//
		// marker = (b & 0x80) == 0x80;
		// payloadType = b & 0x7F;
		//
		// seqNumber = (bin[2] & 0xff) << 8;
		// seqNumber = seqNumber | (bin[3] & 0xff);
		//
		// int p = (bin[4] << 24);
		// int q = (bin[5] << 16);
		// int r = (bin[6] << 8);
		// int s = (bin[7]);
		//
		// timestamp = p | q | r | s;
		//		
		// p = (bin[8] << 24);
		// q = (bin[9] << 16);
		// r = (bin[10] << 8);
		// s = (bin[11]);
		//
		// ssrc = p | q | r | s;

	}

	public void init(boolean marker, byte payloadType, int seqNumber, int timestamp, long ssrc) {
		this.payloadType = payloadType;
		this.seqNumber = seqNumber;
		this.timestamp = timestamp;
		this.ssrc = ssrc;
		this.marker = marker;

		bin[0] = (byte) (0x80);

		bin[1] = marker ? (byte) (payloadType | 0x80) : (byte) (payloadType & 0x7f);
		// bin[1] = (payloadType);
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

		this.init(false, (byte) (payloadType & 0x7F), seqNumber, timestamp, ssrc);

	}
}

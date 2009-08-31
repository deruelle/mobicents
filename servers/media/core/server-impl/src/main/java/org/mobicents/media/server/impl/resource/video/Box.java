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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author kulikov
 */
public abstract class Box {

	public static HashMap<byte[], String> bytetoTypeMap = new HashMap<byte[], String>();

	private long size;
	private String type;

	public Box(long size, String type) {
		this.size = size;
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public String getType() {
		return type;
	}

	protected String readType(DataInputStream in) throws IOException {
		byte[] buff = new byte[4];
		for (int i = 0; i < buff.length; i++) {
			buff[i] = in.readByte();
		}
		return new String(buff);
	}

	protected int readLen(DataInputStream in) throws IOException {
		return in.readInt();
	}

	protected String readText(DataInputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte b = 0;
		while ((b = in.readByte()) != 0) {
			bout.write(b);
		}
		return new String(bout.toByteArray(), "UTF-8");
	}

	protected byte[] read(DataInputStream in) throws IOException {
		byte[] buff = new byte[4];
		for (int i = 0; i < buff.length; i++) {
			buff[i] = in.readByte();
		}
		return buff;
	}

	protected int read32(DataInputStream in) throws IOException {
		int output = in.readInt();
		return output;

	}

	protected int read24(DataInputStream fin) throws IOException {
		int output = 0;
		output = (fin.readByte() << 16) | (fin.readByte() << 8) | fin.readByte();
		return output;

	}

	protected int read16(DataInputStream fin) throws IOException {
		int output = 0;
		output = (fin.readByte() << 8) | fin.readByte();
		return output;

	}

	protected boolean comparebytes(byte[] arg1, byte[] arg2) {
		if (arg1.length != arg2.length) {
			return false;
		}
		for (int i = 0; i < arg1.length; i++) {
			if (arg1[i] != arg2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Loads Box from stream.
	 * 
	 * @param fin
	 *            the stream to load box from
	 * @return the number of bytes readed from stream; *
	 * @throws java.io.IOException
	 *             if some I/O error occured.
	 */
	protected abstract int load(DataInputStream fin) throws IOException;
}

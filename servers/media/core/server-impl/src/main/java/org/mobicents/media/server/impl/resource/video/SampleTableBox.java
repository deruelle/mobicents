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
public class SampleTableBox extends Box {

	private SampleDescription stsd;
	private TimeToSampleBox stts;
	private SampleToChunkBox stsc;
	private SampleSizeBox stsz;

	public SampleTableBox(long size, String type) {
		super(size, type);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		int count = 8;
		while (count < getSize()) {
			int len = readLen(fin);
			byte[] type = read(fin);
			if (comparebytes(type, SampleDescription.TYPE)) {
				stsd = new SampleDescription(len);
				count += stsd.load(fin);
			} else if (comparebytes(type, TimeToSampleBox.TYPE)) {
				stts = new TimeToSampleBox(len);
				count += stts.load(fin);
			} else if (comparebytes(type, SampleToChunkBox.TYPE)) {
				stsc = new SampleToChunkBox(len);
				count += stsc.load(fin);
			} else if (comparebytes(type, SampleSizeBox.TYPE)) {
				stsz = new SampleSizeBox(len);
				count += stsz.load(fin);
			} else {
				throw new IOException("Unknown box=" + type);
			}
		}
		return (int) getSize();
	}

}

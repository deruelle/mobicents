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
import java.util.ArrayList;
import java.util.List;

/**
 * The metadata for a presentation is stored in the single Movie Box which occurs at the top-level of a file.
 * 
 * Normally this box is close to the beginning or end of the file, though this is not required.
 * 
 * Box Type: moov Container: File Mandatory: Yes Quantity: Exactly one
 * 
 * @author kulikov
 */
public class MovieBox extends Box {

	// File Type = moov
	static byte[] TYPE = new byte[] { AsciiTable.ALPHA_m, AsciiTable.ALPHA_o, AsciiTable.ALPHA_o, AsciiTable.ALPHA_v };

	private MovieHeaderBox mvhd;
	private List<TrackBox> trackBoxs = new ArrayList<TrackBox>();
	private TrackBox track;

	public MovieBox(long size, String type) {
		super(size, type);
	}

	@Override
	protected int load(DataInputStream in) throws IOException {

		int count = 8;

		// loading movie header
		int len = readLen(in);
		byte[] type = read(in);

		// String type = readType(in);

		if (!comparebytes(type, MovieHeaderBox.TYPE)) {
			throw new IOException("Movie Header Box expected");
		}

		mvhd = new MovieHeaderBox(len);
		count += mvhd.load(in);

		while (count < getSize()) {
			// loading track
			len = readLen(in);
			String type1 = readType(in);

			if (!type1.equals("trak")) {
				throw new IOException("Track box expected");
			}

			track = new TrackBox(len, type1);
			count += track.load(in);
			trackBoxs.add(track);
		}

		return (int) this.getSize();
	}

}

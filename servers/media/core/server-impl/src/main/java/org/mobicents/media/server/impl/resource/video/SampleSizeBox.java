package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

public class SampleSizeBox extends FullBox {

	// File Type = stsz
	static byte[] TYPE = new byte[] { AsciiTable.ALPHA_s, AsciiTable.ALPHA_t, AsciiTable.ALPHA_s, AsciiTable.ALPHA_z };
	static String TYPE_S = "stsz";
	static {
		bytetoTypeMap.put(TYPE, TYPE_S);
	}

	private int sampleSize;
	private int sampleCount;
	private int[] entrySize;

	public SampleSizeBox(long size) {
		super(size, TYPE_S);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);

		sampleSize = fin.readInt();
		sampleCount = fin.readInt();

		if (sampleSize == 0) {
			entrySize = new int[sampleCount];

			for (int i = 0; i < sampleCount; i++) {
				entrySize[i] = fin.readInt();
			}
		}
		return (int) this.getSize();
	}

}

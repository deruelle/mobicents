package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

public class TimeToSampleBox extends FullBox {

	// File Type = stsd
	static byte[] TYPE = new byte[] { AsciiTable.ALPHA_s, AsciiTable.ALPHA_t, AsciiTable.ALPHA_t, AsciiTable.ALPHA_s };
	static String TYPE_S = "stts";
	static {
		bytetoTypeMap.put(TYPE, TYPE_S);
	}

	private int[] sampleCount;
	private int[] sampleDelta;

	public TimeToSampleBox(long size) {
		super(size, TYPE_S);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);

		int entryCount = fin.readInt();

		sampleCount = new int[entryCount];
		sampleDelta = new int[entryCount];
		for (int i = 0; i < entryCount; i++) {
			sampleCount[i] = fin.readInt();
			sampleDelta[i] = fin.readInt();
		}

		return (int) this.getSize();
	}

}

package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * @author amit bhayani
 *
 */
public class ChunkOffsetBox extends FullBox {

	// File Type = stsd
	static byte[] TYPE = new byte[] { AsciiTable.ALPHA_s, AsciiTable.ALPHA_t, AsciiTable.ALPHA_c, AsciiTable.ALPHA_o };
	static String TYPE_S = "stco";
	static {
		bytetoTypeMap.put(TYPE, TYPE_S);
	}

	private int[] chunkOffset;

	public ChunkOffsetBox(long size) {
		super(size, TYPE_S);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);

		int entryCount = fin.readInt();
		chunkOffset = new int[entryCount];
		for (int i = 0; i < entryCount; i++) {
			chunkOffset[i] = fin.readInt();
		}

		return (int) this.getSize();

	}

	public int[] getChunkOffset() {
		return chunkOffset;
	}

}

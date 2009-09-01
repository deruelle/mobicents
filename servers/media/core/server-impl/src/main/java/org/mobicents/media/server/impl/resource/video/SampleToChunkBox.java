package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * @author amit bhayani
 *
 */
public class SampleToChunkBox extends FullBox {

	// File Type = stsc
	static byte[] TYPE = new byte[] { AsciiTable.ALPHA_s, AsciiTable.ALPHA_t, AsciiTable.ALPHA_s, AsciiTable.ALPHA_c };
	static String TYPE_S = "stsc";
	static {
		bytetoTypeMap.put(TYPE, TYPE_S);
	}

	private int[] firstChunk;
	private int[] samplesPerChunk;
	private int[] sampleDescriptionIndex;

	public SampleToChunkBox(long size) {
		super(size, TYPE_S);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);

		int entryCount = fin.readInt();

		firstChunk = new int[entryCount];
		samplesPerChunk = new int[entryCount];
		sampleDescriptionIndex = new int[entryCount];
		for (int i = 0; i < entryCount; i++) {
			firstChunk[i] = fin.readInt();
			samplesPerChunk[i] = fin.readInt();
			sampleDescriptionIndex[i] = fin.readInt();
		}

		return (int) this.getSize();
	}

	public int[] getFirstChunk() {
		return firstChunk;
	}

	public int[] getSamplesPerChunk() {
		return samplesPerChunk;
	}

	public int[] getSampleDescriptionIndex() {
		return sampleDescriptionIndex;
	}

}

package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * @author amit bhayani
 * 
 */
public class VisualSampleEntry extends SampleEntry {

	private int[] predefined = new int[3];

	private int width;
	private int height;
	private double horizresolution;
	private double vertresolution;
	private int frameCount;
	private String compressorname;
	private int depth;

	public VisualSampleEntry(long size, String type) {
		super(size, type);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);
		int count = 8;
		// int(16) pre_defined
		// int(16) reserved
		fin.skip(4);

		predefined[0] = fin.readInt();
		predefined[1] = fin.readInt();
		predefined[2] = fin.readInt();

		width = this.read16(fin);
		height = this.read16(fin);

		return count;
	}

}

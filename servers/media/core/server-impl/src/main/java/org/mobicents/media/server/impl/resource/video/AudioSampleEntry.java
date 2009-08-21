package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * @author amit bhayani
 *
 */
public class AudioSampleEntry extends SampleEntry {

	private int channelCount;
	private int sampleSize;
	private double sampleRate;

	public AudioSampleEntry(long size, String type) {
		super(size, type);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);

		// int[2] reserved
		fin.skip(8);

		channelCount = (fin.readByte() << 8) | fin.readByte();
		sampleSize = (fin.readByte() << 8) | fin.readByte();

		fin.skip(2);

		// reserved
		fin.skip(2);

		int a = fin.readInt();
		sampleRate = (a >> 16) + (a & 0xffff) / 10;
		
		int len = readLen(fin);
		String type = readType(fin);
		if (type.equals("damr")) {			
			AmrSpecificBox amrBox = new AmrSpecificBox(len, type);
			amrBox.load(fin);
			
		}

		return 0;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public double getSampleRate() {
		return sampleRate;
	}

}

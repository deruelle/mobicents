package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * @author amit bhayani
 *
 */
public class SampleDescription extends FullBox {
	
	// File Type = stsd
	static byte[] TYPE = new byte[] { AsciiTable.ALPHA_s, AsciiTable.ALPHA_t, AsciiTable.ALPHA_s, AsciiTable.ALPHA_d };
	static String TYPE_S = "stsd";
	static {
		bytetoTypeMap.put(TYPE, TYPE_S);
	}

	public SampleDescription(long size) {
		super(size, TYPE_S);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);
		int entryCount = readLen(fin);
		
		
		int len1 = readLen(fin);
		String type = readType(fin);
		if (type.equals("samr")) {	
			AudioSampleEntry auSamEntBox = new AudioSampleEntry(len1, type);
			auSamEntBox.load(fin);
			
		}
		
		return (int)getSize();
	}

}

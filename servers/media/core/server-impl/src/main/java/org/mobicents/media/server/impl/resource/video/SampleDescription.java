package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * @author amit bhayani
 *
 */
public class SampleDescription extends FullBox {

	public SampleDescription(long size, String type) {
		super(size, type);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		super.load(fin);
		int len = readLen(fin);
		
		
		int len1 = readLen(fin);
		String type = readType(fin);
		if (type.equals("samr")) {	
			AudioSampleEntry auSamEntBox = new AudioSampleEntry(len1, type);
			auSamEntBox.load(fin);
			
		}
		
		return len;
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.video;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 * @author kulikov
 */
public class SampleEntry extends Box {

	protected byte[] reserved = new byte[6];
	protected short dataReferenceIndex;

	public SampleEntry(long size, String type) {
		super(size, type);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		fin.skip(6);
		dataReferenceIndex = fin.readShort();
		return (int) getSize();
	}

}

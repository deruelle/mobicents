/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.rtp;

import java.io.Serializable;
import java.util.ArrayList;

import org.mobicents.media.Buffer;



/**
 * 
 * @author kulikov
 */
public class BufferFactory implements Serializable,org.mobicents.media.BufferFactory {

	private static int BUFF_SIZE = 320;
	private ArrayList<Buffer> list = new ArrayList<Buffer>();
	private int size;
	private String name = null;

	public BufferFactory(int size, String name, int buffSize) {
		this(size, name);
		this.BUFF_SIZE = buffSize;
	}

	public BufferFactory(int size, String name) {
		this.size = size;
		this.name = name;
		for (int i = 0; i < size; i++) {
			Buffer buffer = new Buffer();
			buffer.setData(new byte[BUFF_SIZE]);
			buffer.setFactory(this);
			list.add(buffer);
		}

	}

	public Buffer allocate(boolean wantRtpHeader) {
		Buffer buffer = this.allocate();
		if (wantRtpHeader) {
			RtpHeader header = new RtpHeader();
			buffer.setHeader(header);
		}
		return buffer;
	}

	public Buffer allocate() {
		Buffer buffer = null;
		if (!list.isEmpty()) {
			buffer = list.remove(0);
		}

		if (buffer != null) {
			buffer.setOffset(0);
			buffer.setLength(0);
			return buffer;
		}

		buffer = new Buffer();
		buffer.setFactory(this);
		buffer.setData(new byte[BUFF_SIZE]);

		return buffer;
	}

	public void deallocate(Buffer buffer) {
		if (list.size() < size && buffer != null && buffer.getData() != null) {
			buffer.setOffset(0);
			buffer.setLength(0);
			buffer.setHeader(null);
			list.add(buffer);
		}
	}
}

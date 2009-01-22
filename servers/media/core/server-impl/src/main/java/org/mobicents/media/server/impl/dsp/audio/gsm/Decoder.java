package org.mobicents.media.server.impl.dsp.audio.gsm;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.dsp.SignalingProcessor;
import org.tritonus.lowlevel.gsm.InvalidGSMFrameException;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Decoder implements Codec {

	private transient Logger logger = Logger.getLogger(Decoder.class);

	private org.tritonus.lowlevel.gsm.GSMDecoder decoder = new org.tritonus.lowlevel.gsm.GSMDecoder();

	private static final int BUFFER_SIZE = 320;

	public Decoder() {
	}


	public void process(Buffer buffer) {
		byte[] data = (byte[]) buffer.getData();

		int offset = buffer.getOffset();
		int length = buffer.getLength();

		byte[] media = new byte[length - offset];

		System.arraycopy(data, 0, media, 0, media.length);

		byte[] res = process(data);

		buffer.setData(res);
		buffer.setFormat(Codec.LINEAR_AUDIO);
		buffer.setOffset(0);
		buffer.setLength(res.length);

	}

	private byte[] m_abBuffer;

	/**
	 * Perform compression.
	 * 
	 * @param input
	 *            media
	 * @return compressed media.
	 */
	public byte[] process(byte[] media) {

		m_abBuffer = new byte[BUFFER_SIZE];

		try {
			decoder.decode(media, 0, m_abBuffer, 0, false);
		} catch (InvalidGSMFrameException e) {
			e.printStackTrace();
		}
		return m_abBuffer;
	}

	public Format getSupportedInputFormat() {
		return Codec.GSM;
	}

	public Format getSupportedOutputFormat() {
		return Codec.LINEAR_AUDIO;
	}

	public void setProc(SignalingProcessor processor) {
		// TODO Auto-generated method stub
		
	}

}

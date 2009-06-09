/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.media.server.impl.resource.dtmf;

import org.apache.log4j.Logger;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.rtp.RtpHeader;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import org.mobicents.media.server.spi.resource.DtmfDetector;

/**
 * 
 * @author Oleg Kulikov
 * @author amit bhayani
 */
public class Rfc2833DetectorImpl extends DtmfBuffer implements DtmfDetector {

	private transient Logger logger = Logger.getLogger(Rfc2833DetectorImpl.class);
	
	public final static Format[] FORMATS = new Format[] { AVProfile.DTMF };

	private String mask = DETECTOR_MASK;
	private int duration = DETECTOR_DURATION;

	public Rfc2833DetectorImpl(String name) {
		super(name);
	}

	public void start() {
	}

	public void stop() {
	}

	public int getDuration() {
		return this.duration;
	}

	public void receive(Buffer buffer) {
		try {
				byte[] data = (byte[]) buffer.getData();
				String digit = TONE[data[0]];
                                push(digit);
                                
		} finally {
			buffer.dispose();
		}
	}

	@Override
	public void disconnect(MediaSource source) {
		super.disconnect(source);
	}

	public Format[] getFormats() {
		return FORMATS;
	}

	public boolean isAcceptable(Format format) {
		return AVProfile.DTMF.matches(format);
	}

}

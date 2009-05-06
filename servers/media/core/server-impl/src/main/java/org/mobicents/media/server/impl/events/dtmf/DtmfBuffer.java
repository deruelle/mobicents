/*
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

package org.mobicents.media.server.impl.events.dtmf;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.AbstractSink;

/**
 * Implements digit buffer.
 * 
 * @author Oleg Kulikov
 */
public abstract class DtmfBuffer extends AbstractSink implements Serializable {
	public final static int TIMEOUT = 5000;
	public final static int SILENCE = 500;

	private StringBuffer buffer = new StringBuffer();
	private String mask = "[0-9, A,B,C,D,*,#]";

	// private DtmfDetector detector;

	private long lastActivity = System.currentTimeMillis();
	private String lastSymbol;

	private transient Logger logger = Logger.getLogger(DtmfBuffer.class);

	public DtmfBuffer(String name) {
		super(name);
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.buffer = new StringBuffer();
		this.mask = mask;
	}

	public void push(String symbol) {
		long now = System.currentTimeMillis();

		if (now - lastActivity > TIMEOUT) {
			buffer = new StringBuffer();
		}

		if (!symbol.equals(lastSymbol) || (now - lastActivity > SILENCE)) {
			buffer.append(symbol);
			lastActivity = now;
			lastSymbol = symbol;
			String digits = buffer.toString();
			if (digits.matches(mask)) {
				// send event;
				if (logger.isDebugEnabled()) {
					logger.debug("Send DTMF event: " + digits);
				}
				
				System.out.println("Send DTMF event: " + digits);

//				DtmfEvent evt = new DtmfEvent(digits);
//				super.sendEvent(evt);

				buffer = new StringBuffer();
			}
		}

	}

}

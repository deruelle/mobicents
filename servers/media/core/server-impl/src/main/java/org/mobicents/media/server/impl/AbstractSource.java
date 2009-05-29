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
package org.mobicents.media.server.impl;

import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;

/**
 * 
 * @author Oleg Kulikov
 */
public abstract class AbstractSource extends BaseComponent implements MediaSource {

	protected transient MediaSink otherParty;
        
	public AbstractSource(String name) {
		super(name);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.MediaStream#connect(MediaSink).
	 */
	public void connect(MediaSink otherParty) {
            AbstractSink sink = (AbstractSink) otherParty;
            if (sink.otherParty == null) {
                sink.otherParty = this;
            }
            
            if (this.otherParty == null) {
                otherParty.connect(this);
            }
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.MediaStream#diconnection(MediaSink).
	 */
	public void disconnect(MediaSink otherParty) {
            AbstractSink sink = (AbstractSink) otherParty;
            if (sink.otherParty != null) {
                sink.otherParty = null;
            }
            
            if (this.otherParty != null) {
                otherParty.disconnect(this);
            }
	}

        public boolean isConnected() {
            return otherParty != null;
        }
        
}

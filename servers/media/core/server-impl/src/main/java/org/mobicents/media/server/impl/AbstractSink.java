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
public abstract class AbstractSink extends BaseComponent implements MediaSink {

	protected transient MediaSource otherParty;

	public AbstractSink(String name) {
		super(name);
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.MediaSink#connect(MediaStream).
	 */
	public void connect(MediaSource otherParty) {
		this.otherParty = otherParty;
		// if (((AbstractSource) otherParty).otherParty != this) {
		if (!((AbstractSource) otherParty).isConnected(this)) {
			otherParty.connect(this);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.MediaSink#disconnect(MediaStream).
	 */
	public void disconnect(MediaSource otherParty) {
		disconnect(otherParty, true);
		
	}

	public void disconnect(MediaSource otherParty, boolean doCallOther)
	{
		if (this.otherParty != null && otherParty != null) {
			
			// ((AbstractSource) otherParty).otherParty = null;
			if (((AbstractSource) otherParty).isConnected(this)) {
				//we need to inform other side so it can perform its task, but let it not call us, we already know of disconnect
				if(doCallOther)
					((AbstractSource) otherParty).disconnect(this,false);
				
					
				this.otherParty = null;
			} else {
				//throw new IllegalArgumentException("Disconnect. Other party does not match. Local: " + this.otherParty + ", passed: " + otherParty);
				//System.err.println("Disconnect["+this+"]. Other party does not match. Local: " + this.otherParty + ", passed: " + otherParty);
			}

		}
	}


	public boolean isConnected(MediaSource component) {
		//System.err.println("siConnected["+this+"] local: "+this.otherParty+", passed: "+component);
		return this.otherParty == component;
	}
}

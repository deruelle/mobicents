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
		this.otherParty = otherParty;
		//if (((AbstractSink) otherParty).otherParty == null) {
		if (!((AbstractSink) otherParty).isConnected(this)){
			
			otherParty.connect(this);
		}
	}

	/**
	 * (Non Java-doc).
	 * 
	 * @see org.mobicents.MediaStream#diconnection(MediaSink).
	 */
	public void disconnect(MediaSink otherParty) {
	
		disconnect(otherParty, true);
	}

	public void disconnect(MediaSink otherParty, boolean doCallOtherParty)
	{
		if (this.otherParty != null && otherParty != null) {
			
			// ((AbstractSink) otherParty).otherParty = null;
			if (((AbstractSink) otherParty).isConnected(this))
			{
				//we need to inform other side so it can perform its task, but let it not call us, we already know of disconnect
				if(doCallOtherParty)
					((AbstractSink) otherParty).disconnect(this,false);
				this.otherParty = null;
				
			}else
			{
				//throw new IllegalArgumentException("Disconnect on["+this+"]. Other party does not match. Local: " + this.otherParty + ", passed: " + otherParty);
				//System.err.println("Disconnect["+this+"]. Other party does not match. Local: " + this.otherParty + ", passed: " + otherParty);
			}
		}
		
	}
	
	@Override
	public boolean equals(Object other) {
		return other == this;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		return hash;
	}

	public boolean isConnected(MediaSink component) {

		return this.otherParty == component;
	}

}

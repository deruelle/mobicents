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

package org.mobicents.media.server.impl.enp.cnf;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.AbstractSource;

/**
 *
 * @author Oleg Kulikov
 */
public class MixerOutput extends AbstractSource {
    protected void push(Buffer buffer) {
    	
        		if(!super.makeReceive(buffer))
        		{
        			//FIXME: baranowb : are we eligible to end this?
        			//It happens in Timer inside AudioMixer
        			logger.info("MixerOutput : failed receive operation :");
    				Thread.currentThread().stop();
        		}
    
    }

    public void start() {
    }

    public void stop() {
    }

    public Format[] getFormats() {
        return AudioMixer.formats;
    }
}

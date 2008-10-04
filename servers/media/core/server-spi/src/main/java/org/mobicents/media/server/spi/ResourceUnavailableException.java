/*
 * ResourceUnavailableException.java
 *
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

package org.mobicents.media.server.spi;

/**
 *
 * @author Oleg Kulikov
 */
public class ResourceUnavailableException extends Exception {
    
    /** Creates a new instance of ResourceUnavailableException 
     * @param e */
    public ResourceUnavailableException(String message, InterruptedException e) {
        super(message);
    }

	public ResourceUnavailableException() {
		super();

	}

	public ResourceUnavailableException(String message, Throwable cause) {
		super(message, cause);

	}

	public ResourceUnavailableException(String message) {
		super(message);

	}

	public ResourceUnavailableException(Throwable cause) {
		super(cause);

	}
    
}

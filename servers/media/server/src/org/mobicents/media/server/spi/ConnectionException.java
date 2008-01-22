/*
 * ConnectionException.java
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
public class ConnectionException extends java.lang.Exception {
    
    private Connection connection;
    
    
    /**
     * Constructs an instance of <code>ConnectionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ConnectionException(Connection connection, String msg) {
        super(msg);
        this.connection = connection;
    }
    
    public ConnectionException(Connection connection, Throwable t) {
        super(t.getMessage());
        this.initCause(t);
        this.connection = connection;
    }
}

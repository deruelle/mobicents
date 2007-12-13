/*
 * File Name     : TimeoutException.java
 *
 * The Java Call Control API for SIP
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
package org.mobicents.jcc.sip;

/**
 *
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 */
public class TimeoutException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>TimeoutException</code> without detail message.
     */
    public TimeoutException() {
    }
    
    
    /**
     * Constructs an instance of <code>TimeoutException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TimeoutException(String msg) {
        super(msg);
    }
}

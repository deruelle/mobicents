/*
 * File Name     : BlockingFilter.java
 *
 * The Java Call Control RA
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


package org.mobicents.slee.resource.jcc.ra;

import javax.csapi.cc.jcc.EventFilter;

import javax.csapi.cc.jcc.JccEvent;
import javax.csapi.cc.jcc.JccCallEvent;
import javax.csapi.cc.jcc.JccConnectionEvent;


/**
 * Implements EventFilter interface wich blocks all non-terminating events.
 * 
 * @author $Author: kulikoff $
 * @version $Revision: 1.1 $
 */
public class BlockingFilter implements EventFilter {
    
    /**
     * Creates a new instance of BlockingFilter 
     */
    public BlockingFilter() {
    }

    public int getEventDisposition(JccEvent event) {
        if (event instanceof JccConnectionEvent) {
            if (event.getID() == JccConnectionEvent.CONNECTION_DISCONNECTED ||
                    (event.getID() == JccConnectionEvent.CONNECTION_FAILED)) {
                return EventFilter.EVENT_NOTIFY;
            } else {
                return EventFilter.EVENT_BLOCK;
            }
        } else {
            if (event.getID() == JccCallEvent.CALL_EVENT_TRANSMISSION_ENDED ||
                    event.getID() == JccCallEvent.CALL_INVALID) {
                return EventFilter.EVENT_NOTIFY;
            } else {
                return EventFilter.EVENT_BLOCK;
            }
        }
    }
    
}

/**
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
package org.mobicents.media.server.spi.events.dtmf;

import org.mobicents.media.server.spi.events.*;

/**
 *
 * @author Oleg Kulikov
 */
public enum DTMFEventID implements EventID {

    DTMF_0("0"),
    DTMF_1("1"),
    DTMF_2("2"),
    DTMF_3("3"),
    DTMF_4("4"),
    DTMF_5("5"),
    DTMF_6("6"),
    DTMF_7("7"),
    DTMF_8("8"),
    DTMF_9("9"),
    DTMF_A("A"),
    DTMF_B("B"),
    DTMF_C("C"),
    DTMF_D("D"),
    DTMF_STAR("*"),
    DTMF_DIGIT_NUM("#"),
    DTMF_SEQ("SEQ"),
    FAILURE("FAILED");
    
    private final static String packageName = "org.mobicents.media.server.evt.dtmf";
    private String eventName;

    private DTMFEventID(String eventName) {
        this.eventName = eventName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getEventName() {
        return eventName;
    }

    @Override
    public String toString() {
        return packageName + "." + eventName;
    }
    
}

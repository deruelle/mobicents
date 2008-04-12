/*
 * Announcement.java
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

package org.mobicents.media.server.spi.events;

/**
 *
 * @author Oleg Kulikov
 */
public interface Announcement {
    public final static int PLAY = 100;
    public final static int COMPLETE = 101;
    public final static int FAIL = 102;
    
    public final static int CAUSE_NORMAL = 0;
    public final static int CAUSE_ANNOUNCEMENT = 1;
    public final static int CAUSE_FACILITY_FAILURE = 2;
    public final static int CAUSE_END_OF_MEDIA = 2;
}

/*
 * MediaContextEventImpl.java
 *
 * The Simple Media API RA
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

package org.mobicents.slee.resource.media.ra;

import org.mobicents.slee.resource.media.ratype.MediaContext;
import org.mobicents.slee.resource.media.ratype.MediaContextEvent;

/**
 *
 * @author Oleg Kulikov
 */
public class MediaContextEventImpl implements MediaContextEvent {
    
    private int cause;
    private MediaContext context;
    
    /** Creates a new instance of MediaContextEventImpl */
    public MediaContextEventImpl(MediaContext context, int cause) {
        this.context = context;
        this.cause = cause;
    }

    public int getCause() {
        return cause;
    }

    public MediaContext getMediaContext() {
        return context;
    }
    
}

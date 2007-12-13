/*
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

import org.mobicents.slee.resource.media.ra.conf.ConfMediaContextImpl;
import org.mobicents.slee.resource.media.ra.ivr.AnnouncementContextImpl;
import org.mobicents.slee.resource.media.ra.ivr.IVRContextImpl;
import org.mobicents.slee.resource.media.ratype.MediaContext;

/**
 *
 * @author Oleg Kulikov
 */
public class MediaContextLocator {
    
    /** Creates a new instance of MediaContextLocator */
    public MediaContextLocator() {
    }
    
    public static synchronized MediaContext getContext(int contextType, MediaResourceAdaptor ra) {
        switch (contextType) {
            case MediaContext.ANNOUNCEMENT :
                return new AnnouncementContextImpl(ra);
            case MediaContext.IVR :
                return new IVRContextImpl(ra);
            case MediaContext.CONFERENCE :
                return new ConfMediaContextImpl(ra);
            default : return null;
        }
    }
}

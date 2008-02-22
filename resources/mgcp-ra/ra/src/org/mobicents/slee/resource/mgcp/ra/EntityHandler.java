/*
 * EntityHandler.java
 *
 * Media Gateway Control Protocol (MGCP) Resource Adaptor.
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

package org.mobicents.slee.resource.mgcp.ra;

import javax.slee.resource.ActivityHandle;

/**
 *
 * @author Oleg Kulikov
 */
public class EntityHandler implements ActivityHandle {
    
    private String entityID;
    
    /** Creates a new instance of EntityHandler */
    public EntityHandler(String entityID) {
        this.entityID = entityID;
    }

    public boolean equals(Object other) {
        return other instanceof String && other.equals(entityID);
    }
    
    public int hasCode() {
        return entityID.hashCode();
    }
}

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

package org.mobicents.media.server.vxml.sbb;

import org.mobicents.media.server.vxml.*;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * A sequence of procedural statements used for prompting and computation, 
 * but not for gathering input. 
 * 
 * A block has a (normally implicit) form item variable that is set to true just 
 * before it is interpreted.
 * 
 * @author Oleg Kulikov
 */
public abstract class BlockSbb extends ItemSbb {
    
    public ChildRelation getRelation(Node node) {
        if (node.getNodeName().equalsIgnoreCase("audio")) {
            return getAudioSbb();
        }
        return null;
    }
    
    public abstract ChildRelation getAudioSbb();
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        this.logger = Logger.getLogger(BlockSbb.class);
    }

    public void unsetSbbContext() {
    }

    public void sbbCreate() throws CreateException {
    }

    public void sbbPostCreate() throws CreateException {
    }

    public void sbbActivate() {
    }

    public void sbbPassivate() {
    }

    public void sbbLoad() {
    }

    public void sbbStore() {
    }

    public void sbbRemove() {
    }

    public void sbbExceptionThrown(Exception arg0, Object arg1, ActivityContextInterface arg2) {
    }

    public void sbbRolledBack(RolledBackContext arg0) {
    }

}

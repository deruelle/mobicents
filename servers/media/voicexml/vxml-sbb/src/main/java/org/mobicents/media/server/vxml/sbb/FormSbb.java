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

import java.util.HashMap;
import org.mobicents.media.server.vxml.*;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * Forms are interpreted by an implicit form interpretation algorithm (FIA). 
 * The FIA has a main loop that repeatedly selects a form item and then visits it.
 * The selected form item is the first in document order whose guard condition 
 * is not satisfied. For instance, a field's default guard condition tests to 
 * see if the field's form item variable has a value, so that if a simple form 
 * contains only fields, the user will be prompted for each field in turn. 
 * Interpreting a form item generally involves:
 * <ul>
 * <li>Selecting and playing one or more prompts;</li>
 * <li>Collecting a user input, either a response that fills in one or more input 
 * items, or a throwing of some event (help, for instance); </li>
 * <li>Interpreting any <filled> actions that pertained to the newly filled in 
 * input items.</li>
 * </ul>
 * The FIA ends when it interprets a transfer of control statement (e.g. a 
 * <goto> to another dialog or document, or a <submit> of data to the 
 * document server). It also ends with an implied <exit> when no form item 
 * remains eligible to select.
 * 
 * @author Oleg Kulikov
 */
public abstract class FormSbb extends ItemSbb {
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#perform(VXMLDocument, VXMLItem, String, Node).
     */
    @Override
    public void perform(VXMLDocument root, VXMLItem parent, String endpoint, Node node) {
        setVariables(new HashMap());
        super.perform(root, parent, endpoint, node);
    }
    
    public ChildRelation getRelation(Node node) {
        if (node.getNodeName().equalsIgnoreCase("block")) {
            return getBlockSbb();
        } else if (node.getNodeName().equalsIgnoreCase("prompt")) {
            return getPromptSbb();
        } else if (node.getNodeName().equalsIgnoreCase("field")) {
            return getFieldSbb();
        } else if (node.getNodeName().equalsIgnoreCase("submit")) {
            return getSubmitSbb();
        }
        return null;
    }
    
    public abstract HashMap getVariables();
    public abstract void setVariables(HashMap variables);
    
    public abstract ChildRelation getBlockSbb();
    public abstract ChildRelation getPromptSbb();
    public abstract ChildRelation getFieldSbb();
    public abstract ChildRelation getSubmitSbb();
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        this.logger = Logger.getLogger(FormSbb.class);
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

    public void sbbRolledBack(RolledBackContext context) {
    }

}

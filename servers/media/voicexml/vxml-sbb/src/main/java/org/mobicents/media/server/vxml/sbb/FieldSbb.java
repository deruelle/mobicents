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

import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import org.apache.log4j.Logger;
import org.mobicents.media.server.vxml.grammar.DTMFGrammar;
import org.mobicents.media.server.vxml.grammar.Grammar;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class FieldSbb extends ItemSbb {

    public ChildRelation getRelation(Node node) {
        if (node.getNodeName().equalsIgnoreCase("prompt")) {
            return getPromptSbb();
        } else if (node.getNodeName().equalsIgnoreCase("")) {
            return null;
        }
        return null;
    }

    public Grammar getGrammar() {
        NodeList childs = getNode().getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeName().equals("grammar")) {
                String url = child.getAttributes().getNamedItem("src").getNodeValue();
                try {
                    Grammar grammar = new DTMFGrammar(url);
                    return grammar;
                } catch (Exception e) {
                    logger.error("Could not parse grammar", e);
                    return null;
                }
            }
        }
        return null;
    }

    public abstract ChildRelation getPromptSbb();

    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        this.logger = Logger.getLogger(FieldSbb.class);
    }

    public void unsetSbbContext() {
        this.sbbContext = null;
        this.logger = null;
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

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

import java.io.Serializable;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import org.mobicents.media.server.vxml.Prompt;
import org.mobicents.media.server.vxml.VXMLDocument;
import org.mobicents.media.server.vxml.VXMLItem;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class MenuSbb implements Sbb {

    private SbbContext sbbContext;
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
    }

    public void unsetSbbContext() {
        this.sbbContext = null;
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#perform(VXMLDocument, VXMLItem, String, Node).
     */
    public void perform(VXMLDocument root, VXMLItem parent, String endpoint, Node node) {
        setRoot(root);
        setParent(parent);
        setEndpoint(endpoint);
        setNode(node);
        
        ChildRelation relation = getPromptSbb();        
        try {
            Prompt prompt = (Prompt) relation.create();
            prompt.perform(root, 
                    (VXMLItem) sbbContext.getSbbLocalObject(), endpoint, getPrompt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#onCompleted(String).
     */
    public void onCompleted(String userInput) {
        String next = getChoice(userInput);
        getRoot().forward(next);
    }
    
    private Node getPrompt() {
        NodeList childs = getNode().getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            if (childs.item(i).getNodeName().equalsIgnoreCase("prompt")) {
                return childs.item(i);
            }
        }
        return null;
    }
    
    private String getChoice(String userInput) {
        NodeList childs = getNode().getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            if (childs.item(i).getNodeName().equalsIgnoreCase("choice")) {
                Choice choice = new Choice(childs.item(i));
                if (userInput.matches(choice.getDTMF())) {
                    return choice.getNext();
                }
            }
        }
        return null;
    }
    
    public abstract Node getNode();
    public abstract void setNode(Node node);
    
    public abstract String getEndpoint();
    public abstract void setEndpoint(String endpoint);
    
    public abstract VXMLDocument getRoot();
    public abstract void setRoot(VXMLDocument parent);

    public abstract VXMLItem getParent();
    public abstract void setParent(VXMLItem parent);
    
    public abstract ChildRelation getPromptSbb();
    
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

    private class Choice implements Serializable {
        private Node node;
        
        public Choice(Node node) {
            this.node = node;
        }
        
        public String getDTMF() {
            NamedNodeMap attributes = node.getAttributes();
            Node dtmf = attributes.getNamedItem("dtmf");
            return dtmf != null ? dtmf.getNodeValue() : null;
        }
        
        public String getNext() {
            NamedNodeMap attributes = node.getAttributes();
            Node next = attributes.getNamedItem("next");
            return next != null ? next.getNodeValue() : null;
        }
        
        public String getValue() {
            return node.getNodeValue();
        }
    }
}

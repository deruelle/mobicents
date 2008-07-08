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
import javax.slee.ChildRelation;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import org.apache.log4j.Logger;
import org.mobicents.media.server.vxml.VXMLDocument;
import org.mobicents.media.server.vxml.VXMLItem;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class ItemSbb implements Sbb {

    protected SbbContext sbbContext;
    protected Logger logger;

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#perform(VXMLDocument, VXMLItem, String, Node).
     */
    public void perform(VXMLDocument root, VXMLItem parent, String endpoint, Node node) {
        setRoot(root);
        setParent(parent);
        setIndex(0);

        setEndpoint(endpoint);
        setNode(node);
        
        if (this.hasMoreItems()) {
            execute(nextItem());
        } else {
            parent.onCompleted(null);
        }
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#onCompleted(String).
     */
    public void onCompleted(String userInput) {
        if (this.hasMoreItems()) {
            execute(this.nextItem());
        } else {
            logger.info("connection=" + getRoot().getConnection().getId() + ", completed");
            getParent().onCompleted(userInput);
        }
    }

    /**
     * Executes specified node.
     * 
     * @param node the node to be executed.
     */
    private void execute(Node node) {
        ChildRelation relation = getRelation(node);
        if (relation == null) {
            logger.warn("Unknown relation[node=" + node.getNodeName() + "], skipped");
            this.onCompleted(null);
        } else {
            logger.info("connection=" + getRoot().getConnection().getId() + 
                    ", executing node=" + node.getNodeName() + ", index=" + getIndex());
            try {
                VXMLItem item = (VXMLItem) relation.create();
                item.perform(getRoot(),
                        (VXMLItem) sbbContext.getSbbLocalObject(),
                        getEndpoint(), node);
            } catch (Exception e) {
                logger.error("Unexpected error", e);
            }
        }
    }

    private boolean hasMoreItems() {
        NodeList childs = getNode().getChildNodes();
        return childs != null && childs.getLength() > 0 && getIndex() < childs.getLength();
    }

    /**
     * Gets the next item to procees.
     * 
     * @return the XML node.
     */
    private Node nextItem() {
        int index = getIndex();
        NodeList childs = getNode().getChildNodes();

        setIndex(index + 1);
        return childs.item(index);
    }

    /**
     * Gets specified variable.
     * 
     * @param the name of the variable.
     * @return specified varibale value.
     */
    public String getValue(String var) {
        return (String) getVariables().get(var);
    }
    
    public abstract ChildRelation getRelation(Node node);

    public abstract Integer getIndex();
    public abstract void setIndex(Integer index);

    public abstract Node getNode();
    public abstract void setNode(Node node);

    public abstract String getEndpoint();
    public abstract void setEndpoint(String endpoint);

    public abstract VXMLDocument getRoot();
    public abstract void setRoot(VXMLDocument parent);

    public abstract VXMLItem getParent();
    public abstract void setParent(VXMLItem parent);

    public abstract HashMap getVariables();
    public abstract void setVariables(HashMap variables);
    
}

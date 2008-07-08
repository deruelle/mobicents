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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import org.apache.log4j.Logger;
import org.mobicents.media.server.vxml.VXMLDocument;
import org.mobicents.media.server.vxml.VXMLItem;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class SubmitSbb extends ItemSbb {

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#perform(VXMLDocument, VXMLItem, String, Node).
     */
    @Override
    public void perform(VXMLDocument root, VXMLItem parent, String endpoint, Node node) {
            logger.info("**** SUBMIT REQUEST ****");
        setRoot(root);
        setParent(parent);
        setIndex(0);

        setEndpoint(endpoint);
        setNode(node);

        try {
            logger.info("**** GETING URI ****");
            URL url = new URL(this.getURI());
            logger.info("****  URI =" + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                logger.info("RELOAD FROM " + url);
                getRoot().load(this.getURI(), is);
            }

        } catch (Exception e) {
            logger.error("ERROR", e);
        }
    }

    /**
     * Gets the list of varibales to submit.
     * 
     * @return array of variable names or null if not present;
     */
    private String[] getNameList() {
        NamedNodeMap attributes = getNode().getAttributes();
        if (attributes != null) {
            Node nameList = attributes.getNamedItem("namelist");
            if (nameList != null) {
                return nameList.getNodeValue().split(" ");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the URI reference.
     * 
     * The URI is always fetched even if it contains just a fragment. 
     * In the case of a fragment, the URI requested is the base URI of the 
     * current document.
     * 
     * @return the URI reference or null if not present.
     */
    private String getURI() {
        NamedNodeMap attributes = getNode().getAttributes();
        Node uri = attributes.getNamedItem("next");

        String URI = uri.getNodeValue();
        System.out.println("1. " + URI);
        if (!URI.startsWith("http")) {
            URI = getRoot().getBaseURL() + URI;
        }
        System.out.println("1.5. " + URI);

        String nameList[] = this.getNameList();
        if (nameList.length == 0) {
            return URI;
        }

        System.out.println("2. " + URI);
        URI = URI + "?" + nameList[0] + "=" + getParent().getValue(nameList[0]);
        for (int i = 0; i < nameList.length; i++) {
            URI += "&" + nameList[i] + "=" + getParent().getValue(nameList[0]);
        }
        System.out.println("3. " + URI);

        return URI;
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#onCompleted(String).
     */
    @Override
    public void onCompleted(String userInput) {
        logger.info("connection=" + getRoot().getConnection().getId() + ", completed");
        getParent().onCompleted(userInput);
    }

    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        this.logger = Logger.getLogger(SubmitSbb.class);
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

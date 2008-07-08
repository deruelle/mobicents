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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.mobicents.mscontrol.MsConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implements root Voice XML document.
 * 
 * VoiceXML is designed for creating audio dialogs that feature synthesized speech, 
 * digitized audio, recognition of spoken and DTMF key input, recording of spoken 
 * input, telephony, and mixed initiative conversations. 
 * 
 * Its major goal is to bring the advantages of Web-based development and 
 * content delivery to interactive voice response applications.
 *
 * @author Oleg Kulikov
 */
public abstract class DocumentSbb implements Sbb {

    private final static String FORM_TAG = "form";
    private final static String MENU_TAG = "menu";
    private final static String VARIABLE_TAG = "var";
    private SbbContext sbbContext;
    private Logger logger = Logger.getLogger(DocumentSbb.class);

    public void start(String url, String endpoint) throws SAXException, ParserConfigurationException, IOException {
        setEndpoint(endpoint);

        if (logger.isDebugEnabled()) {
            logger.debug("Configuring XML parsrer");
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        if (logger.isDebugEnabled()) {
            logger.debug("Parsin XML document [" + url + "]");
        }

        Document document = builder.parse(url);

        HashMap dialogs = new HashMap();

        Element element = document.getDocumentElement();
        NodeList childs = element.getChildNodes();

        String firstDialog = null;
        int count = childs.getLength();

        for (int i = 0; i < count; i++) {
            Node node = childs.item(i);
            if (node.getNodeName().equalsIgnoreCase(FORM_TAG)) {
                dialogs.put(getIdentifier(node), node);
                if (firstDialog == null) {
                    firstDialog = getIdentifier(node);
                }
            } else if (node.getNodeName().equalsIgnoreCase(MENU_TAG)) {
                dialogs.put(getIdentifier(node), node);
                if (firstDialog == null) {
                    firstDialog = getIdentifier(node);
                }
            } else if (node.getNodeName().equalsIgnoreCase(VARIABLE_TAG)) {
            //@todo handle varibles
            }
        }

        //compute and hold base url of the document.
        url = url.replaceAll("http://", "");
        String tokens[] = url.split("/");

        String baseURI = "http://";
        for (int i = 0; i < tokens.length - 1; i++) {
            baseURI += tokens[i] + "/";
        }

        setBaseURI(baseURI);

        // hold dialogs and start interpreter from first dialog.
        setDialogs(dialogs);
        process(firstDialog);
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#onCompleted(String).
     */
    public void onCompleted(String userInput) {
        System.out.println("**** DOCUMENT COMPLETED ****");
    }

    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#forward(String).
     */
    public void forward(String dialogID) {
        this.process(dialogID);
    }

    public String getBaseURL() {
        System.out.println("GET BASE URI");
        return getBaseURI();
    }
    
    public void load(String uri, InputStream is) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            Document document = builder.parse(is);

            HashMap dialogs = new HashMap();

            Element element = document.getDocumentElement();
            NodeList childs = element.getChildNodes();

            String firstDialog = null;
            int count = childs.getLength();

            for (int i = 0; i < count; i++) {
                Node node = childs.item(i);
                if (node.getNodeName().equalsIgnoreCase(FORM_TAG)) {
                    dialogs.put(getIdentifier(node), node);
                    if (firstDialog == null) {
                        firstDialog = getIdentifier(node);
                    }
                } else if (node.getNodeName().equalsIgnoreCase(MENU_TAG)) {
                    dialogs.put(getIdentifier(node), node);
                    if (firstDialog == null) {
                        firstDialog = getIdentifier(node);
                    }
                } else if (node.getNodeName().equalsIgnoreCase(VARIABLE_TAG)) {
                //@todo handle varibles
                }
            }

            //compute and hold base url of the document.
            uri = uri.replaceAll("http://", "");
            String tokens[] = uri.split("/");

            String baseURI = "http://";
            for (int i = 0; i < tokens.length - 1; i++) {
                baseURI += tokens[i] + "/";
            }

            setBaseURI(baseURI);

            // hold dialogs and start interpreter from first dialog.
            setDialogs(dialogs);
            process(firstDialog);
        } catch (Exception e) {
            logger.error("COULD NOT RELOAD ", e);
        }
    }

    public MsConnection getConnection() {
        return (MsConnection) sbbContext.getActivities()[0].getActivity();
    }

    public void process(String dialogID) {
        Node node = (Node) getDialogs().get(dialogID);
        logger.info("Executing dialogID= " + dialogID + ", node=" + node);
        try {
            ChildRelation dialogRelation = getRelation(node.getNodeName());
            VXMLDialog dialog = (VXMLDialog) dialogRelation.create();
            dialog.perform(
                    (VXMLDocument) sbbContext.getSbbLocalObject(),
                    (VXMLItem) sbbContext.getSbbLocalObject(),
                    getEndpoint(), node);
        } catch (CreateException e) {
            logger.error("Unexpected error", e);
        }
    }

    private String getIdentifier(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return "default";
        }

        int count = attributes.getLength();
        for (int i = 0; i < count; i++) {
            Node attribute = attributes.item(i);
            if (attribute.getNodeName().equalsIgnoreCase("id")) {
                return attribute.getNodeValue();
            }
        }

        return "default";
    }

    private ChildRelation getRelation(String nodeName) {
        if (nodeName.equalsIgnoreCase("form")) {
            return getFormSbb();
        } else if (nodeName.equalsIgnoreCase("menu")) {
            return getMenuSbb();
        }
        return null;
    }

    /**
     *  CMP Field
     */
    public abstract HashMap getDialogs();

    public abstract void setDialogs(HashMap dialogs);

    public abstract String getEndpoint();

    public abstract void setEndpoint(String endpoint);

    public abstract String getBaseURI();

    public abstract void setBaseURI(String uri);

    public abstract ChildRelation getFormSbb();

    public abstract ChildRelation getMenuSbb();

    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
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

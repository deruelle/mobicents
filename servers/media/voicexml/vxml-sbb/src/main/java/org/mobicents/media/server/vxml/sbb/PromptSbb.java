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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.common.events.EventCause;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.media.server.vxml.Audio;
import org.mobicents.media.server.vxml.VXMLDocument;
import org.mobicents.media.server.vxml.VXMLField;
import org.mobicents.media.server.vxml.VXMLItem;
import org.mobicents.media.server.vxml.grammar.Grammar;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSignalDetector;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
import org.w3c.dom.Node;

/**
 *
 * @author Oleg Kulikov
 */
public abstract class PromptSbb implements Sbb {

    private SbbContext sbbContext;
    private MsProvider msProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    
    private Logger logger = Logger.getLogger(PromptSbb.class);
    
    /**
     * Executes specified prompt node.
     * 
     * @param root reference to the Voice XML document.
     * @param parent reference to the parent node.
     * @param endpoint the endpoint which will execute node.
     * @param prompt the prompt node.
     */
    public void perform(VXMLDocument root, VXMLItem parent, String endpoint, Node prompt) {
        setRoot(root);
        setParent(parent);
        setEndpoint(endpoint);
        setNode(prompt);
        
        // we are exepected <audio> inside of prompt
        // @todo correct here
        Node node = prompt.getFirstChild();
        
        //run audio first
        ChildRelation relation = getAudioSbb();
        try {
            Audio audio = (Audio) relation.create();
            audio.perform(getRoot(), 
                    (VXMLItem) sbbContext.getSbbLocalObject(), endpoint, node);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
    }
    
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#onCompleted(String).
     */
    public void onCompleted(String userInput) {
        logger.info("Audio completed, enable DTMF detector");
        //audio completed here, activate dtmf detector
        
        Grammar grammar = null;
        if (getParent() instanceof VXMLField) {
             grammar = ((VXMLField)getParent()).getGrammar();
        }
        
        String[] opts = null;
        if (grammar != null) {
            opts = new String[] {grammar.getRootRule()};
        } else {
            opts = new String[] {};
        }
        
        System.out.println("***** opts = " + opts);
        
        MsSignalDetector dtmfDetector = msProvider.getSignalDetector(getEndpoint());
        try {
            ActivityContextInterface dtmfAci = 
                    mediaAcif.getActivityContextInterface(dtmfDetector);
            dtmfAci.attach(sbbContext.getSbbLocalObject());
            dtmfDetector.receive(EventID.DTMF, getRoot().getConnection(), opts);
        } catch (UnrecognizedActivityException e) {
        }
    }
    
    public void onDtmf(MsNotifyEvent evt, ActivityContextInterface aci) {
        logger.info("Catch dtmf tone");
        EventCause cause = null; //= evt.getCause();
        if (cause == EventCause.DTMF_DIGIT_0) {
                setInputValue("0");
        } else if (cause == EventCause.DTMF_DIGIT_1) {
                setInputValue("1");
        } else if (cause == EventCause.DTMF_DIGIT_2) {
                setInputValue("2");
        } else if (cause == EventCause.DTMF_DIGIT_3) {
                setInputValue("3");
        } else if (cause == EventCause.DTMF_DIGIT_4) {
                setInputValue("4");
        } else if (cause == EventCause.DTMF_DIGIT_5) {
                setInputValue("5");
        } else if (cause == EventCause.DTMF_DIGIT_6) {
                setInputValue("6");
        } else if (cause == EventCause.DTMF_DIGIT_7) {
                setInputValue("7");
        } else if (cause == EventCause.DTMF_DIGIT_8) {
                setInputValue("8");
        } else if (cause == EventCause.DTMF_DIGIT_9) {
                setInputValue("9");
        }
        
        getParent().onCompleted(getInputValue());
    }
    
    public abstract Node getNode();
    public abstract void setNode(Node node);
    
    public abstract Integer getIndex();
    public abstract void setIndex(Integer index);

    public abstract String getEndpoint();
    public abstract void setEndpoint(String endpoint);
    
    public abstract VXMLDocument getRoot();
    public abstract void setRoot(VXMLDocument parent);

    public abstract VXMLItem getParent();
    public abstract void setParent(VXMLItem parent);
    
    public abstract String getInputValue();
    public abstract void setInputValue(String value);
    
    public abstract ChildRelation getAudioSbb();
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            msProvider = (MsProvider) ctx.lookup("slee/resources/media/1.0/provider");
            mediaAcif = (MediaRaActivityContextInterfaceFactory) ctx.lookup("slee/resources/media/1.0/acifactory");
        } catch (Exception ne) {
            logger.error("Could not set SBB context:", ne);
        }
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

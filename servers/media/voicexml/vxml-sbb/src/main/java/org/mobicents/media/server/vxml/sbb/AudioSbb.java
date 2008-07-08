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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.UnrecognizedActivityException;
import org.apache.log4j.Logger;
import org.mobicents.media.server.impl.common.events.EventID;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSignalGenerator;
import org.mobicents.slee.resource.media.ratype.MediaRaActivityContextInterfaceFactory;
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
public abstract class AudioSbb implements Sbb {

    private SbbContext sbbContext;
    private MsProvider msProvider;
    private MediaRaActivityContextInterfaceFactory mediaAcif;
    
    private Logger logger = Logger.getLogger(AudioSbb.class);
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#perform(VXMLDocument, VXMLItem, String, Node).
     */
    public void perform(VXMLDocument root, VXMLItem parent, String endpoint, Node node) {
        setParent(parent);
        Node url = node.getFirstChild();
        MsSignalGenerator generator = msProvider.getSignalGenerator(endpoint);
        try {
            ActivityContextInterface generatorActivity = mediaAcif.getActivityContextInterface(generator);
            generatorActivity.attach(sbbContext.getSbbLocalObject());
            logger.info("Executing audio signal, url=" + url.getNodeValue());
            generator.apply(EventID.PLAY, new String[]{url.getNodeValue()});
        } catch (UnrecognizedActivityException e) {
        }
    }
    
    /**
     * (Non Java-doc.)
     * 
     * @see org.mobicents.media.server.vxml.VXMLItem#onCompleted(String).
     */
    public void onCompleted(String userInput) {        
    }
    
    /**
     * This method is called when Media server completes announcement.
     * 
     * @param evt the event object
     * @param aci the announcement activity context.
     */
    public void onAnnouncementComplete(MsNotifyEvent evt, ActivityContextInterface aci) {
        logger.info("announcement complete: return");
        getParent().onCompleted(null);
    }
    
    /**
     * Getter for CMP field.
     * 
     * @return the field value.
     */
    public abstract VXMLItem getParent();
    
    /**
     * Setter for CMP field.
     * 
     * @param parent the field value.
     */
    public abstract void setParent(VXMLItem parent);
    
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

/*
 * GatewaySbb.java
 *
 * Created on 29 Декабрь 2006 г., 18:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.smpp.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.ActivityEndEvent;
import javax.slee.FactoryException;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.CreateException;
import javax.slee.UnrecognizedActivityException;
import javax.slee.serviceactivity.ServiceActivity;
import javax.slee.serviceactivity.ServiceStartedEvent;
import javax.slee.serviceactivity.ServiceActivityFactory;
import net.java.slee.resource.smpp.ClientTransaction;

import net.java.slee.resource.smpp.RequestEvent;
import net.java.slee.resource.smpp.ResponseEvent;
import net.java.slee.resource.smpp.ServerTransaction;
import net.java.slee.resource.smpp.ShortMessage;
import net.java.slee.resource.smpp.SmppProvider;
import net.java.slee.resource.smpp.ActivityContextInterfaceFactory;
import net.java.slee.resource.smpp.Dialog;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.XMPPException;

import org.mobicents.slee.resource.xmpp.XmppResourceAdaptorSbbInterface;
import org.mobicents.slee.resource.xmpp.*;

import org.apache.log4j.Logger;
/**
 *
 * @author Oleg Kulikov
 */
public abstract class GatewaySbb implements Sbb {
    
    private SbbContext sbbContext;
    private XmppResourceAdaptorSbbInterface xmppProvider;
    private SmppProvider smppProvider;
    private ActivityContextInterfaceFactory smppAcif;
    
    private final Class[] packetsToListen = {Message.class};
    private final String connectionID = "org.mobicents.examples.googletalk.GoogleTalkBotSbb";
    private final String username = "msggateway";
    private final String password = "chevycaprice";
    private final String resource = "MobicentsMsgGateway";
    private final String serviceHost = "talk.google.com";
    private final int servicePort = 5222;
    private final String serviceName = "gmail.com";
    
    private Logger logger = Logger.getLogger(GatewaySbb.class);
    
    /** Creates a new instance of SmppExample */
    public GatewaySbb() {
    }
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        try {
            logger.info("Called setSbbContext PtinAudioConf!!!");
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            xmppProvider = (XmppResourceAdaptorSbbInterface) myEnv.lookup("slee/resources/xmpp/2.0/xmppinterface");
            smppProvider = (SmppProvider) myEnv.lookup("slee/resources/smpp/3.4/smppinterface");            
            smppAcif = (ActivityContextInterfaceFactory) myEnv.lookup("slee/resources/smpp/3.4/factoryprovider");
        } catch (NamingException ne) {
            logger.warn("Could not set SBB context:" + ne.getMessage());
        }
    }
    
    public void onStartServiceEvent(ServiceStartedEvent event, ActivityContextInterface aci) {
        try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            //check if it's my service that is starting
            ServiceActivity sa = ((ServiceActivityFactory) myEnv.lookup("slee/serviceactivity/factory")).getActivity();
            if (sa.equals(aci.getActivity())) {
                //connect to google talk xmpp server
                xmppProvider.connectClient(connectionID,serviceHost,
                        servicePort,serviceName,username,password, resource,
                        Arrays.asList(packetsToListen));
            }
        } catch (XMPPException e) {
            System.out.println("Connection to server failed! Error:"+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void onActivityEndEvent(ActivityEndEvent event, ActivityContextInterface aci) {
        try {
            Context myEnv = (Context) new InitialContext().lookup("java:comp/env");
            //check if it's my service aci that is ending
            ServiceActivity sa = ((ServiceActivityFactory) myEnv.lookup("slee/serviceactivity/factory")).getActivity();
            if (sa.equals(aci.getActivity())) {
                xmppProvider.disconnect(connectionID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This is the point where we already have a chat session with the
     * user, so, when they send us messages, we count the chars
     * and reply or tell time :)
     */
    public void onGoogleMessage(Message message, ActivityContextInterface aci) {
        String text = message.getBody();
        
        int splitter = text.indexOf(" ");
        
        String address = text.substring(0, splitter).trim();
        String txt = text.substring(splitter).trim();
        txt = "<" + message.getFrom() + "> " + txt;
        
        Dialog dialog = smppProvider.getDialog(address, "0020");
        ShortMessage sms = dialog.createMessage();
        sms.setText(txt);
        
        ClientTransaction tx = dialog.createSubmitSmTransaction();
        try {
            ActivityContextInterface ac = smppAcif.getActivityContextInterface(tx);
            ac.attach(sbbContext.getSbbLocalObject());
        } catch (FactoryException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (UnrecognizedActivityException ex) {
            ex.printStackTrace();
        }
        
        try {
            tx.send(sms);
            logger.info("Sent message to " + address);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void onSmsMessage(RequestEvent event, ActivityContextInterface aci) {
        logger.info("SMS message received");
        
        ShortMessage smsMessage = event.getMessage();
        String text = smsMessage.getText();
        
        int splitter = text.indexOf(" ");
        
        String address = text.substring(0, splitter).trim();
        String txt = text.substring(splitter).trim();
        txt = "<" + smsMessage.getOriginator() + ">\r\n" + txt;
        
        Message gtMessage = new Message(address, Message.Type.CHAT);
        gtMessage.setBody(txt);
        
        try {
            logger.info("Sending message to " + address);
            xmppProvider.sendPacket(connectionID,gtMessage);
            event.getTransaction().respond(0);
        } catch (IOException e) {
            logger.error("Failed. Caused by", e);
        }
    }

    public void onSmsReport(ResponseEvent event, ActivityContextInterface aci) {
        logger.info("SMS message delivered: " + aci.getActivity());
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
    
    public void sbbExceptionThrown(Exception exception, Object object, ActivityContextInterface activityContextInterface) {
    }
    
    public void sbbRolledBack(RolledBackContext rolledBackContext) {
    }
        
}

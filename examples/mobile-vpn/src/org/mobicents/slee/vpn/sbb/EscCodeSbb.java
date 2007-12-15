/*
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Copyrights:
 *
 *  Copyright - 2005 Internet Technologies, Ltd. All rights reserved.
 *  Volgograd, Russia
 *
 *  This product and related documentation are protected by copyright and
 *  distributed under licenses restricting its use, copying, distribution, and
 *  decompilation. No part of this product or related documentation may be
 *  reproduced in any form by any means without prior written authorization of
 *  ITech and its licensors, if any.
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Author:
 *
 *  Internet Technologies, Ltd.
 *  Volgograd, Russia
 *
 *  Module Name   : GCT API
 *  File Name     : PedSbb.java
 *  Version       : $Revision: 1.1 $
 *
 *  $Log: EscCodeSbb.java,v $
 *  Revision 1.1  2007/09/26 16:49:01  kulikoff
 *  Initial implementation
 *
 *  Revision 1.2  2006/06/29 04:41:58  pavel
 *  modify RouteSbb
 *
 *  Revision 1.1  2006/06/13 12:29:03  pavel
 *  just added
 *
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package org.mobicents.slee.vpn.sbb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.slee.ActivityContextInterface;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbID;
import javax.slee.Address;
import javax.slee.AddressPlan;
import javax.slee.ChildRelation;
import javax.slee.SbbLocalObject;

import javax.slee.usage.UnrecognizedUsageParameterSetNameException;

import javax.slee.profile.ProfileID;
import javax.slee.profile.ProfileFacility;
import javax.slee.profile.UnrecognizedProfileTableNameException;
import javax.slee.profile.UnrecognizedProfileNameException;

import javax.slee.CreateException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.csapi.cc.jcc.JccCall;
import javax.csapi.cc.jcc.JccConnectionEvent;
import javax.csapi.cc.jcc.JccConnection;

import org.mobicents.slee.vpn.profile.MemberProfileCMP;
import org.mobicents.slee.vpn.profile.CosProfileCMP;
import org.mobicents.slee.vpn.profile.PatternProfileCMP;
import org.mobicents.slee.vpn.profile.VpnProfileCMP;

import org.apache.log4j.Logger;

/**
 *
 * @author $Author: kulikoff $
 * @version $Revision: 1.1 $
 */
public abstract class EscCodeSbb implements Sbb {
    
    // reference to the profile facility
    private ProfileFacility profileFacility;
    // the identifier for this sbb
    private SbbID sbbId;
    // the sbb's sbbContext
    private SbbContext sbbContext;
    
    private Logger logger = Logger.getLogger(EscCodeSbb.class);
    
    
    /** Creates a new instance of PedSbb */
    public EscCodeSbb() {
    }
    
    public abstract MemberProfileCMP getMemberProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract ChildRelation getAuthorizationSbbRelation();
    
        
    /**
     * EventHandler method for incoming events of type "InitEvent". InitEvent is
     * defined in the deployment descriptor "sbb-jar.xml".
     * This method is invoked by the SLEE if an event of type INIT is received and fired
     * by the resource adaptor.
     */
    public void onAddressAnalyze(JccConnectionEvent event, ActivityContextInterface ac) {
        JccConnection connection = event.getConnection();
        String destination = connection.getDestinationAddress();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Processing EscCode Sbb");
        }
        
        MemberProfileCMP member = getMember(connection.getAddress().getName());
        boolean enableEscCode = (member.getEscCodeFlag() == null) ? false : member.getEscCodeFlag().booleanValue();
                
        if (enableEscCode && destination.startsWith("90a8")) {
            logger.info(connection.getCall() + " Esc-code enabled and destination starts with Esc-Code magic combination");
            //cut esc code
            destination = destination.substring(4);
            try {
                connection.selectRoute(destination);
            } catch (Exception e) {
                logger.error("Unexpected error",  e);
                release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            }
            return;
        }
        
        logger.info(connection.getCall() + " forwarding to authorization");
        try {
            SbbLocalObject authSbb = getAuthorizationSbbRelation().create();
            ac.attach(authSbb);
        } catch (Exception e) {
            logger.error("Unexpected error", e);            
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
        }                        
    }
    
    
    protected void release(JccConnection connection, int cause) {
        try {
            connection.release(cause);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
    }
    
    private MemberProfileCMP getMember(String msidn) {
        Address address = new Address(AddressPlan.E164_MOBILE, msidn);
        try {
            ProfileID profileID = profileFacility.getProfileByIndexedAttribute("VpnMemberProfile", "address", address);        
            return getMemberProfile(profileID);
        } catch (Exception e) {
            logger.error("Could not find member, caused by", e);
            return null;
        }  
    }
    
    public void sbbActivate() {
    }
    public void sbbCreate() throws CreateException {
    }
    public void sbbExceptionThrown(Exception exception, Object obj, ActivityContextInterface activityContextInterface) {
    }
    public void sbbLoad() {
    }
    public void sbbPassivate() {
    }
    public void sbbPostCreate() throws CreateException {
    }
    public void sbbRemove() {
    }
    public void sbbRolledBack(RolledBackContext rolledBackContext) {
    }
    public void sbbStore() {
    }
    
    public void setSbbContext(SbbContext sbbContext) {
        this.sbbContext = sbbContext;
        sbbId = sbbContext.getSbb();
        try {
            Context ctx = (Context) new InitialContext().lookup("java:comp/env");
            profileFacility = (ProfileFacility) ctx.lookup("slee/facilities/profile");
        } catch (NamingException ne) {
            System.out.println("Could not set SBB context: " + ne.toString());
        }        
    }
    
    public void unsetSbbContext() {
    }
    
    
}

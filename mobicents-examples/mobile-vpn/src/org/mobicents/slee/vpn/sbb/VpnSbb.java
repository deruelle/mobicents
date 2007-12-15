/**
 * Mobile Virtual Private Network service.
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
 * @author Oleg Kulikov
 */

public abstract class VpnSbb  implements Sbb {
    
    // reference to the profile facility
    private ProfileFacility profileFacility;
    // the identifier for this sbb
    private SbbID sbbId;
    // the sbb's sbbContext
    private SbbContext sbbContext;
    
    private Logger logger = Logger.getLogger(VpnSbb.class);
    
    
    
    /** Creates a new instance of VpbxSbb */
    public VpnSbb() {
    }
    
    /**
     * EventHandler method for incoming events of type "InitEvent". InitEvent is
     * defined in the deployment descriptor "sbb-jar.xml".
     * This method is invoked by the SLEE if an event of type INIT is received and fired
     * by the resource adaptor.
     */
    public void onAuthorizeCallAttempt(JccConnectionEvent event, ActivityContextInterface ac) {
        JccConnection connection = event.getConnection();
        logger.info("Incoming call[" + connection.getCall() + "] from " + 
                connection.getOriginatingAddress() + " to " + connection.getAddress());        
        
        Address address = new Address(
                AddressPlan.E164_MOBILE,
                connection.getAddress().getName());
        
        ProfileID profileID = null;
        try {            
            logger.info(connection.getCall() + " Looking for member by address: " + address);
            profileID = profileFacility.getProfileByIndexedAttribute(
                    "VpnMemberProfile", "address", address);
        } catch (Exception ex) {
            logger.error("Unexpected error, release call", ex);
            this.release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
        if (profileID == null) {
            logger.warn(connection.getCall() + " Non VPN member. Release");
            release(connection, JccConnectionEvent.CAUSE_INCOMPATIBLE_DESTINATION);
            return;
        }
        
        MemberProfileCMP member = null;
        try {
            member = getMemberProfile(profileID);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
                        
        // Create Authorization Sbb
        try {
            logger.info(connection.getCall() + " forwarding call to authorization");
            SbbLocalObject authSbb = getAuthorizationSbbRelation().create();
            ac.attach(authSbb);
        } catch (Exception e) {
            logger.error("Could not create Authorization Sbb, caused by", e);    
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
        }                
    }
    
    /**
     * EventHandler method for incoming events of type "InitEvent". InitEvent is
     * defined in the deployment descriptor "sbb-jar.xml".
     * This method is invoked by the SLEE if an event of type INIT is received and fired
     * by the resource adaptor.
     */
    public void onAddressAnalyze(JccConnectionEvent event, ActivityContextInterface ac) {
        JccConnection connection = event.getConnection();
        String destination = connection.getDestinationAddress();
        
        logger.info("Outgoing call[ " + connection.getCall() + "] from " + 
                connection.getAddress() + " to " + destination);
        
        //@todo obtain address plan from jcc address
        Address address = new Address(AddressPlan.E164_MOBILE,
                connection.getAddress().getName());
        
        ProfileID profileID = null;
        try {            
            logger.info(connection.getCall() + " Looking for member by address: " + address);
            profileID = profileFacility.getProfileByIndexedAttribute(
                    "VpnMemberProfile", "address", address);
        } catch (Exception ex) {
            logger.error("Unexpected error, release call", ex);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
        if (profileID == null) {
            logger.warn(connection.getCall() + " Non VPN member. Release");
            release(connection, JccConnectionEvent.CAUSE_INCOMPATIBLE_DESTINATION);
            return;
        }
        
        MemberProfileCMP member = null;
        try {
            member = getMemberProfile(profileID);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
                
        // Create Esc-code Sbb
        try {
            logger.info(connection.getCall() + " Checking for Esc-Code");
            SbbLocalObject escCodeSbb = getEscCodeSbbRelation().create();
            ac.attach(escCodeSbb);
        } catch (Exception e) {
            logger.error("Unexpected error", e);    
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
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
        
    public abstract MemberProfileCMP getMemberProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
        
    public abstract ChildRelation getEscCodeSbbRelation();
    public abstract ChildRelation getAuthorizationSbbRelation();
       
    protected void release(JccConnection connection, int cause) {
        try {
            connection.release(cause);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }
    }
        
}

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
public abstract class BlackListSbb implements Sbb {
    // reference to the profile facility
    private ProfileFacility profileFacility;
    // the identifier for this sbb
    private SbbID sbbId;
    // the sbb's sbbContext
    private SbbContext sbbContext;
    
    private Logger logger = Logger.getLogger(BlackListSbb.class);
    
    private final static int INCOMING = 0;
    private final static int OUTGOING = 1;
    
    
    /** Creates a new instance of BlackListSbb */
    public BlackListSbb() {
    }
    
    public abstract VpnProfileCMP getVirtualNetworkProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract MemberProfileCMP getMemberProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract PatternProfileCMP getPatternProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract ChildRelation getScreeningSbbRelation();
    
    
    /**
     * EventHandler method for incoming events of type "InitEvent". InitEvent is
     * defined in the deployment descriptor "sbb-jar.xml".
     * This method is invoked by the SLEE if an event of type INIT is received and fired
     * by the resource adaptor.
     */
    public void onAuthorizeCallAttempt(JccConnectionEvent event, ActivityContextInterface ac) {
        JccConnection connection = event.getConnection();
        String originating = connection.getOriginatingAddress().getName();
        
        MemberProfileCMP member = getMember(connection.getAddress().getName());
        ProfileID[] profiles = member.getBlackList();
        
        if (profiles != null) {
            for (int i = 0; i < profiles.length; i++) {
                ProfileID profile = profiles[i];
                if (profile != null) {
                    PatternProfileCMP pattern;
                    try {
                        pattern = getPatternProfile(profile);
                        if (matches(originating, pattern, INCOMING))  {
                            logger.info(connection.getCall() + ", Subscriber=" + member +
                                    ", Call restricted by rule: " + pattern);
                            release(connection, JccConnectionEvent.CAUSE_CALL_RESTRICTED);
                            return;
                        }
                    } catch (UnrecognizedProfileTableNameException ex) {
                    } catch (UnrecognizedProfileNameException ex) {
                    }
                }
            }
        }
        
        try {
            logger.info(connection.getCall() + ", Subscriber=" + member +
                    ", Call allowed,  forwarding to Screening");
            SbbLocalObject screeningSbb = getScreeningSbbRelation().create();
            ac.attach(screeningSbb);
        } catch (Exception e) {
            logger.error("Unexpected", e);
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
        
        MemberProfileCMP member = getMember(connection.getAddress().getName());
        ProfileID[] profiles = member.getBlackList();
        
        if (profiles != null) {
            VpnProfileCMP vpn = null;
            try {
                vpn = getVirtualNetworkProfile(new ProfileID("VirtualNetworkProfile", member.getNetworkID()));
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
                return;
            }
            
            boolean usePrefix = (vpn.getPrefixFlag() == null) ? false : vpn.getPrefixFlag().booleanValue();
            if (usePrefix && destination.startsWith("a")) {
                destination = destination.substring(1);
            }
            
            for (int i = 0; i < profiles.length; i++) {
                ProfileID profile = profiles[i];
                if (profile != null) {
                    PatternProfileCMP pattern;
                    try {
                        pattern = getPatternProfile(profile);
                        if (matches(destination, pattern, OUTGOING))  {
                            logger.info(connection.getCall() + ", Subscriber=" + member +
                                    ", Call restricted by rule: " + pattern);
                            release(connection, JccConnectionEvent.CAUSE_CALL_RESTRICTED);
                            return;
                        }
                    } catch (UnrecognizedProfileTableNameException ex) {
                    } catch (UnrecognizedProfileNameException ex) {
                    }
                }
            }
        }
        
        try {
            logger.info(connection.getCall() + ", Subscriber=" + member +
                    ", Call allowed,  forwarding to Screening");
            SbbLocalObject screeningSbb = getScreeningSbbRelation().create();
            ac.attach(screeningSbb);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
        }
        
    }
    
    public String getRegExpPattern(String pattern) {
        String str = pattern.replaceAll("x", "[\\\\d]");
        str = str.replaceAll("\\*", "[\\\\w]*");
        return str;
    }
    
    
    private boolean matches(String destination, PatternProfileCMP pattern, int direction) {
        String regexp = getRegExpPattern(pattern.getAddress().getAddressString());
        return destination.matches(regexp) && (direction == pattern.getDirection().intValue());
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

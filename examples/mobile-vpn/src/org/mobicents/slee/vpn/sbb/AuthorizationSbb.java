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
public abstract class AuthorizationSbb implements Sbb {
    
    // reference to the profile facility
    private ProfileFacility profileFacility;
    // the identifier for this sbb
    private SbbID sbbId;
    // the sbb's sbbContext
    private SbbContext sbbContext;
    
    private Logger logger = Logger.getLogger(AuthorizationSbb.class);
    
    private final static int INCOMING = 0;
    private final static int OUTGOING = 1;
    
    
    /** Creates a new instance of AuthorizationSbb */
    public AuthorizationSbb() {
    }
    
    public abstract VpnProfileCMP getVirtualNetworkProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract MemberProfileCMP getMemberProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract CosProfileCMP getCosProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract PatternProfileCMP getPatternProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract ChildRelation getBlackListSbbRelation();
    public abstract ChildRelation getWhiteListSbbRelation();
    
    
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
        String netID = member.getNetworkID();
        
        ProfileID profileID = member.getCos();
        if (profileID == null) {
            logger.info(connection.getCall() + " Subscriber = " + member +
                    " Class of service is undefined. Default instruction=allow");
            try {
                connection.continueProcessing();
            } catch (Exception e) {
            }
            return;
        }
        
        CosProfileCMP cos = null;
        try {
            cos = getCosProfile(profileID);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
        ProfileID[] profiles = cos.getAuthorizationPatterns();
        if (profiles == null || profiles.length == 0) {
            logger.info(connection.getCall() + ", Subscriber=" + member +
                    ", Authorization list empty. Default instruction=deny, Checking white list");
            try {
                SbbLocalObject whiteListSbb = getWhiteListSbbRelation().create();
                ac.attach(whiteListSbb);
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            }
            return;
        }
        
        boolean isAuthorized = false;
        for (int i = 0; i < profiles.length; i++) {
            ProfileID profile = profiles[i];
            if (profile != null) {
                try {
                    PatternProfileCMP pattern = getPatternProfile(profiles[i]);
                    if (authorized(originating, pattern, INCOMING)) {
                        isAuthorized = true;
                        break;
                    }
                } catch (Exception e) {
                    logger.error("Unexpected error", e);
                    release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
                    return;
                }
            }
        }
        
        logger.info(connection.getCall() + ", Subscriber=" + member +
                " Authorized=" + isAuthorized);
        try {
            if (isAuthorized) {
                logger.info(connection.getCall() + ", Subscriber=" + member +
                        " Checking black list");
                SbbLocalObject blackListSbb = getBlackListSbbRelation().create();
                ac.attach(blackListSbb);
            } else {
                logger.info(connection.getCall() + ", Subscriber=" + member +
                        " Checking white list");
                SbbLocalObject whiteListSbb = getWhiteListSbbRelation().create();
                ac.attach(whiteListSbb);
            }
        } catch (Exception e) {
            logger.error("Unexpected error", e);
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
        
        // Try to find in the local VPN profiles
        try {
            String address = findByLocalAddress(member, destination);
            if (address != null) {
                //ON-NET CALL
                connection.selectRoute(address);
            }
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            return;
        }
        
        ProfileID profileID = member.getCos();
        if (profileID == null) {
            logger.info(connection.getCall() + " Subscriber = " + member +
                    " Class of service is undefined. Default instruction=allow");
            try {
                connection.selectRoute(destination);
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            }
            return;
        }
        
        CosProfileCMP cos = null;
        try {
            cos = getCosProfile(profileID);
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
        ProfileID[] profiles = cos.getAuthorizationPatterns();
        
        if (profiles == null || profiles.length == 0) {
            logger.info(connection.getCall() + ", Subscriber=" + member +
                    ", Authorization list empty. Default instruction=deny, Checking white list");
            try {
                SbbLocalObject whiteListSbb = getWhiteListSbbRelation().create();
                ac.attach(whiteListSbb);
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            }
            return;
        }
        
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
        
        boolean isAuthorized = false;
        
        for (int i = 0; i < profiles.length; i++) {
            ProfileID profile = profiles[i];
            if (profile != null) {
                try {
                    PatternProfileCMP pattern = getPatternProfile(profiles[i]);
                    if (authorized(destination, pattern, OUTGOING )) {
                        isAuthorized = true;
                        break;
                    }
                } catch (Exception e) {
                    logger.error("Unexpected error", e);
                    release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
                    return;
                }
            }
        }
        
        logger.info(connection.getCall() + ", Subscriber=" + member +
                " Authorized=" + isAuthorized);
        try {
            if (isAuthorized) {
                logger.info(connection.getCall() + ", Subscriber=" + member +
                        " Checking black list");
                SbbLocalObject blackListSbb = getBlackListSbbRelation().create();
                ac.attach(blackListSbb);
            } else {
                logger.info(connection.getCall() + ", Subscriber=" + member +
                        " Checking white list");
                SbbLocalObject whiteListSbb = getWhiteListSbbRelation().create();
                ac.attach(whiteListSbb);
            }
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
    
    private boolean authorized(String destination, PatternProfileCMP pattern, int direction) {
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
    
    private String findByLocalAddress(MemberProfileCMP original, String destination) throws Exception {
        VpnProfileCMP  vpn = getVirtualNetworkProfile(new ProfileID("VirtualNetworkProfile", original.getNetworkID()));
        ProfileID[] members = vpn.getMembers();
        
        for (int i = 0; i < members.length; i++) {
            MemberProfileCMP member = getMemberProfile(members[i]);
            String localAddress = member.getAddress().getSubAddressString();
            boolean usePrefix = (vpn.getPrefixFlag() == null) ? false : vpn.getPrefixFlag().booleanValue();
            
            if (usePrefix) {
                localAddress = "a" + localAddress;
            }
            
            if (destination.equals(localAddress)) {
                return member.getAddress().getAddressString();
            }
        }
        
        return null;
    }
    
    private boolean isOnNet(MemberProfileCMP member, String address) {
        VpnProfileCMP vpn = null;
        try {
            vpn = getVirtualNetworkProfile(
                    new ProfileID("VirtualNetworkProfile", member.getNetworkID()));
        } catch (Exception e) {
            return false;
        }
        
        ProfileID[] profiles = vpn.getMembers();
        
        for (int i = 0; i < profiles.length; i++) {
            try {
                MemberProfileCMP another = getMemberProfile(profiles[i]);
                String original = another.getAddress().getAddressString();
                if (original.equals(address)) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
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

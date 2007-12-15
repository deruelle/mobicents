/*
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
import java.util.Date;
import java.util.Calendar;

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
public abstract class RouteSbb implements Sbb {
    // reference to the profile facility
    private ProfileFacility profileFacility;
    // the identifier for this sbb
    private SbbID sbbId;
    // the sbb's sbbContext
    private SbbContext sbbContext;
    
    private Logger logger = Logger.getLogger(ScreeningSbb.class);
    
    private final static int INCOMING = 0;
    private final static int OUTGOING = 1;
    
    
    /** Creates a new instance of RouteSbb */
    public RouteSbb() {
    }
    
    public abstract VpnProfileCMP getVirtualNetworkProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract MemberProfileCMP getMemberProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract CosProfileCMP getCosProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract PatternProfileCMP getPatternProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
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
        
        CosProfileCMP cos = null;
        try {
            cos = getCosProfile(member.getCos());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
        ProfileID[] profiles = cos.getRoutePatterns();
        if (profiles != null) {
            ArrayList patterns = null;
            try {
                patterns = getPatterns(profiles, INCOMING);
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
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
            
            // replace getAddress().getName() to originating
            Pattern routePattern = findNearest(originating, patterns);
            if (routePattern != null) {
                try {
                    // replace getAddress().getName() to originating
                    String result = routePattern.getResult(originating);
                    if (isOnNet(member, result)) {
                        connection.selectRoute(result);
                    } else if (isVirtualOnNet(vpn, result, INCOMING)) {
                        connection.selectRoute(result);
                    } else {
                        release(connection, JccConnectionEvent.CAUSE_CALL_RESTRICTED);
                    }
                    return;
                } catch (Exception e) {
                    logger.error("Unexpected error", e);
                    release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
                    return;
                }
            }
        }
        
        try {
            connection.continueProcessing();
        } catch (Exception e) {
            logger.error("Unxpected error", e);
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
        
        CosProfileCMP cos = null;
        try {
            cos = getCosProfile(member.getCos());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
        ProfileID[] profiles = cos.getRoutePatterns();
        boolean prefix = false;
        
        if (profiles != null) {
            ArrayList patterns = null;
            try {
                patterns = getPatterns(profiles, OUTGOING);
            } catch (Exception e) {
                logger.error("Unexpected error", e);
                release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
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
                prefix = true;
            }
            
            Pattern routePattern = findNearest(destination, patterns);
            
            if (routePattern != null) {
                try {
                    String result = routePattern.getResult(destination);
                    
                    if (isOnNet(member, result)) {
                        connection.selectRoute(result);
                    } else if (isVirtualOnNet(vpn, result, OUTGOING)) {
                        connection.selectRoute(result);
                    } else {
                        release(connection, JccConnectionEvent.CAUSE_CALL_RESTRICTED);
                    }
                    return;
                } catch (Exception e) {
                    logger.error("Unexpected error", e);
                    release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
                    return;
                }
            }
        }
        
        if (prefix) {
            release(connection, JccConnectionEvent.CAUSE_CALL_RESTRICTED);
            return;
        }
        
        try {
            connection.selectRoute(destination);
        } catch (Exception e) {
            logger.error("Unxpected error", e);
        }
    }
    
    private ArrayList getPatterns(ProfileID[] profiles, int direction) throws Exception {
        ArrayList patterns = new ArrayList();
        for (int i = 0; i < profiles.length; i++) {
            PatternProfileCMP pattern = getPatternProfile(profiles[i]);
            if (pattern.getDirection().intValue() == direction) {
                String expression = pattern.getAddress().getAddressString();
                patterns.add(new Pattern(expression));
            }
        }
        return patterns;
    }
    
    private Pattern findNearest(String digits, ArrayList routes) {
        Collections.sort(routes);
        Iterator list = routes.iterator();
        
        while (list.hasNext()) {
            Pattern pattern = (Pattern) list.next();
            if (pattern.matches(digits)) {
                return pattern;
            }
        }
        return null;
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
    
    private boolean isVirtualOnNet(VpnProfileCMP vpn, String destination, int direction) {
        ProfileID[] patterns = vpn.getVirtualOnNetPatterns();
        
        if (patterns == null || patterns.length == 0) {
            return false;
        }
        
        for (int i = 0; i < patterns.length; i++) {
            PatternProfileCMP pattern = null;
            
            try {
                pattern = getPatternProfile(patterns[i]);
            } catch (Exception e) {
                return false;
            }
            
            String regexp = getRegExpPattern(pattern.getAddress().getAddressString());
            if (destination.matches(regexp) &&
                    pattern.getDirection().intValue() == direction) {
                return true;
            }
        }
        return false;
    }
    
    private String getRegExpPattern(String pattern) {
        String str = pattern.replaceAll("x", "[\\\\d]");
        str = str.replaceAll("\\*", "[\\\\w]*");
        return str;
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

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
 * @author Oleg Kulikov
 */
public abstract class ScreeningSbb implements Sbb {
    
    // reference to the profile facility
    private ProfileFacility profileFacility;
    // the identifier for this sbb
    private SbbID sbbId;
    // the sbb's sbbContext
    private SbbContext sbbContext;
    
    private Logger logger = Logger.getLogger(ScreeningSbb.class);
    
    private final static int INCOMING = 0;
    private final static int OUTGOING = 1;
    
    
    /** Creates a new instance of ScreeningSbb */
    public ScreeningSbb() {
    }
    
    public abstract VpnProfileCMP getVirtualNetworkProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract MemberProfileCMP getMemberProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract CosProfileCMP getCosProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract PatternProfileCMP getPatternProfile(ProfileID id)
    throws UnrecognizedProfileTableNameException, UnrecognizedProfileNameException;
    
    public abstract ChildRelation getRouteSbbRelation();
    
    
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
        
        ProfileID[] profiles = cos.getScreeningPatterns();
        if (profiles != null) {
            for (int i = 0; i < profiles.length; i++) {
                if (isDeny(profiles[i], originating, INCOMING)) {
                    logger.info(connection.getCall() + ", Subscriber=" + member +
                            ", Call is restricted:" + profiles[i]);
                    release(connection, JccConnectionEvent.CAUSE_CALL_RESTRICTED);
                    return;
                }
            }
        }
        
        try {
            logger.info(connection.getCall() + ", Subscriber=" + member +
                    ", Call is allowed. Forwarding to Router");
            SbbLocalObject routeSbb = getRouteSbbRelation().create();
            ac.attach(routeSbb);
        } catch(Exception e) {
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
        CosProfileCMP cos = null;
        try {
            cos = getCosProfile(member.getCos());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
            return;
        }
        
        ProfileID[] profiles = cos.getScreeningPatterns();
        
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
                if (isDeny(profiles[i], destination, OUTGOING)) {
                    logger.info(connection.getCall() + ", Subscriber=" + member +
                            ", Call is restricted:" + profiles[i]);
                    release(connection, JccConnectionEvent.CAUSE_CALL_RESTRICTED);
                    return;
                }
            }
        }
        
        try {
            logger.info(connection.getCall() + ", Subscriber=" + member +
                    ", Call is allowed. Forwarding to Router");
            SbbLocalObject routeSbb = getRouteSbbRelation().create();
            ac.attach(routeSbb);
        } catch(Exception e) {
            logger.error("Unexpected error", e);
            release(connection, JccConnectionEvent.CAUSE_GENERAL_FAILURE);
        }
    }
    
    private boolean isDeny(ProfileID profile, String destination, int direction) {
        PatternProfileCMP pattern = null;
        try {
            pattern = getPatternProfile(profile);
        } catch (Exception e) {
            logger.error("System failure on getPatternProfile, caused by", e);
            return true;
        }
        
        String regexp = getRegExpPattern(pattern.getAddress().getAddressString());
        
        if(direction != pattern.getDirection().intValue()) {
            return false;
        }
        
        if (!destination.matches(regexp)) {
            return false;
        }
        
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        
        if (pattern.getDayOfWeek().intValue() != -1) {
            if (pattern.getDayOfWeek().intValue() != day) {
                return false;
            }
        }
        
        Date low = getTime(pattern.getStartTime());
        Date high = getTime(pattern.getFinishTime());
        
        if(!low.before(calendar.getTime())) {
            return false;
        }
        
        if(!high.after(calendar.getTime())) {
            return false;
        }
        
        return true;
    }
    
    
    private Date getTime(Date time) {
        Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        // it is not correct
        calendar.setTimeInMillis(time.getTime());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, date);
        
        return calendar.getTime();
    }
    
    public String getRegExpPattern(String pattern) {
        String str = pattern.replaceAll("x", "[\\\\d]");
        str = str.replaceAll("\\*", "[\\\\w]*");
        return str;
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

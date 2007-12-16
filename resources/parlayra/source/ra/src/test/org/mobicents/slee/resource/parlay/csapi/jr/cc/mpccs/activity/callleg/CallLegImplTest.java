package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.TpAddress;
import org.csapi.cc.mpccs.IpCallLeg;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpCallLegActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCallImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManagerImpl;
import org.mobicents.slee.resource.parlay.fw.ServiceAndToken;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;

/**
 * 
 * Class Description for CallLegImplTest
 */
public class CallLegImplTest extends TestCase {
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        ipCallLegControl = MockControl.createControl(IpCallLeg.class);
        
        ipCallLeg = (IpCallLeg) ipCallLegControl.getMock();
        
        mpccsListenerControl = MockControl.createControl(MpccsListener.class);
        
        mpccsListener = (MpccsListener) mpccsListenerControl.getMock();
        
        activityManagerControl = MockControl.createControl(ActivityManager.class);
        
        activityManager = (ActivityManager) activityManagerControl.getMock();
        
        ServiceAndToken serviceAndToken = new ServiceAndToken(null, "TOKEN");
        session = new MultiPartyCallControlManagerImpl(serviceIdentifier, null,
                null, activityManager, mpccsListener);
        
        multiPartyCall = new MultiPartyCallImpl(
                session,
                new org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier(
                        MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID),
                        null, 9, activityManager, mpccsListener, null);
        
        callLegImpl = new CallLegImpl(session, multiPartyCall,
                callLegIdentifier, ipCallLeg,
                MpccsTestData.CALL_LEG_SESSION_ID, activityManager, mpccsListener);
    }
    
    
    
    IpCallLeg ipCallLeg;
    
    MockControl ipCallLegControl;
    
    MultiPartyCallControlManager session;
    
    MultiPartyCall multiPartyCall;
    
    TpCallLegIdentifier callLegIdentifier = new TpCallLegIdentifier(1,
            MpccsTestData.CALL_LEG_SESSION_ID);
    
    final TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(1);
    
    MpccsListener mpccsListener;
    
    MockControl mpccsListenerControl;
    
    ActivityManager activityManager;
    
    MockControl activityManagerControl;
    
    CallLegImpl callLegImpl;
    
    public void testInit() {
        
        
        activityManagerControl.replay();
        
        ipCallLegControl.replay();
        
        callLegImpl.init();
        
        activityManagerControl.verify();
        
        ipCallLegControl.verify();
    }
    
    public void testGetTpCallLegIdentifier() {
        assertEquals(callLegIdentifier, callLegImpl.getTpCallLegIdentifier());
    }
    
    public void testGetIpCallLeg() {
        assertEquals(ipCallLeg, callLegImpl.getIpCallLeg());
    }
    
    
    
    public void testGetMpccsSession() {
        assertEquals(session, callLegImpl.getMpccsSession());
    }
    
    public void testRouteReq() throws Exception {
        
        
        ipCallLeg.routeReq(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_ADDRESS, MpccsTestData.TP_ADDRESS,
                MpccsTestData.TP_CALL_APP_INFO_ARRAY,
                MpccsTestData.TP_CALL_LEG_CONNECTION_PROPERTIES);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl.routeReq(MpccsTestData.TP_ADDRESS,
                MpccsTestData.TP_ADDRESS,
                MpccsTestData.TP_CALL_APP_INFO_ARRAY,
                MpccsTestData.TP_CALL_LEG_CONNECTION_PROPERTIES);
        
        
        ipCallLegControl.verify();
    }
    
    public void testEventReportReq() throws Exception {
        
        
        ipCallLeg.eventReportReq(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl
        .eventReportReq(MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY);
        
        
        ipCallLegControl.verify();
    }
    
    public void testRelease() throws Exception {
        
        
        ipCallLeg.release(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_RELEASE_CAUSE);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl.release(MpccsTestData.TP_RELEASE_CAUSE);
        
        
        ipCallLegControl.verify();
    }
    
    public void testGetInfoReq() throws Exception{
        
        
        ipCallLeg.getInfoReq(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.CALL_INFO_REQUESTED);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl.getInfoReq(MpccsTestData.CALL_INFO_REQUESTED);
        
        
        ipCallLegControl.verify();
    }
    
    public void testGetCall() throws Exception{
        
        
        ipCallLeg.getCall(MpccsTestData.CALL_LEG_SESSION_ID);
        ipCallLegControl.setReturnValue(MpccsTestData.TP_CALL_IDENTIFIER);
        
        
        ipCallLegControl.replay();
        
        
        org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier callIdentifier = callLegImpl
        .getCall();
        
        assertEquals(MpccsTestData.TP_CALL_IDENTIFIER.CallSessionID,
                callIdentifier.getCallSessionID());
        assertEquals(MpccsTestData.CALL_ID, callIdentifier
                .getCallRefID());
        
        ipCallLegControl.verify();
    }
    
    public void testAttachMediaReq() throws Exception{
        
        
        ipCallLeg.attachMediaReq(MpccsTestData.CALL_LEG_SESSION_ID);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl.attachMediaReq();
        
        
        ipCallLegControl.verify();
    }
    
    public void testDetachMediaReq() throws Exception{
        
        
        ipCallLeg.detachMediaReq(MpccsTestData.CALL_LEG_SESSION_ID);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl.detachMediaReq();
        
        
        ipCallLegControl.verify();
    }
    
    public void testGetCurrentDestinationAddress() throws Exception{
        
        
        ipCallLeg
        .getCurrentDestinationAddress(MpccsTestData.CALL_LEG_SESSION_ID);
        ipCallLegControl.setReturnValue(MpccsTestData.TP_ADDRESS);
        
        
        ipCallLegControl.replay();
        
        
        TpAddress address = callLegImpl.getCurrentDestinationAddress();
        
        assertEquals(MpccsTestData.TP_ADDRESS, address);
        
        
        ipCallLegControl.verify();
    }
    
    public void testContinueProcessing() throws Exception{
        
        ipCallLeg.continueProcessing(MpccsTestData.CALL_LEG_SESSION_ID);
        
        ipCallLegControl.replay();
        
        
        callLegImpl.continueProcessing();
        
        
        ipCallLegControl.verify();
    }
    
    public void testSetChargePlan() throws Exception{
        
        ipCallLeg.setChargePlan(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_CHARGE_PLAN);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl.setChargePlan(MpccsTestData.TP_CALL_CHARGE_PLAN);
        
        
        
        ipCallLegControl.verify();
    }
    
    public void testSetAdviceOfCharge() throws Exception{
        
        ipCallLeg.setAdviceOfCharge(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_AOC_INFO, MpccsTestData.TARIFF_SWITCH);
        
        
        ipCallLegControl.replay();
        
        callLegImpl.setAdviceOfCharge(MpccsTestData.TP_AOC_INFO,
                MpccsTestData.TARIFF_SWITCH);
        
        
        ipCallLegControl.verify();
    }
    
    public void testSuperviseReq() throws Exception{
        
        ipCallLeg.superviseReq(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TIME, MpccsTestData.TREATMENT);
        
        
        ipCallLegControl.replay();
        
        
        callLegImpl.superviseReq(MpccsTestData.TIME,
                MpccsTestData.TREATMENT);
        
        
        ipCallLegControl.verify();
    }
    
    public void testDeassign() throws Exception{
        
        ipCallLeg.deassign(MpccsTestData.CALL_LEG_SESSION_ID);
        
        activityManager.remove(new TpCallLegActivityHandle(callLegIdentifier), callLegIdentifier);
        
        activityManager.activityEnding(new TpCallLegActivityHandle(callLegIdentifier));
        
        activityManagerControl.replay();
        
        ipCallLegControl.replay();
        
        
        callLegImpl.deassign();
        
        
        ipCallLegControl.verify();
        
        activityManagerControl.verify();
    }
    
    public void testEventReportRes() throws Exception {
        
        mpccsListener.onEventReportResEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.eventReportRes(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_EVENT_INFO);
        
        mpccsListenerControl.verify();
    }
    
    public void testEventReportErr() {
        
        mpccsListener.onEventReportErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.eventReportErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);
        
        mpccsListenerControl.verify();
    }
    
    public void testAttachMediaRes() {
        
        mpccsListener.onAttachMediaResEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.attachMediaRes(MpccsTestData.ASSIGNMENT_ID);
    }
    
    public void testAttachMediaErr() {
        
        mpccsListener.onAttachMediaErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.attachMediaErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);
    }
    
    public void testDetachMediaRes() {
        
        mpccsListener.onDetachMediaResEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.detachMediaRes(MpccsTestData.ASSIGNMENT_ID);
    }
    
    public void testDetachMediaErr() {
        
        mpccsListener.onDetachMediaErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.detachMediaErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);
    }
    
    public void testGetInfoRes() {
        
        mpccsListener.onGetInfoResEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.getInfoRes(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_LEG_INFO_REPORT);
    }
    
    public void testGetInfoErr() {
        
        mpccsListener.onGetInfoErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.getInfoErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);
    }
    
    public void testRouteErr() {
        
        mpccsListener.onRouteErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.routeErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);
    }
    
    public void testSuperviseRes() {
        
        mpccsListener.onSuperviseResEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.superviseRes(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.REPORT, MpccsTestData.DURATION);
    }
    
    public void testSuperviseErr() {
        
        mpccsListener.onSuperviseErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.superviseErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);
    }
    
    public void testCallLegEnded() {
        
        mpccsListener.onCallLegEndedEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();
        
        callLegImpl.callLegEnded(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_RELEASE_CAUSE);
    }
    
    public void testDispose() {
        callLegImpl.dispose();
        
        assertNull(callLegImpl.getIpCallLeg());
    }
    
    public void testCloseConnection() throws ResourceException {
        
        callLegImpl.closeConnection();
        
    }
    
}
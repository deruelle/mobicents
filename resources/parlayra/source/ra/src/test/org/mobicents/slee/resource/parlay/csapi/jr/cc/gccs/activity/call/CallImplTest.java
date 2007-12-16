
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.gccs.IpCall;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.gccs.CallEndedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallFaultDetectedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.RouteErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.RouteResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.CallImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;

/**
 *
 **/
public class CallImplTest extends TestCase {

    MockControl callControlManagerControl;
    
    CallControlManager mockCallControlManager;
    
    MockControl ipCallControl;
    
    IpCall mockIpCall;
    
    MockControl activityManagerControl;
    
    ActivityManager mockActivityManager;
    
    MockControl gccsListenerControl;
    
    GccsListener mockGccsListener;
    
    CallImpl callImpl;
    
    TpCallIdentifier callIdentifier;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        
        callControlManagerControl = MockControl.createControl(CallControlManager.class);
        
        mockCallControlManager = (CallControlManager)callControlManagerControl.getMock(); 
        
        ipCallControl = MockControl.createControl(IpCall.class);
        
        mockIpCall = (IpCall)ipCallControl.getMock();
        
        activityManagerControl = MockControl.createControl(ActivityManager.class);
        
        mockActivityManager = (ActivityManager)activityManagerControl.getMock();
        
        gccsListenerControl = MockControl.createControl(GccsListener.class);
        
        mockGccsListener = (GccsListener)gccsListenerControl.getMock();
        
        callIdentifier = new TpCallIdentifier(GccsTestData.CALL_REF_ID, GccsTestData.SESSIONID);
        callImpl = new CallImpl(mockCallControlManager, mockIpCall, GccsTestData.SESSIONID, callIdentifier,  mockActivityManager, mockGccsListener);
    }

    public void testDispose() {
        callControlManagerControl.reset();
        mockCallControlManager.removeCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(GccsTestData.CALL);
        
        callControlManagerControl.replay();
        callImpl.dispose();
        
        callControlManagerControl.verify();
    }

    public void testRouteRes() {
        
        mockGccsListener.onRouteResEvent(new RouteResEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.EVENTREPORT, GccsTestData.CALL_LEG_SESSIONID));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.routeRes(GccsTestData.SESSIONID, GccsTestData.EVENTREPORT, GccsTestData.CALL_LEG_SESSIONID);
        gccsListenerControl.verify();
    }

    public void testRouteErr() {
        mockGccsListener.onRouteErrEvent(new RouteErrEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.TP_CALL_ERROR, GccsTestData.CALL_LEG_SESSIONID));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.routeErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR, GccsTestData.CALL_LEG_SESSIONID);
        gccsListenerControl.verify();
    }

    public void testGetCallInfoRes() {
        mockGccsListener.onGetCallInfoResEvent(new GetCallInfoResEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.TP_CALL_INFO_REPORT));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.getCallInfoRes(GccsTestData.SESSIONID, GccsTestData.TP_CALL_INFO_REPORT);
        gccsListenerControl.verify();
    }

    public void testGetCallInfoErr() {
        mockGccsListener.onGetCallInfoErrEvent(new GetCallInfoErrEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.TP_CALL_ERROR));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.getCallInfoErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        gccsListenerControl.verify();
    }

    public void testSuperviseCallRes() {
        mockGccsListener.onSuperviseCallResEvent(new SuperviseCallResEvent(new TpServiceIdentifier(1), callIdentifier, 1, 1));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.superviseCallRes(GccsTestData.SESSIONID, 1, 1);
        gccsListenerControl.verify();
    }

    public void testSuperviseCallErr() {
        mockGccsListener.onSuperviseCallErrEvent(new SuperviseCallErrEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.TP_CALL_ERROR));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.superviseCallErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        gccsListenerControl.verify();
    }

    public void testCallFaultDetected() {
        mockGccsListener.onCallFaultDetectedEvent(new CallFaultDetectedEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.TP_CALL_FAULT));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        
        mockActivityManager.remove(null, null);
        activityManagerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        mockActivityManager.activityEnding(null);
        activityManagerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        activityManagerControl.replay();
        mockCallControlManager.getTpServiceIdentifier();
        callControlManagerControl.setReturnValue(new TpServiceIdentifier(1));
        mockCallControlManager.removeCall(callIdentifier.getCallSessionID());
        callControlManagerControl.setReturnValue(callImpl);
        callControlManagerControl.replay();

        callImpl.callFaultDetected(GccsTestData.SESSIONID, GccsTestData.TP_CALL_FAULT);
        gccsListenerControl.verify();
        activityManagerControl.verify();
        callControlManagerControl.verify();
    }

    public void testGetMoreDialledDigitsRes() {
        mockGccsListener.onGetMoreDialledDigitsResEvent(new GetMoreDialledDigitsResEvent(new TpServiceIdentifier(1), callIdentifier, "10"));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();

        callImpl.getMoreDialledDigitsRes(GccsTestData.SESSIONID, "10");
        gccsListenerControl.verify();

    }

    public void testGetMoreDialledDigitsErr() {
        mockGccsListener.onGetMoreDialledDigitsErrEvent(new GetMoreDialledDigitsErrEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.TP_CALL_ERROR));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.getMoreDialledDigitsErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        gccsListenerControl.verify();
    }

    public void testCallEnded() {
        mockGccsListener.onCallEndedEvent(new CallEndedEvent(new TpServiceIdentifier(1), callIdentifier, GccsTestData.TP_CALL_ENDED_REPORT));
        gccsListenerControl.setMatcher(MockControl.ALWAYS_MATCHER);
        gccsListenerControl.replay();
        callImpl.callEnded(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ENDED_REPORT);
        gccsListenerControl.verify();
    }

    public void testRouteReq() throws P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE, TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID, P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA, ResourceException {

        mockIpCall.routeReq(GccsTestData.SESSIONID, GccsTestData.CALL_REPORT_REQUEST_ARRAY, GccsTestData.TP_ADDRESS, GccsTestData.TP_ADDRESS, GccsTestData.TP_ADDRESS, GccsTestData.TP_ADDRESS, GccsTestData.TP_CALL_APP_INFO_ARRAY);
        ipCallControl.setReturnValue(GccsTestData.SESSIONID);
        ipCallControl.replay();
        
        assertEquals(GccsTestData.SESSIONID, callImpl.routeReq(GccsTestData.CALL_REPORT_REQUEST_ARRAY, 
                GccsTestData.TP_ADDRESS, 
                GccsTestData.TP_ADDRESS, 
                GccsTestData.TP_ADDRESS, 
                GccsTestData.TP_ADDRESS, 
                GccsTestData.TP_CALL_APP_INFO_ARRAY));
        ipCallControl.verify();

    }

    public void testRelease() throws TpCommonExceptions, P_INVALID_NETWORK_STATE, ResourceException, P_INVALID_SESSION_ID {

        mockIpCall.release(GccsTestData.SESSIONID, GccsTestData.TP_CALL_RELEASE_CAUSE);
        ipCallControl.replay();
        callImpl.release(GccsTestData.TP_CALL_RELEASE_CAUSE);
        ipCallControl.verify();

    }

    public void testDeassignCall() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {

        mockIpCall.deassignCall(GccsTestData.SESSIONID);
        ipCallControl.replay();
        callImpl.deassignCall();
        ipCallControl.verify();

    }

    public void testGetCallInfoReq() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {

        mockIpCall.getCallInfoReq(GccsTestData.SESSIONID, GccsTestData.CALL_INFO_REQUESTED);
        ipCallControl.replay();
        callImpl.getCallInfoReq(GccsTestData.CALL_INFO_REQUESTED);
        ipCallControl.verify();

    }

    public void testSetCallChargePlan() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {

        mockIpCall.setCallChargePlan(GccsTestData.SESSIONID, GccsTestData.TP_CALL_CHARGE_PLAN);
        ipCallControl.replay();
        callImpl.setCallChargePlan(GccsTestData.TP_CALL_CHARGE_PLAN);
        ipCallControl.verify();
    }

    public void testSetAdviceOfCharge() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {
            mockIpCall.setAdviceOfCharge(GccsTestData.SESSIONID, GccsTestData.TP_AOC_INFO, 1);
            ipCallControl.replay();
            callImpl.setAdviceOfCharge(GccsTestData.TP_AOC_INFO, 1);
            ipCallControl.verify();
    }

    public void testGetMoreDialledDigitsReq() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {
        mockIpCall.getMoreDialledDigitsReq(GccsTestData.SESSIONID, 100);
        ipCallControl.replay();
        callImpl.getMoreDialledDigitsReq(100);
        ipCallControl.verify();
    }

    public void testSuperviseCallReq() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {
        mockIpCall.superviseCallReq(GccsTestData.SESSIONID, 100, 1);
        ipCallControl.replay();
        callImpl.superviseCallReq(100, 1);
        ipCallControl.verify();
    }

    public void testContinueProcessing() throws P_INVALID_NETWORK_STATE, TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {
        mockIpCall.continueProcessing(GccsTestData.SESSIONID);
        ipCallControl.replay();
        callImpl.continueProcessing();
        ipCallControl.verify();
    }

    public void testGetIpCall() {

        assertEquals(mockIpCall, callImpl.getIpCall());
    }

    public void testGetTpCallIdentifier() {
        assertEquals(callIdentifier, callImpl.getTpCallIdentifier());
    }

}

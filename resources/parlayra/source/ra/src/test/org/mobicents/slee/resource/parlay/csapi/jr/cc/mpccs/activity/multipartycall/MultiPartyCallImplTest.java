package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_AMOUNT;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_CURRENCY;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_INVALID_SESSION_ID;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.mpccs.IpCallLeg;
import org.csapi.cc.mpccs.IpMultiPartyCall;
import org.csapi.cc.mpccs.TpCallLegIdentifier;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsTestData;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.TpMultiPartyCallActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLegImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLegStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCallImpl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManagerImpl;
import org.mobicents.slee.resource.parlay.fw.ServiceAndToken;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 * 
 * Class Description for MultiPartyCallImplTest
 */
public class MultiPartyCallImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        ipMultiPartyCallControl = MockControl
                .createControl(IpMultiPartyCall.class);

        ipMultiPartyCall = (IpMultiPartyCall) ipMultiPartyCallControl.getMock();

        mpccsListenerControl = MockControl.createControl(MpccsListener.class);

        mpccsListener = (MpccsListener) mpccsListenerControl.getMock();

        activityManagerControl = MockControl
                .createControl(ActivityManager.class);

        activityManager = (ActivityManager) activityManagerControl.getMock();

        ServiceAndToken serviceAndToken = new ServiceAndToken(null, "TOKEN");
        session = new MultiPartyCallControlManagerImpl(serviceIdentifier, null,
                null, activityManager, mpccsListener);

        Executor[] executors = new Executor[] { new QueuedExecutor() };

        multiPartyCallImpl = new MultiPartyCallImpl(session,
                MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER, ipMultiPartyCall,
                MpccsTestData.CALL_SESSION_ID, activityManager, mpccsListener,
                executors) {

            protected void activateIpAppCallLeg() {
            }
        };
    }

    public void testMultiPartyCallImpl() {
    }

    IpMultiPartyCall ipMultiPartyCall;

    MockControl ipMultiPartyCallControl;

    MultiPartyCallControlManager session;

    final TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(1);

    MpccsListener mpccsListener;

    MockControl mpccsListenerControl;

    ActivityManager activityManager;

    MockControl activityManagerControl;

    MultiPartyCallImpl multiPartyCallImpl;

    public void testInit() {

        activityManagerControl.replay();
        
//        try {
//            ipMultiPartyCall.setCallbackWithSessionID(null, MpccsTestData.CALL_SESSION_ID);
//            ipMultiPartyCallControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
            ipMultiPartyCallControl.replay();
//        }
//        catch (UserException e) {
//            e.printStackTrace();
//            fail();
//        }

        multiPartyCallImpl.init();
        
        activityManagerControl.verify();
        ipMultiPartyCallControl.verify();
    }

    public void testGetTpMultiPartyCallIdentifier() {
        assertEquals(MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER,
                multiPartyCallImpl.getTpMultiPartyCallIdentifier());
    }

 

    public void testAddCallLeg() {
        CallLeg callLeg = new CallLegImpl(session, multiPartyCallImpl, null,
                null, MpccsTestData.CALL_LEG_SESSION_ID, activityManager, null);

        multiPartyCallImpl.addCallLeg(MpccsTestData.CALL_LEG_SESSION_ID,
                callLeg);

        assertEquals(callLeg, multiPartyCallImpl
                .getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID));

        assertEquals(callLeg, multiPartyCallImpl
                .removeCallLeg(MpccsTestData.CALL_LEG_SESSION_ID));

        assertNull(multiPartyCallImpl
                .getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID));
    }

    public void testGetIpMultiPartyCall() {
        assertEquals(ipMultiPartyCall, multiPartyCallImpl.getIpMultiPartyCall());
    }
 

    public void testGetCallLegs() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {
        TpCallLegIdentifier[] callLegIdentifiers = new TpCallLegIdentifier[] {};

            ipMultiPartyCall.getCallLegs(MpccsTestData.CALL_SESSION_ID);
            ipMultiPartyCallControl.setReturnValue(callLegIdentifiers);


        ipMultiPartyCallControl.replay();


            org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier[] result = multiPartyCallImpl
                    .getCallLegs();

            assertEquals(callLegIdentifiers.length, result.length);

            for (int i = 0; i < result.length; i++) {
                assertEquals(callLegIdentifiers[i].CallLegSessionID, result[i]
                        .getCallLegSessionID());
            }


        ipMultiPartyCallControl.verify();
    }

    public void testCreateCallLeg() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {

        MockControl ipCallLegControl = MockControl
                .createControl(IpCallLeg.class);

        IpCallLeg ipCallLeg = (IpCallLeg) ipCallLegControl.getMock();

        TpCallLegIdentifier callLegIdentifier = new TpCallLegIdentifier(
                ipCallLeg, MpccsTestData.CALL_LEG_SESSION_ID);


            ipMultiPartyCall.createCallLeg(MpccsTestData.CALL_SESSION_ID, null);
            ipMultiPartyCallControl.setReturnValue(callLegIdentifier);

            //ipCallLeg.setCallbackWithSessionID(multiPartyCallImpl
            //        .getIpAppCallLeg(), MpccsTestData.CALL_LEG_SESSION_ID);


        ipMultiPartyCallControl.replay();
        ipCallLegControl.replay();


            org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier result = multiPartyCallImpl
                    .createCallLeg();

            assertEquals(callLegIdentifier.CallLegSessionID, result
                    .getCallLegSessionID());

            assertNotNull(multiPartyCallImpl.getCallLeg(result
                    .getCallLegSessionID()));


        ipMultiPartyCallControl.verify();
        ipCallLegControl.verify();
    }

    public void testCreateAndRouteCallLegReq() throws P_INVALID_INTERFACE_TYPE, P_INVALID_EVENT_TYPE, P_INVALID_NETWORK_STATE, TpCommonExceptions, P_INVALID_ADDRESS, P_INVALID_SESSION_ID, P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_CRITERIA, ResourceException {

        MockControl ipCallLegControl = MockControl
                .createControl(IpCallLeg.class);

        IpCallLeg ipCallLeg = (IpCallLeg) ipCallLegControl.getMock();

        TpCallLegIdentifier callLegIdentifier = new TpCallLegIdentifier(
                ipCallLeg, MpccsTestData.CALL_LEG_SESSION_ID);


            ipMultiPartyCall.createAndRouteCallLegReq(
                    MpccsTestData.CALL_SESSION_ID,
                    MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY,
                    MpccsTestData.TP_ADDRESS, MpccsTestData.TP_ADDRESS,
                    MpccsTestData.TP_CALL_APP_INFO_ARRAY, multiPartyCallImpl
                            .getIpAppCallLeg());
            ipMultiPartyCallControl.setReturnValue(callLegIdentifier);

            //ipCallLeg.setCallbackWithSessionID(multiPartyCallImpl
            //        .getIpAppCallLeg(), MpccsTestData.CALL_LEG_SESSION_ID);


        ipMultiPartyCallControl.replay();
        ipCallLegControl.replay();


            org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier result = multiPartyCallImpl
                    .createAndRouteCallLegReq(
                            MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY,
                            MpccsTestData.TP_ADDRESS, MpccsTestData.TP_ADDRESS,
                            MpccsTestData.TP_CALL_APP_INFO_ARRAY);

            assertEquals(callLegIdentifier.CallLegSessionID, result
                    .getCallLegSessionID());

            assertNotNull(multiPartyCallImpl.getCallLeg(result
                    .getCallLegSessionID()));


        ipMultiPartyCallControl.verify();
        ipCallLegControl.verify();
    }

    public void testRelease() throws P_INVALID_NETWORK_STATE, TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {


            ipMultiPartyCall.release(MpccsTestData.CALL_SESSION_ID,
                    MpccsTestData.TP_RELEASE_CAUSE);


        ipMultiPartyCallControl.replay();


            multiPartyCallImpl.release(MpccsTestData.TP_RELEASE_CAUSE);


        ipMultiPartyCallControl.verify();
    }

    public void testDeassignCall() throws TpCommonExceptions, ResourceException, P_INVALID_SESSION_ID {


            ipMultiPartyCall.deassignCall(MpccsTestData.CALL_SESSION_ID);

            activityManager.remove(new TpMultiPartyCallActivityHandle(
                    MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER),
                    MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER);

            activityManager.activityEnding(new TpMultiPartyCallActivityHandle(
                    MpccsTestData.TP_SLEE_MP_CALL_IDENTIFIER));


        activityManagerControl.replay();

        ipMultiPartyCallControl.replay();


            multiPartyCallImpl.deassignCall();
       

        activityManagerControl.verify();

        ipMultiPartyCallControl.verify();
    }

    public void testGetInfoReq() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {


            ipMultiPartyCall.getInfoReq(MpccsTestData.CALL_SESSION_ID,
                    MpccsTestData.CALL_INFO_REQUESTED);


        ipMultiPartyCallControl.replay();


            multiPartyCallImpl.getInfoReq(MpccsTestData.CALL_INFO_REQUESTED);


        ipMultiPartyCallControl.verify();
    }

    public void testSetChargePlan() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {


            ipMultiPartyCall.setChargePlan(MpccsTestData.CALL_SESSION_ID,
                    MpccsTestData.TP_CALL_CHARGE_PLAN);


        ipMultiPartyCallControl.replay();


            multiPartyCallImpl.setChargePlan(MpccsTestData.TP_CALL_CHARGE_PLAN);


        ipMultiPartyCallControl.verify();
    }

    public void testSetAdviceOfCharge() throws P_INVALID_AMOUNT, P_INVALID_CURRENCY, TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {


            ipMultiPartyCall.setAdviceOfCharge(MpccsTestData.CALL_SESSION_ID,
                    MpccsTestData.TP_AOC_INFO, MpccsTestData.TARIFF_SWITCH);


        ipMultiPartyCallControl.replay();


            multiPartyCallImpl.setAdviceOfCharge(MpccsTestData.TP_AOC_INFO,
                    MpccsTestData.TARIFF_SWITCH);


        ipMultiPartyCallControl.verify();
    }

    public void testSuperviseReq() throws TpCommonExceptions, P_INVALID_SESSION_ID, ResourceException {


            ipMultiPartyCall.superviseReq(MpccsTestData.CALL_SESSION_ID,
                    MpccsTestData.TIME, MpccsTestData.TREATMENT);


        ipMultiPartyCallControl.replay();


            multiPartyCallImpl.superviseReq(MpccsTestData.TIME,
                    MpccsTestData.TREATMENT);


        ipMultiPartyCallControl.verify();
    }

    public void testGetInfoRes() {

        mpccsListener.onGetInfoResEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallImpl.getInfoRes(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_INFO_REPORT);

        mpccsListenerControl.verify();
    }

    public void testGetInfoErr() {

        mpccsListener.onGetInfoErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallImpl.getInfoErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);

        mpccsListenerControl.verify();
    }

    public void testSuperviseRes() {

        mpccsListener.onSuperviseResEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallImpl.superviseRes(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.REPORT, MpccsTestData.DURATION);

        mpccsListenerControl.verify();
    }

    public void testSuperviseErr() {

        mpccsListener.onSuperviseErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallImpl.superviseErr(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ERROR);

        mpccsListenerControl.verify();
    }

    public void testCallEnded() {

        mpccsListener.onCallEndedEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallImpl.callEnded(MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_ENDED_REPORT);

        mpccsListenerControl.verify();
    }

    public void testCreateAndRouteCallLegErr() {

        // Set up a call leg for the error
        multiPartyCallImpl.addCallLeg(MpccsTestData.CALL_LEG_SESSION_ID,
                new CallLegStub());

        mpccsListener.onCreateAndRouteCallLegErrEvent(null);
        mpccsListenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mpccsListenerControl.replay();

        multiPartyCallImpl.createAndRouteCallLegErr(
                MpccsTestData.ASSIGNMENT_ID,
                MpccsTestData.TP_CALL_LEG_IDENTIFIER,
                MpccsTestData.TP_CALL_ERROR);

        mpccsListenerControl.verify();
    }

    public void testDispose() {
        MockControl callLegControl = MockControl.createControl(CallLeg.class);
        CallLeg callLeg = (CallLeg) callLegControl.getMock();

        callLeg.dispose();

        callLegControl.replay();

        multiPartyCallImpl.addCallLeg(MpccsTestData.CALL_LEG_SESSION_ID,
                callLeg);

        multiPartyCallImpl.dispose();

        assertNull(multiPartyCallImpl.getIpMultiPartyCall());

        callLegControl.verify();
    }

    public void testCloseConnection() throws ResourceException {

            multiPartyCallImpl.closeConnection();

    }

    public void testGetActivityHandle() {
        assertEquals(MpccsTestData.callActivityHandle, multiPartyCallImpl
                .getActivityHandle());
    }

    public void testGetIpCallLegConnection() throws ResourceException {
        // Set up a call leg for the connection
        multiPartyCallImpl.addCallLeg(MpccsTestData.CALL_LEG_SESSION_ID,
                new CallLegStub());


            assertNotNull(multiPartyCallImpl
                    .getIpCallLegConnection(MpccsTestData.TP_SLEE_CALL_LEG_IDENTIFIER));


    }

}
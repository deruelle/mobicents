package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import junit.framework.TestCase;

import org.csapi.TpAddress;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpMultiPartyCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;

/**
 * 
 * Class Description for IpCallLegConnectionImplTest
 */
public class IpCallLegConnectionImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        callLegControl = MockControl.createControl(CallLeg.class);
        callLeg = (CallLeg) callLegControl.getMock();

        connectionImpl = new IpCallLegConnectionImpl(callLeg);
    }

    CallLeg callLeg;

    MockControl callLegControl;

    IpCallLegConnectionImpl connectionImpl;

    public void testIpCallLegConnectionImpl() {

        try {
            connectionImpl = new IpCallLegConnectionImpl(null);
            fail();
        }
        catch (Exception e) {

        }

    }

    public void testRouteReq() {
        try {
            callLeg.routeReq(MpccsTestData.TP_ADDRESS,
                    MpccsTestData.TP_ADDRESS,
                    MpccsTestData.TP_CALL_APP_INFO_ARRAY,
                    MpccsTestData.TP_CALL_LEG_CONNECTION_PROPERTIES);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.routeReq(MpccsTestData.TP_ADDRESS,
                    MpccsTestData.TP_ADDRESS,
                    MpccsTestData.TP_CALL_APP_INFO_ARRAY,
                    MpccsTestData.TP_CALL_LEG_CONNECTION_PROPERTIES);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testEventReportReq() {
        try {
            callLeg.eventReportReq(MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl
                    .eventReportReq(MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testRelease() {
        try {
            callLeg.release(MpccsTestData.TP_RELEASE_CAUSE);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.release(MpccsTestData.TP_RELEASE_CAUSE);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testGetInfoReq() {
        try {
            callLeg.getInfoReq(MpccsTestData.CALL_INFO_REQUESTED);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.getInfoReq(MpccsTestData.CALL_INFO_REQUESTED);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testGetCall() {
        TpMultiPartyCallIdentifier callIdentifier = new TpMultiPartyCallIdentifier(
                MpccsTestData.CALL_ID, MpccsTestData.CALL_SESSION_ID);
        try {
            callLeg.getCall();
            callLegControl.setReturnValue(callIdentifier);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            TpMultiPartyCallIdentifier result = connectionImpl.getCall();

            assertEquals(callIdentifier, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testAttachMediaReq() {
        try {
            callLeg.attachMediaReq();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.attachMediaReq();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testDetachMediaReq() {
        try {
            callLeg.detachMediaReq();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.detachMediaReq();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testGetCurrentDestinationAddress() {
        try {
            callLeg.getCurrentDestinationAddress();
            callLegControl.setReturnValue(MpccsTestData.TP_ADDRESS);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            TpAddress result = connectionImpl.getCurrentDestinationAddress();

            assertEquals(MpccsTestData.TP_ADDRESS, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testContinueProcessing() {
        try {
            callLeg.continueProcessing();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.continueProcessing();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testSetChargePlan() {
        try {
            callLeg.setChargePlan(MpccsTestData.TP_CALL_CHARGE_PLAN);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.setChargePlan(MpccsTestData.TP_CALL_CHARGE_PLAN);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testSetAdviceOfCharge() {
        try {
            callLeg.setAdviceOfCharge(MpccsTestData.TP_AOC_INFO,
                    MpccsTestData.TARIFF_SWITCH);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.setAdviceOfCharge(MpccsTestData.TP_AOC_INFO,
                    MpccsTestData.TARIFF_SWITCH);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testSuperviseReq() {
        try {
            callLeg.superviseReq(MpccsTestData.TIME, MpccsTestData.TREATMENT);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.superviseReq(MpccsTestData.TIME,
                    MpccsTestData.TREATMENT);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testDeassign() {
        try {
            callLeg.deassign();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.replay();

        try {
            connectionImpl.deassign();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        callLegControl.verify();
    }

    public void testClose() {
        callLegControl.replay();

        try {
            connectionImpl.closeConnection();
        }
        catch (javax.slee.resource.ResourceException e) {
            fail();
        }

        callLegControl.verify();
    }

}
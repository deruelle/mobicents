package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.cc.mpccs.IpCallLegConnection;
import org.mobicents.csapi.jr.slee.cc.mpccs.TpCallLegIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLegStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;

/**
 * 
 * Class Description for IpMultiPartyCallConnectionImplTest
 */
public class IpMultiPartyCallConnectionImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        multiPartyCallControl = MockControl.createControl(MultiPartyCall.class);

        multiPartyCall = (MultiPartyCall) multiPartyCallControl.getMock();

        connectionImpl = new IpMultiPartyCallConnectionImpl(multiPartyCall);
    }

    MultiPartyCall multiPartyCall;

    MockControl multiPartyCallControl;

    IpMultiPartyCallConnectionImpl connectionImpl;
    
    public void testIpMultiPartyCallConnectionImpl() {

        try {
            connectionImpl = new IpMultiPartyCallConnectionImpl(null);
            fail();
        }
        catch (IllegalArgumentException e) {

        }
        
    }

    public void testGetCallLegConnection() {
        CallLeg callLeg = new CallLegStub();
        IpCallLegConnection callLegConnection = new IpCallLegConnectionImpl(
                callLeg);

        TpCallLegIdentifier callLegIdentifier = new TpCallLegIdentifier(
                MpccsTestData.CALL_LEG_ID, MpccsTestData.CALL_LEG_SESSION_ID);

        try {
            multiPartyCall.getIpCallLegConnection(callLegIdentifier);
            multiPartyCallControl.setReturnValue(callLegConnection);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();
        try {
            IpCallLegConnection result = connectionImpl
                    .getIpCallLegConnection(callLegIdentifier);
            assertNotNull(result);
            assertEquals(callLegConnection, result);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testGetCallLegs() {
        TpCallLegIdentifier[] callLegIdentifiers = new TpCallLegIdentifier[] {};
        try {
            multiPartyCall.getCallLegs();
            multiPartyCallControl.setReturnValue(callLegIdentifiers);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            TpCallLegIdentifier[] result = connectionImpl.getCallLegs();

            assertEquals(callLegIdentifiers, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testCreateCallLeg() {
        TpCallLegIdentifier callLegIdentifier = new TpCallLegIdentifier(
                MpccsTestData.CALL_LEG_ID, MpccsTestData.CALL_LEG_SESSION_ID);
        try {
            multiPartyCall.createCallLeg();
            multiPartyCallControl.setReturnValue(callLegIdentifier);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            TpCallLegIdentifier result = connectionImpl.createCallLeg();

            assertEquals(callLegIdentifier, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testCreateAndRouteCallLegReq() {
        TpCallLegIdentifier callLegIdentifier = new TpCallLegIdentifier(
                MpccsTestData.CALL_LEG_ID, MpccsTestData.CALL_LEG_SESSION_ID);
        try {
            multiPartyCall.createAndRouteCallLegReq(
                    MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY,
                    MpccsTestData.TP_ADDRESS, MpccsTestData.TP_ADDRESS,
                    MpccsTestData.TP_CALL_APP_INFO_ARRAY);
            multiPartyCallControl.setReturnValue(callLegIdentifier);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            TpCallLegIdentifier result = connectionImpl
                    .createAndRouteCallLegReq(
                            MpccsTestData.TP_CALL_EVENT_REQUEST_ARRAY,
                            MpccsTestData.TP_ADDRESS, MpccsTestData.TP_ADDRESS,
                            MpccsTestData.TP_CALL_APP_INFO_ARRAY);

            assertEquals(callLegIdentifier, result);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testRelease() {
        try {
            multiPartyCall.release(MpccsTestData.TP_RELEASE_CAUSE);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            connectionImpl.release(MpccsTestData.TP_RELEASE_CAUSE);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testDeassignCall() {
        try {
            multiPartyCall.deassignCall();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            connectionImpl.deassignCall();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testGetInfoReq() {
        try {
            multiPartyCall.getInfoReq(MpccsTestData.CALL_INFO_REQUESTED);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            connectionImpl.getInfoReq(MpccsTestData.CALL_INFO_REQUESTED);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testSetChargePlan() {
        try {
            multiPartyCall.setChargePlan(MpccsTestData.TP_CALL_CHARGE_PLAN);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            connectionImpl.setChargePlan(MpccsTestData.TP_CALL_CHARGE_PLAN);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testSetAdviceOfCharge() {
        try {
            multiPartyCall.setAdviceOfCharge(MpccsTestData.TP_AOC_INFO,
                    MpccsTestData.TARIFF_SWITCH);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            connectionImpl.setAdviceOfCharge(MpccsTestData.TP_AOC_INFO,
                    MpccsTestData.TARIFF_SWITCH);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testSuperviseReq() {
        try {
            multiPartyCall.superviseReq(MpccsTestData.TIME,
                    MpccsTestData.TREATMENT);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.replay();

        try {
            connectionImpl.superviseReq(MpccsTestData.TIME,
                    MpccsTestData.TREATMENT);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        multiPartyCallControl.verify();
    }

    public void testClose() throws ResourceException {
        multiPartyCallControl.replay();
        connectionImpl.closeConnection();
        multiPartyCallControl.verify();
    }

}
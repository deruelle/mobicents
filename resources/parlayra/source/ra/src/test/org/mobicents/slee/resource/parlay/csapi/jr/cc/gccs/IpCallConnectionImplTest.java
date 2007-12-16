
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpCommonExceptions;
import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call;

/**
 *
 **/
public class IpCallConnectionImplTest extends TestCase {


    Call mockCall;

    MockControl callControl;

    IpCallConnectionImpl connectionImpl;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        callControl = MockControl.createControl(Call.class);

        mockCall = (Call) callControl.getMock();

        connectionImpl = new IpCallConnectionImpl(mockCall);
    }

    public void testRouteReq() throws TpCommonExceptions, P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN, P_INVALID_NETWORK_STATE, P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {

        mockCall.routeReq(GccsTestData.CALL_REPORT_REQUEST_ARRAY, 
                    GccsTestData.TP_ADDRESS,
                    GccsTestData.TP_ADDRESS, 
                    GccsTestData.TP_ADDRESS, 
                    GccsTestData.TP_ADDRESS,
                    GccsTestData.TP_CALL_APP_INFO_ARRAY);
        callControl.setReturnValue(1001);

        callControl.replay();

        int result = connectionImpl.routeReq(GccsTestData.CALL_REPORT_REQUEST_ARRAY, 
                    GccsTestData.TP_ADDRESS,
                    GccsTestData.TP_ADDRESS, 
                    GccsTestData.TP_ADDRESS, 
                    GccsTestData.TP_ADDRESS,
                    GccsTestData.TP_CALL_APP_INFO_ARRAY);

        assertEquals(1001, result);

        callControl.verify();
    }

    public void testRelease() throws TpCommonExceptions, P_INVALID_NETWORK_STATE, ResourceException {

        mockCall.release(GccsTestData.TP_CALL_RELEASE_CAUSE);

        callControl.replay();

        connectionImpl.release(GccsTestData.TP_CALL_RELEASE_CAUSE);

        callControl.verify();
    }

    public void testDeassignCall() throws TpCommonExceptions, ResourceException {
        
        mockCall.deassignCall();

        callControl.replay();

        connectionImpl.deassignCall();


        callControl.verify();
    }

    public void testGetCallInfoReq() throws TpCommonExceptions, ResourceException {
        
        mockCall.getCallInfoReq(GccsTestData.CALL_INFO_REQUESTED);

        callControl.replay();

        connectionImpl.getCallInfoReq(GccsTestData.CALL_INFO_REQUESTED);

        callControl.verify();
    }

    public void testSetCallChargePlan() throws TpCommonExceptions, ResourceException {

        mockCall.setCallChargePlan(GccsTestData.TP_CALL_CHARGE_PLAN);

        callControl.replay();

        connectionImpl.setCallChargePlan(GccsTestData.TP_CALL_CHARGE_PLAN);

        callControl.verify();
    }

    public void testSetAdviceOfCharge() throws TpCommonExceptions, ResourceException {

        mockCall.setAdviceOfCharge(GccsTestData.TP_AOC_INFO, GccsTestData.ASSIGNMENT_ID);


        callControl.replay();

        connectionImpl.setAdviceOfCharge(GccsTestData.TP_AOC_INFO, GccsTestData.ASSIGNMENT_ID);


        callControl.verify();
    }

    public void testGetMoreDialledDigitsReq() throws TpCommonExceptions, ResourceException {

        mockCall.getMoreDialledDigitsReq(1);

        callControl.replay();

        connectionImpl.getMoreDialledDigitsReq(1);

        callControl.verify();
    }

    public void testSuperviseCallReq() throws TpCommonExceptions, ResourceException {

        mockCall.superviseCallReq(1, 1);

        callControl.replay();

        connectionImpl.superviseCallReq(1, 1);

        callControl.verify();
    }

    public void testContinueProcessing() throws TpCommonExceptions, P_INVALID_NETWORK_STATE, ResourceException {
        
        mockCall.continueProcessing();

        callControl.replay();

        connectionImpl.continueProcessing();

        callControl.verify();
    }

    public void testCloseConnection() throws ResourceException {

        callControl.replay();

        connectionImpl.closeConnection();

        callControl.verify();
    }

}

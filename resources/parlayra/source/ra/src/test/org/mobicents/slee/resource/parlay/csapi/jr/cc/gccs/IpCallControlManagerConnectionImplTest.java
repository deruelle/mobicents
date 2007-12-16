
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ADDRESS;
import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_EVENT_TYPE;
import org.csapi.P_UNSUPPORTED_ADDRESS_PLAN;
import org.csapi.TpCommonExceptions;
import org.csapi.cc.gccs.TpCallEventCriteriaResult;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.cc.gccs.IpCallConnection;
import org.mobicents.csapi.jr.slee.cc.gccs.TpCallIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.CallStub;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager;

/**
 *
 **/
public class IpCallControlManagerConnectionImplTest extends TestCase {


    CallControlManager mockCallControlManager;

    MockControl callControlManagerControl;

    IpCallControlManagerConnectionImpl connectionImpl;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        callControlManagerControl = MockControl
        .createControl(CallControlManager.class);

        mockCallControlManager = (CallControlManager) callControlManagerControl
        .getMock();

        connectionImpl = new IpCallControlManagerConnectionImpl(
        mockCallControlManager);
    }

    public void testGetIpCallConnection() throws ResourceException {
        IpCallConnection ipCallConnection = new IpCallConnectionImpl(new CallStub());
        IpCallConnection result = null;
            
        mockCallControlManager.getIpCallConnection(GccsTestData.SLEE_TP_CALL_IDENTIFIER);
        callControlManagerControl.setReturnValue(ipCallConnection);
        callControlManagerControl.replay();
            
        result = connectionImpl.getIpCallConnection(GccsTestData.SLEE_TP_CALL_IDENTIFIER);
        
        assertNotNull(result);
        assertEquals(ipCallConnection, result);
        
        callControlManagerControl.verify();
    }

    public void testCreateCall() throws TpCommonExceptions, ResourceException {

        mockCallControlManager.createCall();
        callControlManagerControl.setReturnValue(GccsTestData.SLEE_TP_CALL_IDENTIFIER);

        callControlManagerControl.replay();

        TpCallIdentifier result = connectionImpl.createCall();

        assertEquals(GccsTestData.SLEE_TP_CALL_IDENTIFIER, result);

        callControlManagerControl.verify();
    }

    public void testEnableCallNotification() throws TpCommonExceptions, P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {

        mockCallControlManager.enableCallNotification(GccsTestData.TP_CALL_EVENT_CRITERIA);
        callControlManagerControl.setReturnValue(GccsTestData.ASSIGNMENT_ID);


        callControlManagerControl.replay();

        int result = connectionImpl.enableCallNotification(GccsTestData.TP_CALL_EVENT_CRITERIA);

        assertEquals(GccsTestData.ASSIGNMENT_ID, result);


        callControlManagerControl.verify();
    }

    public void testDisableCallNotification() throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID, ResourceException {

        mockCallControlManager.disableCallNotification(GccsTestData.ASSIGNMENT_ID);


        callControlManagerControl.replay();

        connectionImpl.disableCallNotification(GccsTestData.ASSIGNMENT_ID);


        callControlManagerControl.verify();
    }

    public void testSetCallLoadControl() throws TpCommonExceptions, P_INVALID_ADDRESS, P_UNSUPPORTED_ADDRESS_PLAN, ResourceException {

        mockCallControlManager.setCallLoadControl(GccsTestData.DURATION, 
                    GccsTestData.TP_CALL_LOAD_CONTROL_MECHANIM, 
                    GccsTestData.TP_CALL_TREATMENT, 
                    GccsTestData.TP_ADDRESS_RANGE);
        callControlManagerControl.setReturnValue(GccsTestData.ASSIGNMENT_ID);


        callControlManagerControl.replay();

        int result = connectionImpl.setCallLoadControl(GccsTestData.DURATION, 
                    GccsTestData.TP_CALL_LOAD_CONTROL_MECHANIM, 
                    GccsTestData.TP_CALL_TREATMENT, 
                    GccsTestData.TP_ADDRESS_RANGE);
            
        assertEquals(GccsTestData.ASSIGNMENT_ID, result);


        callControlManagerControl.verify();
    }

    public void testChangeCallNotification() throws TpCommonExceptions, P_INVALID_ASSIGNMENT_ID, P_INVALID_CRITERIA, P_INVALID_EVENT_TYPE, ResourceException {

        mockCallControlManager.changeCallNotification(GccsTestData.SESSIONID, 
                    GccsTestData.TP_CALL_EVENT_CRITERIA);


        callControlManagerControl.replay();

        connectionImpl.changeCallNotification(GccsTestData.SESSIONID, 
                    GccsTestData.TP_CALL_EVENT_CRITERIA);

        callControlManagerControl.verify();
    }

    public void testGetCriteria() throws TpCommonExceptions, ResourceException {

        mockCallControlManager.getCriteria();
        callControlManagerControl.setReturnValue(GccsTestData.TP_CALL_EVENT_CRITERIA_RESULT_SET);


        callControlManagerControl.replay();

        TpCallEventCriteriaResult[] result = connectionImpl.getCriteria();
            
        assertEquals(GccsTestData.TP_CALL_EVENT_CRITERIA_RESULT_SET, result);


        callControlManagerControl.verify();
    }

    public void testCloseConnection() throws ResourceException {

        callControlManagerControl.replay();

        connectionImpl.closeConnection();

        callControlManagerControl.verify();
    }

}

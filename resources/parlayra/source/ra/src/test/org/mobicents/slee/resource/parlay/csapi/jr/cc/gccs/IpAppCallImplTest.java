
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.call.Call;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.activity.callcontrolmanager.CallControlManager;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 *
 **/
public class IpAppCallImplTest extends TestCase {
    
    IpAppCallImpl appCallImpl = null;
    
    MockControl callControlManagerControl = null;
    
    CallControlManager mockCallControlManager = null;
    
    MockControl callControl = null;
    
    Call mockCall = null;

    POA poa;

    Executor executor;
    
    Executor[] executors;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        callControlManagerControl = MockControl.createControl(CallControlManager.class);
        
        mockCallControlManager = (CallControlManager)callControlManagerControl.getMock();
        
        
        callControl = MockControl.createControl(Call.class);
        
        mockCall = (Call)callControl.getMock();

        poa = new POAStub();
        

        executor = new QueuedExecutor();
        
        executors = new Executor[] { executor };

        appCallImpl = new IpAppCallImpl(mockCallControlManager,
                poa, executors);
    }

    public void testRouteRes() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.routeRes(GccsTestData.SESSIONID, GccsTestData.EVENTREPORT, GccsTestData.CALL_LEG_SESSIONID);
        callControl.replay();
        
        appCallImpl.routeRes(GccsTestData.SESSIONID, GccsTestData.EVENTREPORT, GccsTestData.CALL_LEG_SESSIONID);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
        
    }

    public void testRouteErr() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.routeErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR, GccsTestData.CALL_LEG_SESSIONID);
        callControl.replay();
        
        appCallImpl.routeErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR, GccsTestData.CALL_LEG_SESSIONID);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testGetCallInfoRes() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.getCallInfoRes(GccsTestData.SESSIONID, GccsTestData.TP_CALL_INFO_REPORT);
        callControl.replay();
        
        appCallImpl.getCallInfoRes(GccsTestData.SESSIONID, GccsTestData.TP_CALL_INFO_REPORT);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testGetCallInfoErr() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.getCallInfoErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        callControl.replay();
        
        appCallImpl.getCallInfoErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testSuperviseCallRes() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.superviseCallRes(GccsTestData.SESSIONID, GccsTestData.REPORT, GccsTestData.USED_TIME);
        callControl.replay();
        
        appCallImpl.superviseCallRes(GccsTestData.SESSIONID, GccsTestData.REPORT, GccsTestData.USED_TIME);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testSuperviseCallErr() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.superviseCallErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        callControl.replay();
        
        appCallImpl.superviseCallErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testCallFaultDetected() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.callFaultDetected(GccsTestData.SESSIONID, GccsTestData.TP_CALL_FAULT);
        callControl.replay();
        
        appCallImpl.callFaultDetected(GccsTestData.SESSIONID, GccsTestData.TP_CALL_FAULT);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testGetMoreDialledDigitsRes() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.getMoreDialledDigitsRes(GccsTestData.SESSIONID, GccsTestData.DIGITS);
        callControl.replay();
        
        appCallImpl.getMoreDialledDigitsRes(GccsTestData.SESSIONID, GccsTestData.DIGITS);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testGetMoreDialledDigitsErr() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.getMoreDialledDigitsErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        callControl.replay();
        
        appCallImpl.getMoreDialledDigitsErr(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ERROR);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testCallEnded() {
        mockCallControlManager.getCall(GccsTestData.SESSIONID);
        callControlManagerControl.setReturnValue(mockCall);
        callControlManagerControl.replay();
        
        mockCall.callEnded(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ENDED_REPORT);
        callControl.replay();
        
        appCallImpl.callEnded(GccsTestData.SESSIONID, GccsTestData.TP_CALL_ENDED_REPORT);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        
        callControl.verify();
    }

    public void testDispose() {
        appCallImpl.dispose();
        appCallImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appCallImpl._default_POA());
    }
    
 

}

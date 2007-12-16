package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs;

import junit.framework.TestCase;

import org.csapi.cc.gccs.IpAppCall;
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
public class IpAppCallControlManagerImplTest extends TestCase {
    
    IpAppCallControlManagerImpl appCallControlManagerImpl = null;

    POA poa;
    
    MockControl callControlManagerControl = null;
    
    CallControlManager mockCallControlManager = null;
    
    MockControl callControl = null;
    
    Call mockCall = null;

    IpAppCall ipAppCall = null;
    
    Executor executor;
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
        
        appCallControlManagerImpl = new IpAppCallControlManagerImpl(mockCallControlManager, poa, new QueuedExecutor() );
        
        

    }

    public void testDispose() {
        appCallControlManagerImpl.dispose();
        appCallControlManagerImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appCallControlManagerImpl._default_POA());
    }

    public void testCallAborted() {
        
        mockCallControlManager.callAborted(GccsTestData.SESSIONID);
        callControlManagerControl.replay();
        
        appCallControlManagerImpl.callAborted(GccsTestData.SESSIONID);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();

    }

    public void testCallEventNotify() {
        mockCallControlManager.callEventNotify(GccsTestData.SLEE_TP_CALL_IDENTIFIER, GccsTestData.TP_CALL_EVENT_INFO, GccsTestData.ASSIGNMENT_ID);
        mockCallControlManager.getCall(GccsTestData.TP_CALL_IDENTIFIER.CallSessionID);
        callControlManagerControl.setReturnValue(null);
        

        mockCallControlManager.createCall(GccsTestData.TP_CALL_IDENTIFIER);
        callControlManagerControl.setReturnValue(mockCall);
        
        mockCallControlManager.getIpAppCall();
        callControlManagerControl.setReturnValue(null);
        
        callControlManagerControl.replay();
        
        mockCall.getTpCallIdentifier();
        callControl.setReturnValue(GccsTestData.SLEE_TP_CALL_IDENTIFIER);
        callControl.replay();
        
        IpAppCall appCall = appCallControlManagerImpl.callEventNotify(GccsTestData.TP_CALL_IDENTIFIER, GccsTestData.TP_CALL_EVENT_INFO, GccsTestData.ASSIGNMENT_ID);
        
        assertEquals(ipAppCall, appCall);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
        callControl.verify();
    }

    public void testCallNotificationInterrupted() {
        mockCallControlManager.callNotificationInterrupted();
        callControlManagerControl.replay();
        
        appCallControlManagerImpl.callNotificationInterrupted();
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
    }

    public void testCallNotificationContinued() {
        mockCallControlManager.callNotificationContinued();
        callControlManagerControl.replay();
        
        appCallControlManagerImpl.callNotificationContinued();
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
    }

    public void testCallOverloadEncountered() {
        mockCallControlManager.callOverloadEncountered(GccsTestData.ASSIGNMENT_ID);
        callControlManagerControl.replay();
        
        appCallControlManagerImpl.callOverloadEncountered(GccsTestData.ASSIGNMENT_ID);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
    }

    public void testCallOverloadCeased() {
        mockCallControlManager.callOverloadCeased(GccsTestData.ASSIGNMENT_ID);
        callControlManagerControl.replay();
        
        appCallControlManagerImpl.callOverloadCeased(GccsTestData.ASSIGNMENT_ID);
        
        TestThreadUtil.pause();
        callControlManagerControl.verify();
    }
    
 

}

package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 * 
 * Class Description for IpAppMultiPartyCallImplTest
 */
public class IpAppMultiPartyCallImplTest extends TestCase {

    IpAppMultiPartyCallImpl appMultiPartyCallImpl;

    MultiPartyCallControlManager callControlManager;

    MockControl callControlManagerControl;

    MultiPartyCall call;

    MockControl callControl;

    POA poa;

    Executor executor;
    
    Executor[] executors;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        callControlManagerControl = MockControl
                .createControl(MultiPartyCallControlManager.class);

        callControlManager = (MultiPartyCallControlManager) callControlManagerControl
                .getMock();

        callControl = MockControl.createControl(MultiPartyCall.class);

        call = (MultiPartyCall) callControl.getMock();

        poa = new POAStub();
        
        executor = new QueuedExecutor();
        
        executors = new Executor[] { executor };
        
        appMultiPartyCallImpl = new IpAppMultiPartyCallImpl(callControlManager,
                poa, executors);
    }

    public void testDispose() {
        appMultiPartyCallImpl.dispose();
        appMultiPartyCallImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appMultiPartyCallImpl._default_POA());
    }

    public void testGetInfoRes() {

        callControlManager.getMultiPartyCall(MpccsTestData.CALL_SESSION_ID);
        callControlManagerControl.setReturnValue(call);
        callControlManagerControl.replay();

        call.getInfoRes(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_INFO_REPORT);
        callControl.replay();

        appMultiPartyCallImpl.getInfoRes(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_INFO_REPORT);

        TestThreadUtil.pause();

        callControlManagerControl.verify();
        callControl.verify();
    }

    public void testGetInfoErr() {

        callControlManager.getMultiPartyCall(MpccsTestData.CALL_SESSION_ID);
        callControlManagerControl.setReturnValue(call);
        callControlManagerControl.replay();

        call.getInfoErr(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callControl.replay();

        appMultiPartyCallImpl.getInfoErr(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControlManagerControl.verify();
        callControl.verify();
    }

    public void testSuperviseRes() {

        callControlManager.getMultiPartyCall(MpccsTestData.CALL_SESSION_ID);
        callControlManagerControl.setReturnValue(call);
        callControlManagerControl.replay();

        call.superviseRes(MpccsTestData.CALL_SESSION_ID, MpccsTestData.REPORT,
                MpccsTestData.DURATION);
        callControl.replay();

        appMultiPartyCallImpl.superviseRes(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.REPORT, MpccsTestData.DURATION);

        TestThreadUtil.pause();

        callControlManagerControl.verify();
        callControl.verify();
    }

    public void testSuperviseErr() {

        callControlManager.getMultiPartyCall(MpccsTestData.CALL_SESSION_ID);
        callControlManagerControl.setReturnValue(call);
        callControlManagerControl.replay();

        call.superviseErr(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callControl.replay();

        appMultiPartyCallImpl.superviseErr(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControlManagerControl.verify();
        callControl.verify();
    }

    public void testCallEnded() {

        callControlManager.getMultiPartyCall(MpccsTestData.CALL_SESSION_ID);
        callControlManagerControl.setReturnValue(call);
        callControlManagerControl.replay();

        call.callEnded(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_ENDED_REPORT);
        callControl.replay();

        appMultiPartyCallImpl.callEnded(MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_ENDED_REPORT);

        TestThreadUtil.pause();

        callControlManagerControl.verify();
        callControl.verify();
    }

    public void testCreateAndRouteCallLegErr() {
        callControlManager.getMultiPartyCall(MpccsTestData.CALL_SESSION_ID);
        callControlManagerControl.setReturnValue(call);
        callControlManagerControl.replay();

        call.createAndRouteCallLegErr(
                MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_LEG_IDENTIFIER,
                MpccsTestData.TP_CALL_ERROR);
        callControl.replay();

        appMultiPartyCallImpl.createAndRouteCallLegErr(
                MpccsTestData.CALL_SESSION_ID,
                MpccsTestData.TP_CALL_LEG_IDENTIFIER,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControlManagerControl.verify();
        callControl.verify();
    }

    public void testGetExecutors() {
        assertEquals(executors, appMultiPartyCallImpl.getExecutors());
    }

    
}
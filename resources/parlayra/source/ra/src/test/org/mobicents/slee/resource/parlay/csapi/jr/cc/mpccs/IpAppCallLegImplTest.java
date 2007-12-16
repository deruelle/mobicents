package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import org.easymock.MockControl;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.callleg.CallLeg;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycall.MultiPartyCall;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import junit.framework.TestCase;

/**
 *
 * Class Description for IpAppCallLegImplTest
 */
public class IpAppCallLegImplTest extends TestCase {
    
    IpAppCallLegImpl appCallLegImpl;

    MultiPartyCall call;

    MockControl callControl;
    
    CallLeg callLeg;
    
    MockControl callLegControl;

    POA poa;

    Executor executor;
    
    Executor[] executors;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        callControl = MockControl.createControl(MultiPartyCall.class);

        call = (MultiPartyCall) callControl.getMock();

        callLegControl = MockControl.createControl(CallLeg.class);

        callLeg = (CallLeg) callLegControl.getMock();

        poa = new POAStub();

        executor = new QueuedExecutor();
        
        executors = new Executor[] { executor };
        
        appCallLegImpl = new IpAppCallLegImpl(call,
                poa, executors);

    }

    public void testDispose() {
        appCallLegImpl.dispose();
        appCallLegImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appCallLegImpl._default_POA());
    }

    public void testEventReportRes() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.eventReportRes(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_EVENT_INFO);
        callLegControl.replay();

        appCallLegImpl.eventReportRes(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_EVENT_INFO);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testEventReportErr() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.eventReportErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callLegControl.replay();

        appCallLegImpl.eventReportErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testAttachMediaRes() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.attachMediaRes(MpccsTestData.CALL_LEG_SESSION_ID);
        callLegControl.replay();

        appCallLegImpl.attachMediaRes(MpccsTestData.CALL_LEG_SESSION_ID);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testAttachMediaErr() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.attachMediaErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callLegControl.replay();

        appCallLegImpl.attachMediaErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testDetachMediaRes() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.detachMediaRes(MpccsTestData.CALL_LEG_SESSION_ID);
        callLegControl.replay();

        appCallLegImpl.detachMediaRes(MpccsTestData.CALL_LEG_SESSION_ID);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testDetachMediaErr() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.detachMediaErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callLegControl.replay();

        appCallLegImpl.detachMediaErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testGetInfoRes() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.getInfoRes(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_LEG_INFO_REPORT);
        callLegControl.replay();

        appCallLegImpl.getInfoRes(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_LEG_INFO_REPORT);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testGetInfoErr() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.getInfoErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callLegControl.replay();

        appCallLegImpl.getInfoErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testRouteErr() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.routeErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callLegControl.replay();

        appCallLegImpl.routeErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testSuperviseRes() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.superviseRes(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.REPORT, MpccsTestData.DURATION);
        callLegControl.replay();

        appCallLegImpl.superviseRes(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.REPORT, MpccsTestData.DURATION);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testSuperviseErr() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.superviseErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);
        callLegControl.replay();

        appCallLegImpl.superviseErr(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_CALL_ERROR);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }

    public void testCallLegEnded() {

        call.getCallLeg(MpccsTestData.CALL_LEG_SESSION_ID);
        callControl.setReturnValue(callLeg);
        callControl.replay();

        callLeg.callLegEnded(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_RELEASE_CAUSE);
        callLegControl.replay();

        appCallLegImpl.callLegEnded(MpccsTestData.CALL_LEG_SESSION_ID,
                MpccsTestData.TP_RELEASE_CAUSE);

        TestThreadUtil.pause();

        callControl.verify();
        callLegControl.verify();
    }


   
}

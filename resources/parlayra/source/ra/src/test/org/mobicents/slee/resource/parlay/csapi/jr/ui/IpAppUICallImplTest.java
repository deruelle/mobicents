package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICall;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManager;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 * Much of this class is very similar to IpAppUIImplTest
 */
public class IpAppUICallImplTest extends TestCase {

    IpAppUICallImpl appUICallImpl = null;

    MockControl uiManagerControl = null;

    UIManager uiManagerMock = null;

    MockControl uiCallControl = null;

    UICall uiCallControlMock = null;

    POA poa;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        uiManagerControl = MockControl.createControl(UIManager.class);
        uiManagerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);

        uiManagerMock = (UIManager) uiManagerControl.getMock();

        uiCallControl = MockControl.createControl(UICall.class);
        uiCallControlMock = (UICall) uiCallControl.getMock();

        poa = new POAStub();

        appUICallImpl = new IpAppUICallImpl(uiManagerMock, poa,
                new Executor[] { new QueuedExecutor() });
    }

    public void testSendInfoRes() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.sendInfoRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.sendInfoRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testSendInfoErr() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.sendInfoErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.sendInfoErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testSendInfoAndCollectRes() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.sendInfoAndCollectRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT,
                UiTestData.COLLECTED_INFO);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.sendInfoAndCollectRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT,
                UiTestData.COLLECTED_INFO);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testSendInfoAndCollectErr() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.sendInfoAndCollectErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.sendInfoAndCollectErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    
    
    
    public void testUserInteractionFaultDetected() {
        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.userInteractionFaultDetected(UiTestData.SESSIONID,
                UiTestData.TP_UI_FAULT);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.userInteractionFaultDetected(UiTestData.SESSIONID,
                UiTestData.TP_UI_FAULT);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }
    
    // UICALL ONLY TESTS ...
    public void testDeleteMessageRes() {

        uiManagerMock.getUICall(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.deleteMessageRes(UiTestData.SESSIONID,
                UiTestData.TP_UI_REPORT, UiTestData.ASSIGNMENT_ID);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.deleteMessageRes(UiTestData.SESSIONID,
                UiTestData.TP_UI_REPORT, UiTestData.ASSIGNMENT_ID);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testDeleteMessageErr() {

        uiManagerMock.getUICall(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.deleteMessageErr(UiTestData.SESSIONID,
                UiTestData.TP_UI_ERROR, UiTestData.ASSIGNMENT_ID);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.deleteMessageErr(UiTestData.SESSIONID,
                UiTestData.TP_UI_ERROR, UiTestData.ASSIGNMENT_ID);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testRecordMessageRes() {

        uiManagerMock.getUICall(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.recordMessageRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT,
                UiTestData.MESSAGE_ID);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.recordMessageRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT,
                UiTestData.MESSAGE_ID);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testRecordMessageErr() {

        uiManagerMock.getUICall(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.recordMessageErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.recordMessageErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testAbortActionRes() {

        uiManagerMock.getUICall(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.abortActionRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.abortActionRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testAbortActionErr() {

        uiManagerMock.getUICall(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiCallControlMock);

        uiCallControlMock.abortActionErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        uiCallControl.setVoidCallable(MockControl.ONE);

        uiCallControl.replay();
        uiManagerControl.replay();

        appUICallImpl.abortActionErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiCallControl.verify();

    }

    public void testDispose() {
        appUICallImpl.dispose();
        appUICallImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appUICallImpl._default_POA());
    }

}

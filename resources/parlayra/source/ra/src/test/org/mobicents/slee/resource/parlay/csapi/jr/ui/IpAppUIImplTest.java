package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGeneric;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManager;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 * 
 */
public class IpAppUIImplTest extends TestCase {

    IpAppUIImpl appUIImpl = null;

    MockControl uiManagerControl = null;

    UIManager uiManagerMock = null;

    MockControl uiGenericControl = null;

    UIGeneric uiGenericControlMock = null;

    POA poa;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        uiManagerControl = MockControl.createControl(UIManager.class);
        uiManagerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);

        uiManagerMock = (UIManager) uiManagerControl.getMock();

        uiGenericControl = MockControl.createControl(UIGeneric.class);
        uiGenericControlMock = (UIGeneric) uiGenericControl.getMock();

        poa = new POAStub();

        appUIImpl = new IpAppUIImpl(uiManagerMock, poa,
                new Executor[] { new QueuedExecutor() });
    }

    public void testSendInfoRes() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiGenericControlMock);

        uiGenericControlMock.sendInfoRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT);
        uiGenericControl.setVoidCallable(MockControl.ONE);

        uiGenericControl.replay();
        uiManagerControl.replay();

        appUIImpl.sendInfoRes(UiTestData.SESSIONID, UiTestData.ASSIGNMENT_ID,
                UiTestData.TP_UI_REPORT);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiGenericControl.verify();

    }

    public void testSendInfoErr() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiGenericControlMock);

        uiGenericControlMock.sendInfoErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        uiGenericControl.setVoidCallable(MockControl.ONE);

        uiGenericControl.replay();
        uiManagerControl.replay();

        appUIImpl.sendInfoErr(UiTestData.SESSIONID, UiTestData.ASSIGNMENT_ID,
                UiTestData.TP_UI_ERROR);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiGenericControl.verify();

    }

    public void testSendInfoAndCollectRes() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiGenericControlMock);

        uiGenericControlMock.sendInfoAndCollectRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT,
                UiTestData.COLLECTED_INFO);
        uiGenericControl.setVoidCallable(MockControl.ONE);

        uiGenericControl.replay();
        uiManagerControl.replay();

        appUIImpl.sendInfoAndCollectRes(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_REPORT,
                UiTestData.COLLECTED_INFO);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiGenericControl.verify();

    }

    public void testSendInfoAndCollectErr() {

        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiGenericControlMock);

        uiGenericControlMock.sendInfoAndCollectErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        uiGenericControl.setVoidCallable(MockControl.ONE);

        uiGenericControl.replay();
        uiManagerControl.replay();

        appUIImpl.sendInfoAndCollectErr(UiTestData.SESSIONID,
                UiTestData.ASSIGNMENT_ID, UiTestData.TP_UI_ERROR);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiGenericControl.verify();

    }

    public void testUserInteractionFaultDetected() {
        uiManagerMock.getAbstractUI(UiTestData.SESSIONID);
        uiManagerControl.setReturnValue(uiGenericControlMock);

        uiGenericControlMock.userInteractionFaultDetected(UiTestData.SESSIONID,
                UiTestData.TP_UI_FAULT);
        uiGenericControl.setVoidCallable(MockControl.ONE);

        uiGenericControl.replay();
        uiManagerControl.replay();

        appUIImpl.userInteractionFaultDetected(UiTestData.SESSIONID,
                UiTestData.TP_UI_FAULT);
        TestThreadUtil.pause();

        uiManagerControl.verify();
        uiGenericControl.verify();

    }

    public void testDispose() {
        appUIImpl.dispose();
        appUIImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appUIImpl._default_POA());
    }

}

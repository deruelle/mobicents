package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import junit.framework.TestCase;

import org.csapi.ui.IpAppUI;
import org.easymock.MockControl;
import org.mobicents.slee.resource.TestThreadUtil;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGeneric;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManager;
import org.mobicents.slee.resource.parlay.session.POAStub;
import org.omg.PortableServer.POA;

import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;

/**
 * 
 */
public class IpAppUIManagerImplTest extends TestCase {

    IpAppUIManagerImpl appUIManagerImpl = null; 

    POA poa;

    MockControl uiManagerControl = null;

    UIManager uiManagerMock = null;

    MockControl uiGenericControl = null;

    UIGeneric uiGenericMock = null;

    IpAppUI ipAppUIDummy = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        uiManagerControl = MockControl.createControl(UIManager.class);
        uiManagerMock = (UIManager) uiManagerControl.getMock();

        uiGenericControl = MockControl.createControl(UIGeneric.class);
        uiGenericMock = (UIGeneric) uiGenericControl.getMock();

        poa = new POAStub();

        appUIManagerImpl = new IpAppUIManagerImpl(uiManagerMock, poa,
                new QueuedExecutor());

    }

    public void testDispose() {
        appUIManagerImpl.dispose();
        appUIManagerImpl.dispose();
    }

    /*
     * Class under test for POA _default_POA()
     */
    public void test_default_POA() {
        assertEquals(poa, appUIManagerImpl._default_POA());
    }

    public void testUserInteractionAborted() {

        uiManagerMock.userInteractionAborted(UiTestData.TP_UI_IDENTIFIER);
        uiManagerControl.replay();

        appUIManagerImpl.userInteractionAborted(UiTestData.TP_UI_IDENTIFIER);

        TestThreadUtil.pause();
        uiManagerControl.verify();

    }

    public void testReportNotification() {

        uiManagerMock.createUIGeneric(UiTestData.TP_UI_IDENTIFIER);
        uiManagerControl.setReturnValue(uiGenericMock);

        uiManagerMock.reportNotification(UiTestData.SLEE_TP_UI_IDENTIFIER,
                UiTestData.TP_UI_EVENT_INFO, UiTestData.ASSIGNMENT_ID);

        uiManagerControl.setVoidCallable(MockControl.ONE);

        uiManagerMock.getIpAppUI();
        uiManagerControl.setReturnValue(null);

        uiManagerControl.replay();

        uiGenericMock.getTpUIIdentifier();
        uiGenericControl.setReturnValue(UiTestData.SLEE_TP_UI_IDENTIFIER);
        uiGenericControl.replay();

        IpAppUI appUI = appUIManagerImpl.reportNotification(
                UiTestData.TP_UI_IDENTIFIER, UiTestData.TP_UI_EVENT_INFO,
                UiTestData.ASSIGNMENT_ID);

        assertEquals(ipAppUIDummy, appUI);

        TestThreadUtil.pause();

        uiGenericControl.verify();
        uiManagerControl.verify();
    }

    public void testReportEventNotification() {

        uiManagerMock.createUIGeneric(UiTestData.TP_UI_IDENTIFIER);
        uiManagerControl.setReturnValue(uiGenericMock);

        uiManagerMock.reportEventNotification(UiTestData.SLEE_TP_UI_IDENTIFIER,
                UiTestData.TP_UI_EVENT_NOTIFICATION_INFO,
                UiTestData.ASSIGNMENT_ID);
        uiManagerControl.setVoidCallable(MockControl.ONE);

        uiManagerMock.getIpAppUI();
        uiManagerControl.setReturnValue(null);

        uiManagerControl.replay();

        uiGenericMock.getTpUIIdentifier();
        uiGenericControl.setReturnValue(UiTestData.SLEE_TP_UI_IDENTIFIER);
        uiGenericControl.replay();

        IpAppUI appUI = appUIManagerImpl.reportEventNotification(
                UiTestData.TP_UI_IDENTIFIER,
                UiTestData.TP_UI_EVENT_NOTIFICATION_INFO,
                UiTestData.ASSIGNMENT_ID);

        assertEquals(ipAppUIDummy, appUI);

        TestThreadUtil.pause();

        uiGenericControl.verify();
        uiManagerControl.verify();
    }

    public void testUserInteractionNotificationInterrupted() {
        uiManagerMock.userInteractionNotificationInterrupted();
        uiManagerControl.replay();

        appUIManagerImpl.userInteractionNotificationInterrupted();

        TestThreadUtil.pause();
        uiManagerControl.verify();
    }

    public void testUserInteractionNotificationContinued() {
        uiManagerMock.userInteractionNotificationContinued();
        uiManagerControl.replay();

        appUIManagerImpl.userInteractionNotificationContinued();

        TestThreadUtil.pause();
        uiManagerControl.verify();
    }

}

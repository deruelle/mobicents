package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.csapi.P_INVALID_ASSIGNMENT_ID;
import org.csapi.P_INVALID_CRITERIA;
import org.csapi.P_INVALID_NETWORK_STATE;
import org.csapi.TpCommonExceptions;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.ui.IpUICallConnection;
import org.mobicents.csapi.jr.slee.ui.IpUIConnection;
import org.mobicents.csapi.jr.slee.ui.TpUICallIdentifier;
import org.mobicents.csapi.jr.slee.ui.TpUIIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.ui.UIGenericStub;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uicall.UICallStub;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.activity.uimanager.UIManager;

/**
 * 
 */
public class IpUIManagerConnectionImplTest extends TestCase {

    UIManager uiManagerMock;
    MockControl uiManagerControl;

    IpUIManagerConnectionImpl connectionImpl;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        uiManagerControl = MockControl.createControl(UIManager.class);
        uiManagerMock = (UIManager) uiManagerControl.getMock();

        // class under test
        connectionImpl = new IpUIManagerConnectionImpl(uiManagerMock);
    }

    public void testGetIpUIConnection() {
        IpUIConnection expected = new IpUIConnectionImpl(new UIGenericStub());

        try {

            uiManagerMock.getIpUIConnection(UiTestData.SLEE_TP_UI_IDENTIFIER);
            uiManagerControl.setReturnValue(expected);
            uiManagerControl.replay();

            IpUIConnection actual = connectionImpl
                    .getIpUIConnection(UiTestData.SLEE_TP_UI_IDENTIFIER);
            assertNotNull(actual);
            assertEquals(expected, actual);
        } catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
        uiManagerControl.verify();
    }

    public void testGetIpUICallConnection() {
        IpUICallConnection expected = new IpUICallConnectionImpl(
                new UICallStub());

        try {

            uiManagerMock
                    .getIpUICallConnection(UiTestData.SLEE_TP_UICALL_IDENTIFIER);
            uiManagerControl.setReturnValue(expected);
            uiManagerControl.replay();

            IpUIConnection actual = connectionImpl
                    .getIpUICallConnection(UiTestData.SLEE_TP_UICALL_IDENTIFIER);
            assertNotNull(actual);
            assertEquals(expected, actual);
        } catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
        uiManagerControl.verify();
    }

    public void testCloseConnection() throws ResourceException {

        uiManagerControl.replay();

        connectionImpl.closeConnection();

        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.createUI(TpAddress)'
     */
    public void testCreateUI() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        TpUIIdentifier expectedUiIdentifier = new TpUIIdentifier(
                UiTestData.UI_ID, UiTestData.UI_SESSION_ID);

        uiManagerMock.createUI(UiTestData.TP_ADDRESS);
        uiManagerControl.setReturnValue(expectedUiIdentifier);
        uiManagerControl.replay();

        TpUIIdentifier result = connectionImpl.createUI(UiTestData.TP_ADDRESS);
        assertEquals(expectedUiIdentifier, result);
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.createUICall(TpUITargetObject)'
     */
    public void testCreateUICall() throws TpCommonExceptions,
            P_INVALID_NETWORK_STATE, ResourceException {
        TpUICallIdentifier expectedUiCallIdentifier = new TpUICallIdentifier(
                UiTestData.UI_CALL_ID, UiTestData.UI_CALL_SESSION_ID);

        uiManagerMock.createUICall(UiTestData.SLEE_TP_UI_TARGET_OBJECT);
        uiManagerControl.setReturnValue(expectedUiCallIdentifier);
        uiManagerControl.replay();

        TpUICallIdentifier result = connectionImpl
                .createUICall(UiTestData.SLEE_TP_UI_TARGET_OBJECT);
        assertEquals(expectedUiCallIdentifier, result);
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.createNotification(TpUIEventCriteria)'
     */
    public void testCreateNotification() throws TpCommonExceptions,
            P_INVALID_CRITERIA, ResourceException {

        uiManagerMock.createNotification(UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT);
        uiManagerControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiManagerControl.replay();
        int result = connectionImpl
                .createNotification(UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT);
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.destroyNotification(int)'
     */
    public void testDestroyNotification() throws TpCommonExceptions,
            P_INVALID_ASSIGNMENT_ID, ResourceException {
        uiManagerMock.destroyNotification(UiTestData.ASSIGNMENT_ID);
        uiManagerControl.setVoidCallable();
        uiManagerControl.replay();
        connectionImpl.destroyNotification(UiTestData.ASSIGNMENT_ID);
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.changeNotification(int,
     * TpUIEventCriteria)'
     */
    public void testChangeNotification() throws TpCommonExceptions,
            P_INVALID_ASSIGNMENT_ID, P_INVALID_CRITERIA, ResourceException {
        uiManagerMock.changeNotification(UiTestData.ASSIGNMENT_ID,
                UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT);
        uiManagerControl.setVoidCallable();
        uiManagerControl.replay();
        connectionImpl.changeNotification(UiTestData.ASSIGNMENT_ID,
                UiTestData.TP_UI_EVENT_CRITERIA_DEL_RCPT);
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.getNotification()'
     */
    public void testGetNotification() throws TpCommonExceptions,
            ResourceException {
        uiManagerMock.getNotification();

        uiManagerControl
                .setReturnValue(UiTestData.TP_UI_EVENT_CRITERIA_RESULT_ARRAY);
        uiManagerControl.replay();

        org.csapi.ui.TpUIEventCriteriaResult[] result = connectionImpl
                .getNotification();
        assertEquals(UiTestData.TP_UI_EVENT_CRITERIA_RESULT_ARRAY, result);
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.enableNotifications()'
     */
    public void testEnableNotifications() throws TpCommonExceptions,
            ResourceException {
        uiManagerMock.enableNotifications();
        uiManagerControl.setReturnValue(UiTestData.ASSIGNMENT_ID);
        uiManagerControl.replay();

        int result = connectionImpl.enableNotifications();
        assertEquals(UiTestData.ASSIGNMENT_ID, result);
        uiManagerControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.IpUIManagerConnectionImpl.disableNotifications()'
     */
    public void testDisableNotifications() throws TpCommonExceptions,
            ResourceException {
        uiManagerMock.disableNotifications();
        uiManagerControl.setVoidCallable();
        uiManagerControl.replay();

        connectionImpl.disableNotifications();

        uiManagerControl.verify();
    }

}

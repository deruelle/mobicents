package org.mobicents.slee.resource.parlay.csapi.jr.ui;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.util.event.EventSender;

public class UiListenerImplTest extends TestCase {

    EventSender eventSenderMock = null;

    MockControl eventSenderControl = null;

    UiListenerImpl uiListenerImpl = null;

    protected void setUp() throws Exception {
        super.setUp();
        eventSenderControl = MockControl.createControl(EventSender.class);
        eventSenderMock = (EventSender) eventSenderControl.getMock();

        uiListenerImpl = new UiListenerImpl(eventSenderMock);
    }

    // Mgr events
    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onUserInteractionAbortedEvent(UserInteractionAbortedEvent)'
     */
    public void testOnUserInteractionAbortedEvent() {
        eventSenderMock.sendEvent(UiTestData.userInteractionAbortedEvent,
                UiTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();
        uiListenerImpl
                .onUserInteractionAbortedEvent(UiTestData.userInteractionAbortedEvent);
        eventSenderControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onUserInteractionNotificationInterruptedEvent(UserInteractionNotificationInterruptedEvent)'
     */
    public void testOnUserInteractionNotificationInterruptedEvent() {
        eventSenderMock.sendEvent(
                UiTestData.userInteractionNotificationInterruptedEvent,
                UiTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();
        uiListenerImpl
                .onUserInteractionNotificationInterruptedEvent(UiTestData.userInteractionNotificationInterruptedEvent);
        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onUserInteractionNotificationContinuedEvent(UserInteractionNotificationContinuedEvent)'
     */
    public void testOnUserInteractionNotificationContinuedEvent() {
        eventSenderMock.sendEvent(
                UiTestData.userInteractionNotificationContinuedEvent,
                UiTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();
        uiListenerImpl
                .onUserInteractionNotificationContinuedEvent(UiTestData.userInteractionNotificationContinuedEvent);
        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onReportNotificationEvent(ReportNotificationEvent)'
     */
    public void testOnReportNotificationEvent() {
        eventSenderMock.sendEvent(UiTestData.reportNotificationEvent,
                UiTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();
        uiListenerImpl
                .onReportNotificationEvent(UiTestData.reportNotificationEvent);
        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onReportEventNotificationEvent(ReportEventNotificationEvent)'
     */
    public void testOnReportEventNotificationEvent() {
        eventSenderMock.sendEvent(UiTestData.reportEventNotificationEvent,
                UiTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();
        uiListenerImpl
                .onReportEventNotificationEvent(UiTestData.reportEventNotificationEvent);
        eventSenderControl.verify();
    }

    // UIGeneric event
    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoResEvent(SendInfoResEvent)'
     */
    public void testOnSendInfoResEventForUiGeneric() {

        eventSenderMock.sendEvent(UiTestData.sendInfoResEvent,
                UiTestData.TP_UI_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl.onSendInfoResEvent(UiTestData.sendInfoResEvent);

        eventSenderControl.verify();

        eventSenderControl.reset();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoErrEvent(SendInfoErrEvent)'
     */
    public void testOnSendInfoErrEventForUiGeneric() {

        eventSenderMock.sendEvent(UiTestData.sendInfoErrEvent,
                UiTestData.TP_UI_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl.onSendInfoErrEvent(UiTestData.sendInfoErrEvent);

        eventSenderControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoAndCollectResEvent(SendInfoAndCollectResEvent)'
     */
    public void testOnSendInfoAndCollectResEventForUiGeneric() {
        eventSenderMock.sendEvent(UiTestData.sendInfoAndCollectResEvent,
                UiTestData.TP_UI_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onSendInfoAndCollectResEvent(UiTestData.sendInfoAndCollectResEvent);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoAndCollectErrEvent(SendInfoAndCollectErrEvent)'
     */
    public void testOnSendInfoAndCollectErrEventForUiGeneric() {
        eventSenderMock.sendEvent(UiTestData.sendInfoAndCollectErrEvent,
                UiTestData.TP_UI_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onSendInfoAndCollectErrEvent(UiTestData.sendInfoAndCollectErrEvent);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onUserInteractionFaultDetectedEvent(UserInteractionFaultDetectedEvent)'
     */
    public void testOnUserInteractionFaultDetectedEventForUiGeneric() {
        eventSenderMock.sendEvent(UiTestData.userInteractionFaultDetectedEvent,
                UiTestData.TP_UI_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onUserInteractionFaultDetectedEvent(UiTestData.userInteractionFaultDetectedEvent);

        eventSenderControl.verify();
    }

    // UICall tests (with an equivalent in UIGeneric)

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoResEvent(SendInfoResEvent)'
     */
    public void testOnSendInfoResEventForUiCall() {

        eventSenderMock.sendEvent(UiTestData.sendInfoResEventForUiCall,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl.onSendInfoResEvent(UiTestData.sendInfoResEventForUiCall);

        eventSenderControl.verify();

        eventSenderControl.reset();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoErrEvent(SendInfoErrEvent)'
     */
    public void testOnSendInfoErrEventUiCall() {

        eventSenderMock.sendEvent(UiTestData.sendInfoErrEventForUiCall,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl.onSendInfoErrEvent(UiTestData.sendInfoErrEventForUiCall);

        eventSenderControl.verify();

    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoAndCollectResEvent(SendInfoAndCollectResEvent)'
     */
    public void testOnSendInfoAndCollectResEventUiCall() {
        eventSenderMock.sendEvent(
                UiTestData.sendInfoAndCollectResEventForUiCall,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onSendInfoAndCollectResEvent(UiTestData.sendInfoAndCollectResEventForUiCall);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onSendInfoAndCollectErrEvent(SendInfoAndCollectErrEvent)'
     */
    public void testOnSendInfoAndCollectErrEventUiCall() {
        eventSenderMock.sendEvent(
                UiTestData.sendInfoAndCollectErrEventForUiCall,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onSendInfoAndCollectErrEvent(UiTestData.sendInfoAndCollectErrEventForUiCall);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onUserInteractionFaultDetectedEvent(UserInteractionFaultDetectedEvent)'
     */
    public void testOnUserInteractionFaultDetectedEventUiCall() {
        eventSenderMock.sendEvent(
                UiTestData.userInteractionFaultDetectedEventForUiCall,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onUserInteractionFaultDetectedEvent(UiTestData.userInteractionFaultDetectedEventForUiCall);

        eventSenderControl.verify();
    }

    // UICall events
    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onRecordMessageResEvent(RecordMessageResEvent)'
     */
    public void testOnRecordMessageResEvent() {
        eventSenderMock.sendEvent(UiTestData.recordMessageResEvent,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onRecordMessageResEvent(UiTestData.recordMessageResEvent);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onRecordMessageErrEvent(RecordMessageErrEvent)'
     */
    public void testOnRecordMessageErrEvent() {
        eventSenderMock.sendEvent(UiTestData.recordMessageErrEvent,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onRecordMessageErrEvent(UiTestData.recordMessageErrEvent);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onDeleteMessageResEvent(DeleteMessageResEvent)'
     */
    public void testOnDeleteMessageResEvent() {
        eventSenderMock.sendEvent(UiTestData.deleteMessageResEvent,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onDeleteMessageResEvent(UiTestData.deleteMessageResEvent);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onDeleteMessageErrEvent(DeleteMessageErrEvent)'
     */
    public void testOnDeleteMessageErrEvent() {
        eventSenderMock.sendEvent(UiTestData.deleteMessageErrEvent,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl
                .onDeleteMessageErrEvent(UiTestData.deleteMessageErrEvent);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onAbortActionResEvent(AbortActionResEvent)'
     */
    public void testOnAbortActionResEvent() {
        eventSenderMock.sendEvent(UiTestData.abortActionResEvent,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl.onAbortActionResEvent(UiTestData.abortActionResEvent);

        eventSenderControl.verify();
    }

    /*
     * Test method for
     * 'org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl.onAbortActionErrEvent(AbortActionErrEvent)'
     */
    public void testOnAbortActionErrEvent() {
        eventSenderMock.sendEvent(UiTestData.abortActionErrEvent,
                UiTestData.TP_UICALL_ACTIVITY_HANDLE);
        eventSenderControl.replay();

        uiListenerImpl.onAbortActionErrEvent(UiTestData.abortActionErrEvent);

        eventSenderControl.verify();
    }

}

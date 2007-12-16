
package org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.util.event.EventSender;

/**
 *
 **/
public class GccsListenerImplTest extends TestCase {

    EventSender eventSender = null;

    MockControl eventSenderControl = null;
    
    GccsListenerImpl gccsListenerImpl = null;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        

        eventSenderControl = MockControl.createControl(EventSender.class);

        eventSender = (EventSender) eventSenderControl.getMock();
        
        gccsListenerImpl = new GccsListenerImpl(eventSender);
    }

    public void testOnCallAbortedEvent() {
        eventSender.sendEvent(GccsTestData.callAbortedEvent,
                GccsTestData.PARLAY_ACTIVITY_HANDLER);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallAbortedEvent(GccsTestData.callAbortedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallEndedEvent() {
        eventSender.sendEvent(GccsTestData.callEndedEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallEndedEvent(GccsTestData.callEndedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallEventNotifiyEvent() {
        eventSender.sendEvent(GccsTestData.callEventNotifyEvent,
                GccsTestData.PARLAY_ACTIVITY_HANDLER);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallEventNotifiyEvent(GccsTestData.callEventNotifyEvent);

        eventSenderControl.verify();
    }

    public void testOnCallFaultDetectedEvent() {
        eventSender.sendEvent(GccsTestData.callFaultDetectedEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallFaultDetectedEvent(GccsTestData.callFaultDetectedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallNotificationContinuedEvent() {
        eventSender.sendEvent(GccsTestData.callNotificationContinuedEvent,
                GccsTestData.PARLAY_ACTIVITY_HANDLER);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallNotificationContinuedEvent(GccsTestData.callNotificationContinuedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallNotificationInterruptedEvent() {
        eventSender.sendEvent(GccsTestData.callNotificationInterruptedEvent,
                GccsTestData.PARLAY_ACTIVITY_HANDLER);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallNotificationInterruptedEvent(GccsTestData.callNotificationInterruptedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallOverloadCeasedEvent() {
        eventSender.sendEvent(GccsTestData.callOverloadCeasedEvent,
                GccsTestData.PARLAY_ACTIVITY_HANDLER);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallOverloadCeasedEvent(GccsTestData.callOverloadCeasedEvent);

        eventSenderControl.verify();
    }

    public void testOnGetCallInfoErrEvent() {
        eventSender.sendEvent(GccsTestData.callInfoErrEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onGetCallInfoErrEvent(GccsTestData.callInfoErrEvent);

        eventSenderControl.verify();
    }

    public void testOnGetCallInfoResEvent() {
        eventSender.sendEvent(GccsTestData.callInfoResEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onGetCallInfoResEvent(GccsTestData.callInfoResEvent);

        eventSenderControl.verify();
    }

    public void testOnGetMoreDialledDigitsErrEvent() {
        eventSender.sendEvent(GccsTestData.getMoreDialledDigitsErrEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onGetMoreDialledDigitsErrEvent(GccsTestData.getMoreDialledDigitsErrEvent);

        eventSenderControl.verify();
    }

    public void testOnGetMoreDialledDigitsResEvent() {
        eventSender.sendEvent(GccsTestData.getMoreDialledDigitsResEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onGetMoreDialledDigitsResEvent(GccsTestData.getMoreDialledDigitsResEvent);

        eventSenderControl.verify();
    }

    public void testOnRouteErrEvent() {
        eventSender.sendEvent(GccsTestData.routeErrEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onRouteErrEvent(GccsTestData.routeErrEvent);

        eventSenderControl.verify();
    }

    public void testOnRouteResEvent() {
        eventSender.sendEvent(GccsTestData.routeResEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onRouteResEvent(GccsTestData.routeResEvent);

        eventSenderControl.verify();
    }

    public void testOnSuperviseCallErrEvent() {
        eventSender.sendEvent(GccsTestData.superviseCallErrEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onSuperviseCallErrEvent(GccsTestData.superviseCallErrEvent);

        eventSenderControl.verify();
    }

    public void testOnSuperviseCallResEvent() {
        eventSender.sendEvent(GccsTestData.superviseCallResEvent,
                GccsTestData.TP_CALL_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        gccsListenerImpl
                .onSuperviseCallResEvent(GccsTestData.superviseCallResEvent);

        eventSenderControl.verify();
    }

    public void testOnCallOverloadEncountered() {
        eventSender.sendEvent(GccsTestData.callOverloadEncountered,
                GccsTestData.PARLAY_ACTIVITY_HANDLER);

        eventSenderControl.replay();

        gccsListenerImpl
                .onCallOverloadEncounteredEvent(GccsTestData.callOverloadEncountered);

        eventSenderControl.verify();
    }

}

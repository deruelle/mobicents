package org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs;

import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.util.event.EventSender;

import junit.framework.TestCase;

/**
 * 
 * Class Description for MpccsListenerImplTest
 */
public class MpccsListenerImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        eventSenderControl = MockControl.createControl(EventSender.class);

        eventSender = (EventSender) eventSenderControl.getMock();

        mpccsListenerImpl = new MpccsListenerImpl(eventSender);
    }

    MpccsListenerImpl mpccsListenerImpl = null;

    EventSender eventSender = null;

    MockControl eventSenderControl = null;

    public void testOnAttachMediaErrEvent() {

        eventSender.sendEvent(MpccsTestData.attachMediaErrEvent,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl
                .onAttachMediaErrEvent(MpccsTestData.attachMediaErrEvent);

        eventSenderControl.verify();
    }

    public void testOnAttachMediaResEvent() {

        eventSender.sendEvent(MpccsTestData.attachMediaResEvent,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl
                .onAttachMediaResEvent(MpccsTestData.attachMediaResEvent);

        eventSenderControl.verify();
    }

    public void testOnCallAbortedEvent() {

        eventSender.sendEvent(MpccsTestData.callAbortedEvent,
                MpccsTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        mpccsListenerImpl.onCallAbortedEvent(MpccsTestData.callAbortedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallEndedEvent() {

        eventSender.sendEvent(MpccsTestData.callEndedEvent,
                MpccsTestData.callActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onCallEndedEvent(MpccsTestData.callEndedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallLegEndedEvent() {

        eventSender.sendEvent(MpccsTestData.callLegEndedEvent,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onCallLegEndedEvent(MpccsTestData.callLegEndedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallOverloadCeasedEvent() {

        eventSender.sendEvent(MpccsTestData.callOverloadCeasedEvent,
                MpccsTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        mpccsListenerImpl
                .onCallOverloadCeasedEvent(MpccsTestData.callOverloadCeasedEvent);

        eventSenderControl.verify();
    }

    public void testOnCallOverloadEncounteredEvent() {

        eventSender.sendEvent(MpccsTestData.callOverloadEncounteredEvent,
                MpccsTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        mpccsListenerImpl
                .onCallOverloadEncounteredEvent(MpccsTestData.callOverloadEncounteredEvent);

        eventSenderControl.verify();
    }

    public void testOnCreateAndRouteCallLegErrEvent() {

        eventSender.sendEvent(MpccsTestData.createAndRouteCallLegErrEvent,
                MpccsTestData.callActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onCreateAndRouteCallLegErrEvent(MpccsTestData.createAndRouteCallLegErrEvent);

        eventSenderControl.verify();
    }

    public void testOnDetachMediaErrEvent() {

        eventSender.sendEvent(MpccsTestData.detachMediaErrEvent,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onDetachMediaErrEvent(MpccsTestData.detachMediaErrEvent);

        eventSenderControl.verify();
    }

    public void testOnDetachMediaResEvent() {

        eventSender.sendEvent(MpccsTestData.detachMediaResEvent,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onDetachMediaResEvent(MpccsTestData.detachMediaResEvent);

        eventSenderControl.verify();
    }

    public void testOnEventReportErrEvent() {

        eventSender.sendEvent(MpccsTestData.eventReportErrEvent,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onEventReportErrEvent(MpccsTestData.eventReportErrEvent);

        eventSenderControl.verify();
    }

    public void testOnEventReportResEvent() {

        eventSender.sendEvent(MpccsTestData.eventReportResEvent,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onEventReportResEvent(MpccsTestData.eventReportResEvent);

        eventSenderControl.verify();
    }

    public void testOnGetInfoErrEvent() {

        eventSender.sendEvent(MpccsTestData.getInfoErrEvent,
                MpccsTestData.callActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onGetInfoErrEvent(MpccsTestData.getInfoErrEvent);

        eventSenderControl.verify();

        eventSenderControl.reset();

        eventSender.sendEvent(MpccsTestData.getInfoErrEvent2,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onGetInfoErrEvent(MpccsTestData.getInfoErrEvent2);

        eventSenderControl.verify();
    }

    public void testOnGetInfoResEvent() {

        eventSender.sendEvent(MpccsTestData.getInfoResEvent,
                MpccsTestData.callActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onGetInfoResEvent(MpccsTestData.getInfoResEvent);

        eventSenderControl.verify();

        eventSenderControl.reset();

        eventSender.sendEvent(MpccsTestData.getInfoResEvent2,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onGetInfoResEvent(MpccsTestData.getInfoResEvent2);

        eventSenderControl.verify();
    }

    public void testOnManagerInterruptedEvent() {

        eventSender.sendEvent(MpccsTestData.managerInterruptedEvent,
                MpccsTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        mpccsListenerImpl
                .onManagerInterruptedEvent(MpccsTestData.managerInterruptedEvent);

        eventSenderControl.verify();
    }

    public void testOnManagerResumedEvent() {

        eventSender.sendEvent(MpccsTestData.managerResumedEvent,
                MpccsTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        mpccsListenerImpl
                .onManagerResumedEvent(MpccsTestData.managerResumedEvent);

        eventSenderControl.verify();
    }

    public void testOnReportNotificationEvent() {

        eventSender.sendEvent(MpccsTestData.reportNotificationEvent,
                MpccsTestData.SERVICE_ACTIVITY_HANDLE);

        eventSenderControl.replay();

        mpccsListenerImpl
                .onReportNotificationEvent(MpccsTestData.reportNotificationEvent);

        eventSenderControl.verify();
    }

    public void testOnRouteErrEvent() {

        eventSender.sendEvent(MpccsTestData.routeErrEvent,
                MpccsTestData.callActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onRouteErrEvent(MpccsTestData.routeErrEvent);

        eventSenderControl.verify();
    }

    public void testOnSuperviseErrEvent() {

        eventSender.sendEvent(MpccsTestData.superviseErrEvent,
                MpccsTestData.callActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onSuperviseErrEvent(MpccsTestData.superviseErrEvent);

        eventSenderControl.verify();

        eventSenderControl.reset();

        eventSender.sendEvent(MpccsTestData.superviseErrEvent2,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onSuperviseErrEvent(MpccsTestData.superviseErrEvent2);

        eventSenderControl.verify();
    }

    public void testOnSuperviseResEvent() {

        eventSender.sendEvent(MpccsTestData.superviseResEvent,
                MpccsTestData.callActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onSuperviseResEvent(MpccsTestData.superviseResEvent);

        eventSenderControl.verify();

        eventSenderControl.reset();

        eventSender.sendEvent(MpccsTestData.superviseResEvent2,
                MpccsTestData.callLegActivityHandle);

        eventSenderControl.replay();

        mpccsListenerImpl.onSuperviseResEvent(MpccsTestData.superviseResEvent2);

        eventSenderControl.verify();
    }

}
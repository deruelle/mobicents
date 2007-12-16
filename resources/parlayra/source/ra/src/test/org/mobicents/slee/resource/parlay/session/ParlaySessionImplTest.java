package org.mobicents.slee.resource.parlay.session;

import java.util.Properties;

import junit.framework.TestCase;

import org.csapi.IpService;
import org.csapi.fw.TpProperty;
import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.gccs.CallEventNotifyEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallFaultDetectedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallNotificationContinuedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.CallNotificationInterruptedEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.RouteResEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallErrEvent;
import org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.AttachMediaErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.AttachMediaResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallAbortedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallEndedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallLegEndedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallOverloadCeasedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CallOverloadEncounteredEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.CreateAndRouteCallLegErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.DetachMediaErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.DetachMediaResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.EventReportErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.EventReportResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.GetInfoErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.GetInfoResEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.ManagerInterruptedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.ManagerResumedEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.ReportNotificationEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.RouteErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.SuperviseErrEvent;
import org.mobicents.csapi.jr.slee.cc.mpccs.SuperviseResEvent;
import org.mobicents.csapi.jr.slee.fw.TerminateAccessEvent;
import org.mobicents.csapi.jr.slee.fw.TerminateServiceAgreementEvent;
import org.mobicents.csapi.jr.slee.ui.AbortActionErrEvent;
import org.mobicents.csapi.jr.slee.ui.AbortActionResEvent;
import org.mobicents.csapi.jr.slee.ui.DeleteMessageErrEvent;
import org.mobicents.csapi.jr.slee.ui.DeleteMessageResEvent;
import org.mobicents.csapi.jr.slee.ui.RecordMessageErrEvent;
import org.mobicents.csapi.jr.slee.ui.RecordMessageResEvent;
import org.mobicents.csapi.jr.slee.ui.ReportEventNotificationEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoAndCollectErrEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoAndCollectResEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoErrEvent;
import org.mobicents.csapi.jr.slee.ui.SendInfoResEvent;
import org.mobicents.csapi.jr.slee.ui.UserInteractionAbortedEvent;
import org.mobicents.csapi.jr.slee.ui.UserInteractionFaultDetectedEvent;
import org.mobicents.csapi.jr.slee.ui.UserInteractionNotificationContinuedEvent;
import org.mobicents.csapi.jr.slee.ui.UserInteractionNotificationInterruptedEvent;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayServiceActivityHandle;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListener;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListenerImpl;
import org.mobicents.slee.resource.parlay.fw.FwSession;
import org.mobicents.slee.resource.parlay.fw.FwSessionException;
import org.mobicents.slee.resource.parlay.fw.FwSessionProperties;
import org.mobicents.slee.resource.parlay.fw.ServiceAndToken;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

/**
 * 
 * Class Description for ParlaySessionImplTest
 */
public class ParlaySessionImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        fwSessionControl = MockControl.createControl(FwSession.class);

        mockFwSession = (FwSession) fwSessionControl.getMock();

        activityManagerControl = MockControl
                .createControl(ActivityManager.class);

        activityManager = (ActivityManager) activityManagerControl.getMock();

        FwSessionProperties fwSessionProperties = new FwSessionProperties();

        // override init to inject mock FwSession
        parlaySessionImpl = new ParlaySessionImpl(fwSessionProperties,
                activityManager) {
            /*
             * (non-Javadoc)
             * 
             * @see org.mobicents.slee.resource.parlay.session.ParlaySessionImpl#init()
             */
            public void init() throws FwSessionException {
                fwSession = ParlaySessionImplTest.mockFwSession;

            }
        };
        parlaySessionImpl.init();
    }

    MockControl fwSessionControl;

    static FwSession mockFwSession;

    ActivityManager activityManager;

    MockControl activityManagerControl;

    ParlaySessionImpl parlaySessionImpl;

    public void testInit() {
        // Need to test this in isolation from underlying corba

        FwSessionProperties fwSessionProperties = new FwSessionProperties();

        try {
            parlaySessionImpl = new ParlaySessionImpl(fwSessionProperties,
                    activityManager);
            parlaySessionImpl.init();
        }
        catch (FwSessionException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDestroy() {

        fwSessionControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        try {
            mockFwSession.endAccess(new TpProperty[0]);
        }
        catch (FwSessionException e) {
            fail();
        }
        mockFwSession.shutdown();
        mockFwSession.removeFwSessionListener(null);
        fwSessionControl.replay();

        try {
            parlaySessionImpl.init();
        }
        catch (FwSessionException e) {
            e.printStackTrace();
            fail();
        }
        parlaySessionImpl.destroy();

        fwSessionControl.verify();
    }

    public void testGetService() {
        fwSessionControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);

        IpService ipService = new IpMultiPartyCallControlManagerStub();
        ServiceAndToken serviceAndToken = new ServiceAndToken(ipService, "B");
        ORB orb = new ORBStub();
        POA poa = new POAStub();
        String serviceTypeName = "P_MULTI_PARTY_CALL_CONTROL";
        Properties properties = new Properties();
        properties.put("PropName", "PropValue");

        TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(
                serviceAndToken.getServiceToken().hashCode());
        ParlayServiceActivityHandle serviceActivityHandle = new ParlayServiceActivityHandle(
                serviceIdentifier);

        try {
            mockFwSession.getORB();
            fwSessionControl.setReturnValue(orb);

            mockFwSession.getRootPOA();
            fwSessionControl.setReturnValue(poa, 7);

            mockFwSession.getService(serviceTypeName, null);
            fwSessionControl.setReturnValue(serviceAndToken);

            activityManager.add(serviceActivityHandle, serviceIdentifier);
            activityManager.activityStartedSuspended(serviceActivityHandle);
        }
        catch (FwSessionException e) {
            fail();
        }
        fwSessionControl.replay();

        activityManagerControl.replay();

        try {
            // call twice to ensure map persists the first one correctly
            TpServiceIdentifier serviceIdentifier1 = parlaySessionImpl
                    .getService(serviceTypeName, properties);
            TpServiceIdentifier serviceIdentifier2 = parlaySessionImpl
                    .getService(serviceTypeName, properties);

            assertEquals(serviceIdentifier1, serviceIdentifier2);
        }
        catch (FwSessionException e1) {
            fail();
        }
        catch (javax.slee.resource.ResourceException e1) {
            fail();
        }

        fwSessionControl.verify();

        activityManagerControl.verify();
    }

    public void testGetServiceSession() {
        fwSessionControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);

        IpService ipService = new IpCallControlManagerStub();
        ServiceAndToken serviceAndToken = new ServiceAndToken(ipService, "B");
        ORB orb = new ORBStub();
        POA poa = new POAStub();
        String serviceTypeName = "P_GENERIC_CALL_CONTROL";
        Properties properties = new Properties();
        properties.put("PropName", "PropValue");

        try {
            mockFwSession.getORB();
            fwSessionControl.setReturnValue(orb);

            mockFwSession.getRootPOA();
            fwSessionControl.setReturnValue(poa, 5);

            mockFwSession.getService(serviceTypeName, null);
            fwSessionControl.setReturnValue(serviceAndToken);
        }
        catch (FwSessionException e) {
            fail();
        }
        fwSessionControl.replay();

        try {
            // call twice to ensure map persists the first one correctly
            TpServiceIdentifier serviceIdentifier = parlaySessionImpl
                    .getService(serviceTypeName, properties);
            TpServiceIdentifier serviceIdentifier2 = parlaySessionImpl
                    .getService(serviceTypeName, properties);

            assertEquals(serviceIdentifier, serviceIdentifier2);

            ServiceSession serviceSession = parlaySessionImpl
                    .getServiceSession(serviceIdentifier);
            ;

            assertNotNull(serviceSession);
            assertEquals(ServiceSession.GenericCallControl, serviceSession
                    .getType());
        }
        catch (FwSessionException e1) {
            fail();
        }
        catch (javax.slee.resource.ResourceException e1) {
            fail();
        }

        fwSessionControl.verify();
    }

    public void testTerminateAccess() {
        fwSessionControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);

        mockFwSession.shutdown();
        mockFwSession.removeFwSessionListener(null);
        fwSessionControl.replay();

        TerminateAccessEvent event = new TerminateAccessEvent("Blah", "NULL",
                new byte[0]);
        parlaySessionImpl.terminateAccess(event);

        fwSessionControl.verify();
    }

    public void testTerminateServiceAgreement() {
        fwSessionControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);

        try {
            mockFwSession.endAccess(null);
            mockFwSession.shutdown();
            mockFwSession.removeFwSessionListener(null);
        }
        catch (FwSessionException e) {
            fail();
        }
        fwSessionControl.replay();

        TerminateServiceAgreementEvent event = new TerminateServiceAgreementEvent(
                "Token", "Blah");
        parlaySessionImpl.terminateServiceAgreement(event);

        fwSessionControl.verify();
    }

    public void testGetFwSession() {
        assertEquals(mockFwSession, parlaySessionImpl.getFwSession());
    }

    public void testListeners() {
        MpccsListener listener = new MpccsListenerImpl();
        parlaySessionImpl.setMpccsListener(listener);
        assertEquals(listener, parlaySessionImpl.getMpccsListener());
        
        GccsListener gccsListener = new GccsListenerImpl();
        parlaySessionImpl.setGccsListener(gccsListener);
        assertEquals(gccsListener, parlaySessionImpl.getGccsListener());
        
        UiListener uiListener = new UiListenerImpl();
        parlaySessionImpl.setUiListener(uiListener);
        assertEquals(uiListener, parlaySessionImpl.getUiListener());
    }
    private class UiListenerImpl implements UiListener{

        public void onUserInteractionAbortedEvent(UserInteractionAbortedEvent event) {
            //  Auto-generated method stub
            
        }

        public void onUserInteractionNotificationInterruptedEvent(UserInteractionNotificationInterruptedEvent event) {
            //  Auto-generated method stub
            
        }

        public void onUserInteractionNotificationContinuedEvent(UserInteractionNotificationContinuedEvent event) {
            //  Auto-generated method stub
            
        }

        public void onReportNotificationEvent(org.mobicents.csapi.jr.slee.ui.ReportNotificationEvent event) {
            //  Auto-generated method stub
            
        }

        public void onReportEventNotificationEvent(ReportEventNotificationEvent event) {
            //  Auto-generated method stub
            
        }

        public void onSendInfoResEvent(SendInfoResEvent event) {
            //  Auto-generated method stub
            
        }

        public void onSendInfoErrEvent(SendInfoErrEvent event) {
            //  Auto-generated method stub
            
        }

        public void onSendInfoAndCollectResEvent(SendInfoAndCollectResEvent event) {
            //  Auto-generated method stub
            
        }

        public void onSendInfoAndCollectErrEvent(SendInfoAndCollectErrEvent event) {
            //  Auto-generated method stub
            
        }

        public void onUserInteractionFaultDetectedEvent(UserInteractionFaultDetectedEvent event) {
            //  Auto-generated method stub
            
        }

        public void onRecordMessageResEvent(RecordMessageResEvent event) {
            //  Auto-generated method stub
            
        }

        public void onRecordMessageErrEvent(RecordMessageErrEvent event) {
            //  Auto-generated method stub
            
        }

        public void onDeleteMessageResEvent(DeleteMessageResEvent event) {
            //  Auto-generated method stub
            
        }

        public void onDeleteMessageErrEvent(DeleteMessageErrEvent event) {
            //  Auto-generated method stub
            
        }

        public void onAbortActionResEvent(AbortActionResEvent event) {
            //  Auto-generated method stub
            
        }

        public void onAbortActionErrEvent(AbortActionErrEvent event) {
            //  Auto-generated method stub
            
        }
        
    }
    private class GccsListenerImpl implements GccsListener {

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallAbortedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallAbortedEvent)
         */
        public void onCallAbortedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallAbortedEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallEndedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallEndedEvent)
         */
        public void onCallEndedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallEndedEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallEventNotifiyEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallEventNotifyEvent)
         */
        public void onCallEventNotifiyEvent(CallEventNotifyEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallFaultDetectedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallFaultDetectedEvent)
         */
        public void onCallFaultDetectedEvent(CallFaultDetectedEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallNotificationContinuedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallNotificationContinuedEvent)
         */
        public void onCallNotificationContinuedEvent(CallNotificationContinuedEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallNotificationInterruptedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallNotificationInterruptedEvent)
         */
        public void onCallNotificationInterruptedEvent(CallNotificationInterruptedEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallOverloadEncounteredEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallOverloadEncounteredEvent)
         */
        public void onCallOverloadEncounteredEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallOverloadEncounteredEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onCallOverloadCeasedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallOverloadCeasedEvent)
         */
        public void onCallOverloadCeasedEvent(org.mobicents.csapi.jr.slee.cc.gccs.CallOverloadCeasedEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onGetCallInfoErrEvent(org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoErrEvent)
         */
        public void onGetCallInfoErrEvent(GetCallInfoErrEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onGetCallInfoResEvent(org.mobicents.csapi.jr.slee.cc.gccs.GetCallInfoResEvent)
         */
        public void onGetCallInfoResEvent(GetCallInfoResEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onGetMoreDialledDigitsErrEvent(org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsErrEvent)
         */
        public void onGetMoreDialledDigitsErrEvent(GetMoreDialledDigitsErrEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onGetMoreDialledDigitsResEvent(org.mobicents.csapi.jr.slee.cc.gccs.GetMoreDialledDigitsResEvent)
         */
        public void onGetMoreDialledDigitsResEvent(GetMoreDialledDigitsResEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onRouteErrEvent(org.mobicents.csapi.jr.slee.cc.gccs.RouteErrEvent)
         */
        public void onRouteErrEvent(org.mobicents.csapi.jr.slee.cc.gccs.RouteErrEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onRouteResEvent(org.mobicents.csapi.jr.slee.cc.gccs.RouteResEvent)
         */
        public void onRouteResEvent(RouteResEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onSuperviseCallErrEvent(org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallErrEvent)
         */
        public void onSuperviseCallErrEvent(SuperviseCallErrEvent event) {
            // Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener#onSuperviseCallResEvent(org.mobicents.csapi.jr.slee.cc.gccs.SuperviseCallResEvent)
         */
        public void onSuperviseCallResEvent(SuperviseCallResEvent event) {
            // Auto-generated method stub
            
        }
        
    }

    private class MpccsListenerImpl implements MpccsListener {

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onAttachMediaErrEvent(org.mobicents.csapi.jr.slee.cc.mpccs.AttachMediaErrEvent)
         */
        public void onAttachMediaErrEvent(AttachMediaErrEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onAttachMediaResEvent(org.mobicents.csapi.jr.slee.cc.mpccs.AttachMediaResEvent)
         */
        public void onAttachMediaResEvent(AttachMediaResEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onCallAbortedEvent(org.mobicents.csapi.jr.slee.cc.mpccs.CallAbortedEvent)
         */
        public void onCallAbortedEvent(CallAbortedEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onCallEndedEvent(org.mobicents.csapi.jr.slee.cc.mpccs.CallEndedEvent)
         */
        public void onCallEndedEvent(CallEndedEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onCallLegEndedEvent(org.mobicents.csapi.jr.slee.cc.mpccs.CallLegEndedEvent)
         */
        public void onCallLegEndedEvent(CallLegEndedEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onCallOverloadCeasedEvent(org.mobicents.csapi.jr.slee.cc.mpccs.CallOverloadCeasedEvent)
         */
        public void onCallOverloadCeasedEvent(CallOverloadCeasedEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onCallOverloadEncounteredEvent(org.mobicents.csapi.jr.slee.cc.mpccs.CallOverloadEncounteredEvent)
         */
        public void onCallOverloadEncounteredEvent(
                CallOverloadEncounteredEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onCreateAndRouteCallLegErrEvent(org.mobicents.csapi.jr.slee.cc.mpccs.CreateAndRouteCallLegErrEvent)
         */
        public void onCreateAndRouteCallLegErrEvent(
                CreateAndRouteCallLegErrEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onDetachMediaErrEvent(org.mobicents.csapi.jr.slee.cc.mpccs.DetachMediaErrEvent)
         */
        public void onDetachMediaErrEvent(DetachMediaErrEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onDetachMediaResEvent(org.mobicents.csapi.jr.slee.cc.mpccs.DetachMediaResEvent)
         */
        public void onDetachMediaResEvent(DetachMediaResEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onEventReportErrEvent(org.mobicents.csapi.jr.slee.cc.mpccs.EventReportErrEvent)
         */
        public void onEventReportErrEvent(EventReportErrEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onEventReportResEvent(org.mobicents.csapi.jr.slee.cc.mpccs.EventReportResEvent)
         */
        public void onEventReportResEvent(EventReportResEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onGetInfoErrEvent(org.mobicents.csapi.jr.slee.cc.mpccs.GetInfoErrEvent)
         */
        public void onGetInfoErrEvent(GetInfoErrEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onGetInfoResEvent(org.mobicents.csapi.jr.slee.cc.mpccs.GetInfoResEvent)
         */
        public void onGetInfoResEvent(GetInfoResEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onManagerInterruptedEvent(org.mobicents.csapi.jr.slee.cc.mpccs.ManagerInterruptedEvent)
         */
        public void onManagerInterruptedEvent(ManagerInterruptedEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onManagerResumedEvent(org.mobicents.csapi.jr.slee.cc.mpccs.ManagerResumedEvent)
         */
        public void onManagerResumedEvent(ManagerResumedEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onReportNotificationEvent(org.mobicents.csapi.jr.slee.cc.mpccs.ReportNotificationEvent)
         */
        public void onReportNotificationEvent(ReportNotificationEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onRouteErrEvent(org.mobicents.csapi.jr.slee.cc.mpccs.RouteErrEvent)
         */
        public void onRouteErrEvent(RouteErrEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onSuperviseErrEvent(org.mobicents.csapi.jr.slee.cc.mpccs.SuperviseErrEvent)
         */
        public void onSuperviseErrEvent(SuperviseErrEvent event) {
            //  Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener#onSuperviseResEvent(org.mobicents.csapi.jr.slee.cc.mpccs.SuperviseResEvent)
         */
        public void onSuperviseResEvent(SuperviseResEvent event) {
            //  Auto-generated method stub

        }

    }

}
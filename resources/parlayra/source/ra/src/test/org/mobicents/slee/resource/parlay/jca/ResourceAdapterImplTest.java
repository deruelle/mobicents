package org.mobicents.slee.resource.parlay.jca;

import javax.resource.ResourceException;
import javax.resource.spi.ResourceAdapterInternalException;

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
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManager;
import org.mobicents.slee.resource.parlay.util.activity.ActivityManagerImpl;

import junit.framework.TestCase;

/**
 *
 * Class Description for ResourceAdapterImplTest
 */
public class ResourceAdapterImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        resourceAdapterImpl = new ResourceAdapterImpl();
        
        resourceAdapterImpl.setAuthenticationSequence("ONE_WAY");
        
        resourceAdapterImpl.setDomainID("1234");
        
        resourceAdapterImpl.setIpInitialIOR("");
        
        resourceAdapterImpl.setIpInitialLocation("");
        
        resourceAdapterImpl.setIpInitialURL("");
        
        resourceAdapterImpl.setNamingServiceIOR("");
        
        resourceAdapterImpl.setParlayVersion("P_PARLAY_4");
        
        resourceAdapterImpl.setSharedSecret("Shared Secret");
    }
    
    ResourceAdapterImpl resourceAdapterImpl;

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testHashCode() {
        resourceAdapterImpl.hashCode();
    }

    public void testStart() {
        
        try {
            resourceAdapterImpl.start(null);
        }
        catch (ResourceAdapterInternalException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testStop() {
        
        try {
            resourceAdapterImpl.start(null);
            resourceAdapterImpl.stop();
        }
        catch (ResourceAdapterInternalException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testEndpointActivation() {
        try {
            resourceAdapterImpl.endpointActivation(null, null);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testEndpointDeactivation() {
        resourceAdapterImpl.endpointDeactivation(null, null);
    }

    public void testGetXAResources() {
        try {
            resourceAdapterImpl.getXAResources(null);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetParlaySession() {
        
        try {
            assertNull(resourceAdapterImpl.getParlaySession());
            resourceAdapterImpl.start(null);
            assertNotNull(resourceAdapterImpl.getParlaySession());
        }
        catch (ResourceAdapterInternalException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetAuthenticationSequence() {
        assertEquals("ONE_WAY", resourceAdapterImpl.getAuthenticationSequence());
    }

    public void testGetDomainID() {
        assertEquals("1234", resourceAdapterImpl.getDomainID());
    }

    public void testGetIpInitialIOR() {
        assertEquals("", resourceAdapterImpl.getIpInitialIOR());
    }

    public void testGetParlayVersion() {
        assertEquals("P_PARLAY_4", resourceAdapterImpl.getParlayVersion());
    }
    
    public void testGetSharedSecret() {
        assertEquals("Shared Secret", resourceAdapterImpl.getSharedSecret());
    }
    
    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        
        ResourceAdapterImpl resourceAdapterImpl2 = new ResourceAdapterImpl();
        
        resourceAdapterImpl2.setAuthenticationSequence("ONE_WAY");
        
        resourceAdapterImpl2.setDomainID("1234");
        
        resourceAdapterImpl2.setIpInitialIOR("");
        
        resourceAdapterImpl2.setIpInitialLocation("");
        
        resourceAdapterImpl2.setIpInitialURL("");
        
        resourceAdapterImpl2.setNamingServiceIOR("");
        
        resourceAdapterImpl2.setParlayVersion("P_PARLAY_4");
        
        resourceAdapterImpl2.setSharedSecret("Shared Secret");
        
        assertEquals(resourceAdapterImpl2, resourceAdapterImpl);
        
        resourceAdapterImpl2.setNamingServiceIOR("1");
        
        assertTrue(resourceAdapterImpl2 != resourceAdapterImpl);
    }

    public void testSetParlaySession() {
        
        try {
            assertNull(resourceAdapterImpl.getParlaySession());
            resourceAdapterImpl.start(null);
            assertNotNull(resourceAdapterImpl.getParlaySession());
            resourceAdapterImpl.setParlaySession(null);
            assertNull(resourceAdapterImpl.getParlaySession());
        }
        catch (ResourceAdapterInternalException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetMpccsListener() {
        MpccsListenerImpl listenerImpl = new MpccsListenerImpl();
        
        resourceAdapterImpl.setMpccsListener(listenerImpl);
        
        assertEquals(listenerImpl, resourceAdapterImpl.getMpccsListener());
    }

    public void testSetActivityManager() {
        ActivityManager activityManager = new ActivityManagerImpl(null);
        
        resourceAdapterImpl.setActivityManager(activityManager);
        
        assertEquals(activityManager, resourceAdapterImpl.getActivityManager());
    }

    public void testGetIpInitialLocation() {
        assertEquals("", resourceAdapterImpl.getIpInitialLocation());
    }

    public void testGetIpInitialURL() {
        assertEquals("", resourceAdapterImpl.getIpInitialURL());
    }

    public void testGetNamingServiceIOR() {
        assertEquals("", resourceAdapterImpl.getNamingServiceIOR());
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

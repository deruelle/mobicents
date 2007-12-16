package org.mobicents.slee.resource.parlay;

import java.util.Timer;

import javax.resource.cci.ConnectionFactory;
import javax.slee.SLEEException;
import javax.slee.facilities.AlarmFacility;
import javax.slee.facilities.EventLookupFacility;
import javax.slee.facilities.Tracer;
import javax.slee.profile.ProfileFacility;
import javax.slee.resource.BootstrapContext;
import javax.slee.resource.ResourceException;
import javax.slee.resource.SleeEndpoint;
import javax.slee.transaction.SleeTransactionManager;

import junit.framework.TestCase;

import org.mobicents.csapi.jr.slee.ParlayConnection;
import org.mobicents.slee.resource.parlay.jca.ConnectionFactoryImpl;
import org.mobicents.slee.resource.parlay.jca.ResourceAdapterImpl;

/**
 *
 * Class Description for ParlayProviderImplTest
 */
public class ParlayProviderImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        ParlayResourceAdaptorProperties properties = new ParlayResourceAdaptorProperties();
        
        properties.loadDefaults();
        
        bootstrapContext = new BootstrapContext() {
            public String getEntityName() {
                //  Auto-generated method stub
                return null;
            }

            public SleeEndpoint getSleeEndpoint() {
                //  Auto-generated method stub
                return null;
            }

            public Tracer getTracer(String arg0) throws NullPointerException,
                    IllegalArgumentException, SLEEException {
                //  Auto-generated method stub
                return null;
            }

            public AlarmFacility getAlarmFacility() {
                //  Auto-generated method stub
                return null;
            }

            public EventLookupFacility getEventLookupFacility() {
                //  Auto-generated method stub
                return null;
            }

            public Timer getTimer() {
                //  Auto-generated method stub
                return null;
            }

            public SleeTransactionManager getSleeTransactionManager() {
                //  Auto-generated method stub
                return null;
            }

            public ProfileFacility getProfileFacility() {
                //  Auto-generated method stub
                return null;
            }
        };
        
        providerImpl = new ParlayProviderImpl(bootstrapContext, properties, null){
            protected void startResourceAdapter() throws ResourceException {
                startRACalled = true;
            }
        };
    }
    
    ParlayProviderImpl providerImpl;
    
    BootstrapContext bootstrapContext;
    
    static boolean startRACalled = false;

    public void testParlayProviderImpl() {
        ParlayResourceAdaptorProperties properties = new ParlayResourceAdaptorProperties();
        
        try {
            properties.loadDefaults();
        }
        catch (ResourceException e) {
            fail();
        }
        
        ParlayProviderImpl providerImpl = new ParlayProviderImpl(bootstrapContext, properties, null);
    }

    public void testStart() {
        try {
            providerImpl.start();
        }
        catch (ResourceException e) {
            fail("Unexpected exception starting RA.");
        }
        
        assertTrue(startRACalled);
    }

    public void testGetParlayConnection() {
        try {
            providerImpl.getParlayConnection();
            fail("Expected exception RA not started.");
        }
        catch (ResourceException e) {
        }
        
        try {
            providerImpl.start();
            ParlayConnection connection = providerImpl.getParlayConnection();
            
            assertNotNull(connection);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail("Unexpected exception starting RA.");
        }
    }

    public void testStop() {
        
        try {
            providerImpl.start();
            providerImpl.stop();
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail("Unexpected exception stopping RA.");
        }
    }
    
    public void testSetConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactoryImpl(null, null);
        
        providerImpl.setConnectionFactory(connectionFactory);
        
    }
    
    public void testSetResourceAdapter() {
        ResourceAdapterImpl adapterImpl = new ResourceAdapterImpl();
        
        providerImpl.setResourceAdapterImpl(adapterImpl);
    }

}

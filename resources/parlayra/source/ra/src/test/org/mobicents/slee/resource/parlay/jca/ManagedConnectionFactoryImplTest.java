package org.mobicents.slee.resource.parlay.jca;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnection;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.gccs.GccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.MpccsListener;
import org.mobicents.slee.resource.parlay.csapi.jr.ui.UiListener;
import org.mobicents.slee.resource.parlay.fw.FwSessionException;
import org.mobicents.slee.resource.parlay.session.ParlaySession;
import org.mobicents.slee.resource.parlay.session.ServiceSession;

/**
 * 
 * Class Description for ManagedConnectionFactoryImplTest
 */
public class ManagedConnectionFactoryImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        cmControl = MockControl.createControl(ConnectionManager.class);

        connectionManager = (ConnectionManager) cmControl.getMock();

        managedConnectionFactoryImpl = new ManagedConnectionFactoryImpl();

        ParlaySession parlaySession = new ParlaySession() {
            /*
             * (non-Javadoc)
             * 
             * @see org.mobicents.slee.resource.parlay.session.ParlaySession#init()
             */
            public void init() throws FwSessionException {
                //  Auto-generated method stub

            }

            /*
             * (non-Javadoc)
             * 
             * @see org.mobicents.slee.resource.parlay.session.ParlaySession#destroy()
             */
            public void destroy() {
                //  Auto-generated method stub

            }

            /*
             * (non-Javadoc)
             * 
             * @see org.mobicents.slee.resource.parlay.session.ParlaySession#getService(java.lang.String,
             *      java.util.Properties)
             */
            public TpServiceIdentifier getService(String serviceTypeName,
                    Properties serviceProperties) {
                //  Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.mobicents.slee.resource.parlay.session.ParlaySession#getServiceSession(org.mobicents.csapi.jr.slee.TpServiceIdentifier)
             */
            public ServiceSession getServiceSession(
                    TpServiceIdentifier serviceIdentifier) {
                //  Auto-generated method stub
                return null;
            }

            public MpccsListener getMpccsListener() {
                // Auto-generated method stub
                return null;
            }

            public void setMpccsListener(MpccsListener mpccsListener) {
                // Auto-generated method stub
                
            }

            public void setGccsListener(GccsListener gccsListener) {
                // TODO Auto-generated method stub
                
            }

            public void setUiListener(UiListener uiListener) {
                // TODO Auto-generated method stub
                
            }
        };

        resourceAdapterImpl = new ResourceAdapterImpl();
        resourceAdapterImpl.setParlaySession(parlaySession);

        managedConnectionFactoryImpl.setAuthenticationSequence("ONE_WAY");
        managedConnectionFactoryImpl.setDomainID("1234");
        managedConnectionFactoryImpl.setIpInitialIOR("IOR");
        managedConnectionFactoryImpl.setIpInitialLocation("");
        managedConnectionFactoryImpl.setIpInitialURL("");
        managedConnectionFactoryImpl.setNamingServiceIOR("");
        managedConnectionFactoryImpl.setResourceAdapter(resourceAdapterImpl);
    }

    MockControl cmControl;

    ConnectionManager connectionManager;

    ManagedConnectionFactoryImpl managedConnectionFactoryImpl;

    ParlaySession parlaySession;

    ResourceAdapterImpl resourceAdapterImpl;

    public void testHashCode() {
        ManagedConnectionFactoryImpl managedConnectionFactoryImpl2;

        managedConnectionFactoryImpl2 = new ManagedConnectionFactoryImpl();

        managedConnectionFactoryImpl2.setAuthenticationSequence("ONE_WAY");
        managedConnectionFactoryImpl2.setDomainID("12345");
        managedConnectionFactoryImpl2.setIpInitialIOR("IOR");

        assertTrue(managedConnectionFactoryImpl2.hashCode() != managedConnectionFactoryImpl
                .hashCode());
    }

    /*
     * Class under test for Object createConnectionFactory(ConnectionManager)
     */
    public void testCreateConnectionFactoryConnectionManager() {

        ConnectionFactory connectionFactory = null;
        try {
            connectionFactory = (ConnectionFactory) managedConnectionFactoryImpl
                    .createConnectionFactory(connectionManager);
        }
        catch (ResourceException e) {
            fail();
        }

        assertNotNull(connectionFactory);
    }

    /*
     * Class under test for Object createConnectionFactory()
     */
    public void testCreateConnectionFactory() {

        ConnectionFactory connectionFactory = null;
        try {
            connectionFactory = (ConnectionFactory) managedConnectionFactoryImpl
                    .createConnectionFactory();
        }
        catch (ResourceException e) {
            fail();
        }

        assertNotNull(connectionFactory);
    }

    public void testCreateManagedConnection() {
        try {
            ManagedConnection managedConnection = managedConnectionFactoryImpl
                    .createManagedConnection(null, null);

            assertNotNull(managedConnection);
        }
        catch (ResourceException e) {
            fail();
        }
        try {
            managedConnectionFactoryImpl = new ManagedConnectionFactoryImpl();
            ManagedConnection managedConnection = managedConnectionFactoryImpl
                    .createManagedConnection(null, null);

            fail();
        }
        catch (ResourceException e) {
        }

    }

    public void testMatchManagedConnections() {
        try {
            ManagedConnection managedConnection = managedConnectionFactoryImpl
                    .createManagedConnection(null, null);

            assertNotNull(managedConnection);

            Set set = new HashSet();
            set.add(managedConnection);
            ManagedConnection managedConnection2 = managedConnectionFactoryImpl
                    .matchManagedConnections(set, null, null);

            assertEquals(managedConnection, managedConnection2);
            
            assertNull(managedConnectionFactoryImpl.matchManagedConnections(new HashSet(), null, null));
        }
        catch (ResourceException e) {
            fail();
        }
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        ManagedConnectionFactoryImpl managedConnectionFactoryImpl2;

        managedConnectionFactoryImpl2 = new ManagedConnectionFactoryImpl();

        managedConnectionFactoryImpl2.setAuthenticationSequence("ONE_WAY");
        managedConnectionFactoryImpl2.setDomainID("12345");
        managedConnectionFactoryImpl2.setIpInitialIOR("IOR");

        assertTrue(! managedConnectionFactoryImpl2.equals(managedConnectionFactoryImpl));
    }

    public void testGetResourceAdapter() {
        assertEquals(resourceAdapterImpl, managedConnectionFactoryImpl.getResourceAdapter());
    }

    public void testGetAuthenticationSequence() {
        assertEquals("ONE_WAY", managedConnectionFactoryImpl.getAuthenticationSequence());
    }

    public void testGetDomainID() {
        assertEquals("1234", managedConnectionFactoryImpl.getDomainID());
    }

    public void testGetIpInitialIOR() {
        assertEquals("IOR", managedConnectionFactoryImpl.getIpInitialIOR());
    }

    public void testGetIpInitialLocation() {
        assertEquals("", managedConnectionFactoryImpl.getIpInitialLocation());
    }

    public void testGetIpInitialURL() {
        assertEquals("", managedConnectionFactoryImpl.getIpInitialURL());
    }

    public void testGetNamingServiceIOR() {
        assertEquals("", managedConnectionFactoryImpl.getNamingServiceIOR());
    }
    
    public void testGetLogWriter() {
        PrintWriter printWriter = new PrintWriter(System.out);
        try {
            managedConnectionFactoryImpl.setLogWriter(printWriter);
            assertEquals(printWriter, managedConnectionFactoryImpl.getLogWriter());
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
    }

}
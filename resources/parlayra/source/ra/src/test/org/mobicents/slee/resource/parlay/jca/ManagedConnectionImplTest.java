package org.mobicents.slee.resource.parlay.jca;

import java.io.PrintWriter;
import java.util.Properties;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.IpServiceConnection;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;
import org.mobicents.csapi.jr.slee.cc.mpccs.IpMultiPartyCallControlManagerConnection;
import org.mobicents.slee.resource.parlay.csapi.jr.ParlayConnectionProxyAssociation;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManager;
import org.mobicents.slee.resource.parlay.csapi.jr.cc.mpccs.activity.multipartycallcontrolmanager.MultiPartyCallControlManagerStub;
import org.mobicents.slee.resource.parlay.fw.FwSessionException;
import org.mobicents.slee.resource.parlay.session.ParlaySession;

/**
 * 
 * Class Description for ManagedConnectionImplTest
 */
public class ManagedConnectionImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        parlaySessionControl = MockControl.createControl(ParlaySession.class);

        parlaySession = (ParlaySession) parlaySessionControl.getMock();

        associationControl = MockControl.createControl(ParlayConnectionProxyAssociation.class);

        association = (ParlayConnectionProxyAssociation) associationControl.getMock();

        managedConnectionImpl = new ManagedConnectionImpl(parlaySession);
    }

    MockControl parlaySessionControl;

    ParlaySession parlaySession;
    
    MockControl associationControl;
    
    ParlayConnectionProxyAssociation association;

    ManagedConnectionImpl managedConnectionImpl = null;

    public void testGetService() {
        String serviceName = "123";
        Properties properties = new Properties();

        TpServiceIdentifier tpServiceIdentifier = new TpServiceIdentifier(1);

        try {
            parlaySession.getService(serviceName, properties);
        }
        catch (FwSessionException e1) {
            fail();
        }
        catch (javax.slee.resource.ResourceException e) {
            fail();
        }
        parlaySessionControl.setReturnValue(tpServiceIdentifier);
        parlaySessionControl.replay();

        TpServiceIdentifier tpServiceIdentifier2 = null;
        try {
            tpServiceIdentifier2 = managedConnectionImpl.getService(
                    serviceName, properties);
        }
        catch (javax.slee.resource.ResourceException e) {
            fail();
        }
        assertEquals(tpServiceIdentifier, tpServiceIdentifier2);

        parlaySessionControl.verify();
    }

    public void testGetIpServiceConnection() {

        TpServiceIdentifier tpServiceIdentifier = new TpServiceIdentifier(1);

        MultiPartyCallControlManager serviceSession = new MultiPartyCallControlManagerStub();

        parlaySession.getServiceSession(tpServiceIdentifier);
        parlaySessionControl.setReturnValue(serviceSession);
        parlaySessionControl.replay();

        try {
            IpServiceConnection ipServiceConnection = managedConnectionImpl
                    .getIpServiceConnection(tpServiceIdentifier);

            assertTrue(ipServiceConnection instanceof IpMultiPartyCallControlManagerConnection);
        }
        catch (javax.slee.resource.ResourceException e) {
            fail();
        }
    }

    public void testGetConnection() {
        try {
            Connection connection = (Connection) managedConnectionImpl.getConnection(null, null);
            assertNotNull(connection);
        }
        catch (ResourceException e) {
            fail();
        }
    }

    public void testDestroy() {
        
        try {
            association.setParlayConnectionProxy(managedConnectionImpl);
            
            // called on destroy
            association.setParlayConnectionProxy(null);
            associationControl.replay();

            managedConnectionImpl.associateConnection(association);
            managedConnectionImpl.destroy();
            
            associationControl.verify();
        }
        catch (ResourceException e) {
            fail();
        }
    }

    public void testCleanup() {
        
        try {
            association.setParlayConnectionProxy(managedConnectionImpl);
            
            // called on cleanup
            association.setParlayConnectionProxy(null);
            associationControl.replay();

            managedConnectionImpl.associateConnection(association);
            managedConnectionImpl.cleanup();
            
            associationControl.verify();
        }
        catch (ResourceException e) {
            fail();
        }
    }

    public void testAssociateConnection() {
        
        MockControl associationControl2;
        
        ParlayConnectionProxyAssociation association2;

        associationControl2 = MockControl.createControl(ParlayConnectionProxyAssociation.class);

        association2 = (ParlayConnectionProxyAssociation) associationControl.getMock();
        
        try {
            association.setParlayConnectionProxy(managedConnectionImpl);
            
            association.setParlayConnectionProxy(null);
            
            association2.setParlayConnectionProxy(managedConnectionImpl);
            associationControl.replay();
            associationControl2.replay();

            managedConnectionImpl.associateConnection(association);
            managedConnectionImpl.associateConnection(association2);
            
            associationControl.verify();
            associationControl2.verify();
        }
        catch (ResourceException e) {
            fail();
        }
    }

    public void testAssociationClosed() {
        MockControl listenerControl = MockControl.createControl(ConnectionEventListener.class);
        
        ConnectionEventListener listener = (ConnectionEventListener)listenerControl.getMock();
        
        association.setParlayConnectionProxy(managedConnectionImpl);
        listener.connectionClosed(null);
        listenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        associationControl.replay();
        listenerControl.replay();

        try {
            managedConnectionImpl.addConnectionEventListener(listener);
            managedConnectionImpl.associateConnection(association);
            managedConnectionImpl.associationClosed(association);
        }
        catch (ResourceException e) {
            fail();
        }

        associationControl.verify();
        listenerControl.verify();
    }
    

    public void testFireConnectionClosed() {
        MockControl listenerControl = MockControl.createControl(ConnectionEventListener.class);
        
        ConnectionEventListener listener = (ConnectionEventListener)listenerControl.getMock();
        
        ConnectionEvent connectionEvent = new ConnectionEvent(managedConnectionImpl, ConnectionEvent.CONNECTION_CLOSED);
        
        listener.connectionClosed(connectionEvent);
        listenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        listenerControl.replay();

        managedConnectionImpl.addConnectionEventListener(listener);
        managedConnectionImpl.fireConnectionClosed(connectionEvent);
        managedConnectionImpl.removeConnectionEventListener(listener);
        managedConnectionImpl.fireConnectionClosed(connectionEvent);

        listenerControl.verify();
    }

    public void testFireConnectionErrorOccurred() {
        MockControl listenerControl = MockControl.createControl(ConnectionEventListener.class);
        
        ConnectionEventListener listener = (ConnectionEventListener)listenerControl.getMock();
        
        ConnectionEvent connectionEvent = new ConnectionEvent(managedConnectionImpl, ConnectionEvent.CONNECTION_ERROR_OCCURRED);
        
        listener.connectionErrorOccurred(connectionEvent);
        listenerControl.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        listenerControl.replay();

        managedConnectionImpl.addConnectionEventListener(listener);
        managedConnectionImpl.fireConnectionErrorOccurred(connectionEvent);
        managedConnectionImpl.removeConnectionEventListener(listener);
        managedConnectionImpl.fireConnectionErrorOccurred(connectionEvent);

        listenerControl.verify();
    }
    
    public void testGetLogWriter() {
        PrintWriter printWriter = new PrintWriter(System.out);
        try {
            managedConnectionImpl.setLogWriter(printWriter);
            assertEquals(printWriter, managedConnectionImpl.getLogWriter());
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    public void testUnsupportedOperations() {
        try {
            managedConnectionImpl.getXAResource();
            
            fail();
        }
        catch (ResourceException e) {
        }
        try {
            managedConnectionImpl.getLocalTransaction();
            
            fail();
        }
        catch (ResourceException e) {
        }
        try {
            managedConnectionImpl.getMetaData();
            
            fail();
        }
        catch (ResourceException e) {
        }
    }
    
    public void testEquals() {
        assertFalse(managedConnectionImpl.equals(null));
        assertFalse(managedConnectionImpl.equals(""));
        assertTrue(managedConnectionImpl.equals(managedConnectionImpl));
        assertTrue(managedConnectionImpl.equals(new ManagedConnectionImpl(parlaySession)));
        assertTrue(managedConnectionImpl.equals(new ManagedConnectionImpl(null)));
    }
    
    public void testHashCode() {
        assertTrue(managedConnectionImpl.hashCode() == new ManagedConnectionImpl(null).hashCode());
    }
    
    private class ConnectionEventListenerImpl implements ConnectionEventListener {
        
        boolean connectionClosedReceived = false;
        boolean connectionErrorOccurredReceived = false;

        /* (non-Javadoc)
         * @see javax.resource.spi.ConnectionEventListener#connectionClosed(javax.resource.spi.ConnectionEvent)
         */
        public void connectionClosed(ConnectionEvent arg0) {
            //  Auto-generated method stub
            connectionClosedReceived = true;
            
        }

        /* (non-Javadoc)
         * @see javax.resource.spi.ConnectionEventListener#localTransactionStarted(javax.resource.spi.ConnectionEvent)
         */
        public void localTransactionStarted(ConnectionEvent arg0) {
            //  Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see javax.resource.spi.ConnectionEventListener#localTransactionCommitted(javax.resource.spi.ConnectionEvent)
         */
        public void localTransactionCommitted(ConnectionEvent arg0) {
            //  Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see javax.resource.spi.ConnectionEventListener#localTransactionRolledback(javax.resource.spi.ConnectionEvent)
         */
        public void localTransactionRolledback(ConnectionEvent arg0) {
            //  Auto-generated method stub
            
        }
        /* (non-Javadoc)
         * @see javax.resource.spi.ConnectionEventListener#connectionErrorOccurred(javax.resource.spi.ConnectionEvent)
         */
        public void connectionErrorOccurred(ConnectionEvent arg0) {
            //  Auto-generated method stub
            connectionErrorOccurredReceived = true;
            
        }

        /**
         * @return Returns the connectionClosedReceived.
         */
        public boolean isConnectionClosedReceived() {
            return connectionClosedReceived;
        }
        
        /**
         * @return Returns the connectionErrorOccurredReceived.
         */
        public boolean isConnectionErrorOccurredReceived() {
            return connectionErrorOccurredReceived;
        }
        
    }

}
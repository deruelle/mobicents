package org.mobicents.slee.resource.parlay.jca;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;

import junit.framework.TestCase;

import org.easymock.MockControl;

/**
 *
 * Class Description for ConnectionFactoryImplTest
 */
public class ConnectionFactoryImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        mcfControl = MockControl.createControl(ManagedConnectionFactory.class);
        
        connectionFactory = (ManagedConnectionFactory)mcfControl.getMock();
        
        cmControl = MockControl.createControl(ConnectionManager.class);
        
        connectionManager = (ConnectionManager)cmControl.getMock();
        
        connectionFactoryImpl = new ConnectionFactoryImpl(connectionFactory, connectionManager);
        
        connectionControl = MockControl.createControl(Connection.class);
    }
    
    MockControl mcfControl;
    
    MockControl cmControl;
    
    MockControl connectionControl;
    
    ConnectionManager connectionManager;
    
    ManagedConnectionFactory connectionFactory;
    
    ConnectionFactoryImpl connectionFactoryImpl;

    /*
     * Class under test for Connection getConnection()
     */
    public void testGetConnection() throws ResourceException {
        Connection expectedResult = 
            (Connection)connectionControl.getMock();
        
        // record sequence
        connectionManager.allocateConnection(connectionFactory, null);
        cmControl.setReturnValue(expectedResult);
        cmControl.replay();
        
        try {
            Connection connection = connectionFactoryImpl.getConnection();
            
            assertEquals(connection, expectedResult);
        }
        catch (ResourceException e) {
            fail("Unexpected exception.");
            throw e;
        }
        
        cmControl.verify();
    }

    /*
     * Class under test for Connection getConnection(ConnectionSpec)
     */
    public void testGetConnectionConnectionSpec() throws ResourceException {
        Connection expectedResult = 
            (Connection)connectionControl.getMock();
        
        // record sequence
        connectionManager.allocateConnection(connectionFactory, null);
        cmControl.setReturnValue(expectedResult);
        cmControl.replay();
        
        try {
            Connection connection = connectionFactoryImpl.getConnection(null);
            
            assertEquals(connection, expectedResult);
        }
        catch (ResourceException e) {
            fail("Unexpected exception.");
            throw e;
        }
        
        cmControl.verify();
    }

    public void testGetRecordFactory() {
        try {
            connectionFactoryImpl.getRecordFactory();
            fail("Didn't throw expected UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {
        }
        catch (ResourceException e) {
            fail("Didn't throw expected UnsupportedOperationException.");
        }
    }

    public void testGetMetaData() {
        try {
            connectionFactoryImpl.getMetaData();
            fail("Didn't throw expected UnsupportedOperationException.");
        }
        catch (UnsupportedOperationException e) {
        }
        catch (ResourceException e) {
            fail("Didn't throw expected UnsupportedOperationException.");
        }
    }

    public void testGetReference() {
        Reference reference = new Reference("123");
        
        connectionFactoryImpl.setReference(reference);
        
        try {
            assertEquals(reference, connectionFactoryImpl.getReference());
        }
        catch (NamingException e) {
            fail("Unexpected naming exception getting reference");
        }
    }

}

package org.mobicents.slee.resource.parlay.jca;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;

import junit.framework.TestCase;

import org.easymock.MockControl;

/**
 * 
 * Class Description for ConnectionManagerImplTest
 */
public class ConnectionManagerImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        mcfControl = MockControl.createControl(ManagedConnectionFactory.class);

        managedConnectionFactory = (ManagedConnectionFactory) mcfControl
                .getMock();

        mcControl = MockControl.createControl(ManagedConnection.class);

        managedConnection = (ManagedConnection) mcControl.getMock();

        cControl = MockControl.createControl(Connection.class);

        connection = (Connection) cControl.getMock();

        connectionManagerImpl = new ConnectionManagerImpl();
    }

    MockControl mcfControl;

    ManagedConnectionFactory managedConnectionFactory;

    MockControl mcControl;

    ManagedConnection managedConnection;

    MockControl cControl;

    Connection connection;

    ConnectionManagerImpl connectionManagerImpl;

    public void testAllocateConnection1stTime() throws ResourceException {

        managedConnectionFactory.createManagedConnection(null, null);
        mcfControl.setReturnValue(managedConnection);

        managedConnection.addConnectionEventListener(connectionManagerImpl);
        managedConnection.getConnection(null, null);
        mcControl.setReturnValue(connection);

        mcfControl.replay();
        mcControl.replay();

        try {
            Connection result = (Connection) connectionManagerImpl
                    .allocateConnection(managedConnectionFactory, null);

            assertEquals(connection, result);
        }
        catch (ResourceException e) {
            fail();
        }

        mcfControl.verify();
        mcControl.verify();
    }

    public void testConnectionClosed() throws ResourceException {

        managedConnectionFactory.createManagedConnection(null, null);
        mcfControl.setReturnValue(managedConnection);

        managedConnection.addConnectionEventListener(connectionManagerImpl);
        managedConnection.getConnection(null, null);
        mcControl.setReturnValue(connection);

        managedConnection.cleanup();

        mcfControl.replay();
        mcControl.replay();

        try {
            Connection result = (Connection) connectionManagerImpl
                    .allocateConnection(managedConnectionFactory, null);

            assertEquals(connection, result);
        }
        catch (ResourceException e) {
            fail();
        }

        ConnectionEvent event = new ConnectionEvent(managedConnection,
                ConnectionEvent.CONNECTION_CLOSED);
        connectionManagerImpl.connectionClosed(event);

        mcfControl.verify();
        mcControl.verify();
    }

    public void testConnectionErrorOccurred() throws ResourceException {

        managedConnectionFactory.createManagedConnection(null, null);
        mcfControl.setReturnValue(managedConnection);

        managedConnection.addConnectionEventListener(connectionManagerImpl);
        managedConnection.getConnection(null, null);
        mcControl.setReturnValue(connection);

        managedConnection.removeConnectionEventListener(connectionManagerImpl);
        managedConnection.cleanup();
        managedConnection.destroy();

        mcfControl.replay();
        mcControl.replay();

        try {
            Connection result = (Connection) connectionManagerImpl
                    .allocateConnection(managedConnectionFactory, null);

            assertEquals(connection, result);
        }
        catch (ResourceException e) {
            fail();
        }

        ConnectionEvent event = new ConnectionEvent(managedConnection,
                ConnectionEvent.CONNECTION_ERROR_OCCURRED);
        connectionManagerImpl.connectionErrorOccurred(event);

        mcfControl.verify();
        mcControl.verify();
    }

    public void testLocalTransactionCommitted() {
        connectionManagerImpl.localTransactionCommitted(null);
    }

    public void testLocalTransactionRolledback() {
        connectionManagerImpl.localTransactionRolledback(null);
    }

    public void testLocalTransactionStarted() {
        connectionManagerImpl.localTransactionStarted(null);
    }

}
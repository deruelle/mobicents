package org.mobicents.slee.resource.parlay.csapi.jr;

import java.util.Properties;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.mobicents.csapi.jr.slee.IpServiceConnection;
import org.mobicents.csapi.jr.slee.TpServiceIdentifier;

/**
 *
 * Class Description for ParlayConnectionImplTest
 */
public class ParlayConnectionImplTest extends TestCase {
    
    MockControl parlayConnectionProxyControl;
    
    ParlayConnectionProxy parlayConnectionProxy;
    
    ParlayConnectionImpl connectionImpl = null;
    
    protected void setUp() throws Exception {
        createMocks();
        
        connectionImpl = new ParlayConnectionImpl();
        
        connectionImpl.setParlayConnectionProxy(parlayConnectionProxy);
    }
    
    private void createMocks() {
        parlayConnectionProxyControl = MockControl.createControl((ParlayConnectionProxy.class));
        
        parlayConnectionProxy = (ParlayConnectionProxy) parlayConnectionProxyControl.getMock();
    }

    public void testGetService() {
        
        TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(1);
        
        Properties properties = new Properties();
        
        try {
            parlayConnectionProxy.getService("BLAH", properties);
            parlayConnectionProxyControl.setReturnValue(serviceIdentifier);
        }
        catch (javax.slee.resource.ResourceException e) {
            fail("Failed to setup mock sequence.");
        }
        
        parlayConnectionProxyControl.replay();
        
        try {
            TpServiceIdentifier identifier = connectionImpl.getService("BLAH", properties);
            
            assertEquals(serviceIdentifier, identifier);
        }
        catch (javax.slee.resource.ResourceException e1) {
            fail("Failed invoking test operation");
        }
        
        parlayConnectionProxyControl.verify();
    }

    public void testGetIpServiceConnection() {
        
        IpServiceConnection serviceConnection = new IpServiceConnection() {
            public void closeConnection() throws javax.slee.resource.ResourceException {

            }
        };
        
        TpServiceIdentifier serviceIdentifier = new TpServiceIdentifier(1);
        
        try {
            parlayConnectionProxy.getIpServiceConnection(serviceIdentifier);
            parlayConnectionProxyControl.setReturnValue(serviceConnection);
        }
        catch (javax.slee.resource.ResourceException e) {
            fail("Failed to setup mock sequence.");
        }
        
        parlayConnectionProxyControl.replay();
        
        try {
            IpServiceConnection connection = connectionImpl.getIpServiceConnection(serviceIdentifier);
            
            assertEquals(serviceConnection, connection);
        }
        catch (javax.slee.resource.ResourceException e1) {
            fail("Failed invoking test operation");
        }
        
        parlayConnectionProxyControl.verify();
    }
    
    public void testGetServiceProxyNull() {
        connectionImpl.setParlayConnectionProxy(null);
    
        try {
            connectionImpl.getService(null, null);
            fail("Expected exception was not thrown");
        }
        catch (javax.slee.resource.ResourceException e) {
        }
    }
    
    public void testGetIpServiceConnectionProxyNull() {
        connectionImpl.setParlayConnectionProxy(null);
    
        try {
            connectionImpl.getIpServiceConnection(null);
            fail("Expected exception was not thrown");
        }
        catch (javax.slee.resource.ResourceException e) {
        }
    }
    
    public void testCreateInteraction() {
        try {
            connectionImpl.createInteraction();
            fail("Expected exception was not thrown");
        }
        catch (ResourceException e) {
        }
    }

    public void testGetLocalTransaction() {
        try {
            connectionImpl.getLocalTransaction();
            fail("Expected exception was not thrown");
        }
        catch (ResourceException e) {
        }
    }

    public void testGetMetaData() {
        try {
            connectionImpl.getMetaData();
            fail("Expected exception was not thrown");
        }
        catch (ResourceException e) {
        }
    }

    public void testGetResultSetInfo() {
        try {
            connectionImpl.getResultSetInfo();
            fail("Expected exception was not thrown");
        }
        catch (ResourceException e) {
        }
    }

    public void testClose() {
        parlayConnectionProxy.associationClosed(connectionImpl);
        parlayConnectionProxyControl.replay();
        
        try {
            connectionImpl.close();
            
            assertNull(connectionImpl.getParlayConnectionProxy());
            
            // invoke again to ensure behaviour works
            connectionImpl.close();
        }
        catch (ResourceException e) {
            fail("Failed invoking test operation");
        }
        
        parlayConnectionProxyControl.verify();
    }

}

package org.mobicents.slee.resource.parlay.session;

import java.util.Properties;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * Class Description for ServicePropertiesTest
 */
public class ServicePropertiesTest extends TestCase {
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory
        .getLog(ServicePropertiesTest.class);

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        properties = new Properties();
        
        properties.put("Name1", "Value1");
        properties.put("Name2", "Value2a,Value2b");
    }
    
    Properties properties;

    public void testLoad() {
        ServiceProperties serviceProperties = null;
        try {
            serviceProperties = ServiceProperties.load(properties);
            logger.debug(serviceProperties);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
        
        for (int i = 0; i < serviceProperties.getServiceProperties().length; i++) {
            if(serviceProperties.getServiceProperties()[i].ServicePropertyName.equals("Name1")) {
                assertEquals("Value1", serviceProperties.getServiceProperties()[i].ServicePropertyValueList[0]);
            }
            else if (serviceProperties.getServiceProperties()[i].ServicePropertyName.equals("Name2")) {

                assertEquals("Value2a", serviceProperties.getServiceProperties()[i].ServicePropertyValueList[0]);
                assertEquals("Value2b", serviceProperties.getServiceProperties()[i].ServicePropertyValueList[1]);
                
            }
            else {
                fail("Expected property was not in list.");
            }
        }
    }
    
    public void testEquals() {
        ServiceProperties serviceProperties = null;
        
        ServiceProperties serviceProperties2 = null;
        try {
            serviceProperties = ServiceProperties.load(properties);
            
            serviceProperties2 = ServiceProperties.load(properties);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(serviceProperties.equals(serviceProperties2));
        
        properties.put("A", "B");
        try {            
            serviceProperties2 = ServiceProperties.load(properties);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
        
        assertTrue(! serviceProperties.equals(serviceProperties2));
    }
    
    public void testToString() {
        ServiceProperties serviceProperties = null;
        try {
            serviceProperties = ServiceProperties.load(properties);
        }
        catch (ResourceException e) {
            e.printStackTrace();
            fail();
        }
        serviceProperties.toString();
    }

}

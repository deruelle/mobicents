package org.mobicents.slee.resource.parlay;

import java.util.Properties;

import javax.slee.resource.ResourceException;

import junit.framework.TestCase;

/**
 *
 * Class Description for ParlayResourceAdaptorPropertiesTest
 */
public class ParlayResourceAdaptorPropertiesTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        properties = new ParlayResourceAdaptorProperties();
    }
    
    ParlayResourceAdaptorProperties properties = null;

    public void testLoadDefaults() {
        try {
            properties.loadDefaults();
        }
        catch (ResourceException e) {
            fail();
        }
        
    }

    public void testLoad() {
        Properties propertiesToLoad = new Properties();
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialIOR", "a");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.domainID", "b");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.authenticationSequence", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialLocation", "d");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialURL", "e");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.namingServiceIOR", "f");
        
        try {
            properties.load(propertiesToLoad);
        }
        catch (ResourceException e) {
            fail();
        }
        
        assertEquals("a", properties.getIpInitialIOR());
        assertEquals("b", properties.getDomainID());
        assertEquals("c", properties.getAuthenticationSequence());
        assertEquals("d", properties.getIpInitialLocation());
        assertEquals("e", properties.getIpInitialURL());
        assertEquals("f", properties.getNamingServiceIOR());
        

        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialIOR", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.domainID", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.authenticationSequence", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialLocation", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialURL", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.namingServiceIOR", "f");

        try {
            properties.load(propertiesToLoad);
            fail();
        }
        catch (ResourceException e) {
        }
        

        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialIOR", "a");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.domainID", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.authenticationSequence", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialLocation", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialURL", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.namingServiceIOR", "");

        try {
            properties.load(propertiesToLoad);
        }
        catch (ResourceException e) {
            fail();
        }
        

        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialIOR", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.domainID", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.authenticationSequence", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialLocation", "a");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialURL", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.namingServiceIOR", "b");

        try {
            properties.load(propertiesToLoad);
        }
        catch (ResourceException e) {
            fail();
        }
        

        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialIOR", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.domainID", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.authenticationSequence", "c");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialLocation", "");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.ipInitialURL", "a");
        propertiesToLoad.setProperty("org.mobicents.slee.resource.parlay.namingServiceIOR", "");

        try {
            properties.load(propertiesToLoad);
        }
        catch (ResourceException e) {
            fail();
        }
    }

    public void testSetAuthenticationSequence() {
        properties.setAuthenticationSequence("ABC");
        
        assertEquals("ABC", properties.getAuthenticationSequence());
    }

    public void testSetDomainID() {
        properties.setDomainID("ABC");
        
        assertEquals("ABC", properties.getDomainID());
    }

    public void testSetIpInitialIOR() {
        properties.setIpInitialIOR("ABC");
        
        assertEquals("ABC", properties.getIpInitialIOR());
    }
    
    public void testToString() {
        try {
            properties.toString();
            properties.loadDefaults();
            properties.toString();
        }
        catch (ResourceException e) {
            fail();
        }
    }

}

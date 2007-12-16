package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.csapi.fw.TpDomainID;

/**
 *
 * Class Description for FwSessionPropertiesTest
 */
public class FwSessionPropertiesTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        tpDomainID = new TpDomainID();
        tpDomainID.ClientAppID("1234");
        
        fwSessionProperties = new FwSessionProperties();
        fwSessionProperties.setAuthenticationSequence(AuthenticationSequence.TWO_WAY);
        fwSessionProperties.setDomainID(tpDomainID);
        fwSessionProperties.setIpInitialIOR("IOR:0000");
        fwSessionProperties.setIpInitialLocation("gwuser/Ipinital");
        fwSessionProperties.setIpInitialURL("yourmachine:1010");
        fwSessionProperties.setNamingServiceIOR("IOR:1234");
        
    }
    
    FwSessionProperties fwSessionProperties = null;
    
    TpDomainID tpDomainID = null;
    
    final String DEFAULT_AUTHENTICATION_CAPS = "NULL,P_RSA_512,P_RSA_1024";
    final String DEFAULT_AUTHENTICATION_HASH = "P_OSA_MD5";
    final long DEFAULT_AUTHENTICATION_TIMEOUT = 10000;
    final int DEFAULT_INSTANCE_ID = 0;
    final long DEFAULT_SSA_TIMEOUT = 10000;
    final String DEFAULT_DECRYPTION_CIPHER = "RSA/ECB/PKCS1Padding";
    final String DEFAULT_ENCRYPTION_CIPHER = "RSA/ECB/PKCS1Padding";
    final String DEFAULT_PARLAY_FW_VERSION = "P_PARLAY_4";
    final String DEFAULT_RESPONSE_NAME = "mobicents";
    final String DEFAULT_SHARED_SECRET = "SLEE / OSA Gateway";
    final String DEFAULT_SIGNING_ALGORITHM_CAPS = "NULL";
    final String DEFAULT_CERTIFICATE_VAULT = "."+System.getProperty("file.separator");

    public void testGetIpInitialIOR() {
        assertEquals("IOR:0000", fwSessionProperties.getIpInitialIOR());
    }

    public void testGetAuthenticationSequence() {
        assertEquals(AuthenticationSequence.TWO_WAY, fwSessionProperties.getAuthenticationSequence());
    }

    public void testGetDomainID() {
        assertEquals(tpDomainID, fwSessionProperties.getDomainID());
    }
    
    public void testGetAuthenticationCapabilityList() {
        assertEquals(DEFAULT_AUTHENTICATION_CAPS, fwSessionProperties.getAuthenticationCapabilityList());
    }

    public void testGetAuthenticationHash() {
        assertEquals(DEFAULT_AUTHENTICATION_HASH, fwSessionProperties.getAuthenticationHash());
    }
    
    public void testGetAuthenticationSucceededTimeout() {
        assertEquals(DEFAULT_AUTHENTICATION_TIMEOUT, fwSessionProperties.getAuthenticationSucceededTimeout());
    }
    
    public void testGetInstanceID() {
        assertEquals(DEFAULT_INSTANCE_ID, fwSessionProperties.getInstanceID());
    }
    
    public void testGetSsaTimeout() {
        assertEquals(DEFAULT_SSA_TIMEOUT, fwSessionProperties.getSsaTimeout());
    }
    
    public void testGetDecryptionCipherAlgorithm() {
        assertEquals(DEFAULT_DECRYPTION_CIPHER, fwSessionProperties.getDecryptionCipherAlgorithm());
    }

    public void testGetEncryptionCipherAlgorithm() {
        assertEquals(DEFAULT_ENCRYPTION_CIPHER, fwSessionProperties.getEncryptionCipherAlgorithm());
    }
    
    public void testGetFwParlayVersion() {
        assertEquals(DEFAULT_PARLAY_FW_VERSION, fwSessionProperties.getFwParlayVersion());
    }
    
    public void testGetResponseName() {
        assertEquals(DEFAULT_RESPONSE_NAME, fwSessionProperties.getResponseName());
    }

    public void testGetSharedSecret() {
        assertEquals(DEFAULT_SHARED_SECRET, fwSessionProperties.getSharedSecret());
    }
    
    public void testGetSigningAlgorithmCapabilityList() {
        assertEquals(DEFAULT_SIGNING_ALGORITHM_CAPS, fwSessionProperties.getSigningAlgorithmCapabilityList());
    }
    
    public void testGetCertificateVault() {
        assertEquals(DEFAULT_CERTIFICATE_VAULT, fwSessionProperties.getCertificateVault());
    }

    public void testGetIpInitialLocation() {
        assertEquals("gwuser/Ipinital",fwSessionProperties.getIpInitialLocation());  
    }
    
    public void testGetIpInitialURL() {
        assertEquals("yourmachine:1010",fwSessionProperties.getIpInitialURL()); 
    }
    
    public void testGetNamingServiceIOR() {
        assertEquals("IOR:1234",fwSessionProperties.getNamingServiceIOR()); 
    }
    
    public void testSetAuthenticationSequence() {
        fwSessionProperties.setAuthenticationSequence(AuthenticationSequence.TRUSTED);
        assertEquals(fwSessionProperties.getAuthenticationSequence(), AuthenticationSequence.TRUSTED);
    }

    
    public void testSetDomainID() {
        fwSessionProperties.setDomainID(tpDomainID);
        assertEquals(tpDomainID, fwSessionProperties.getDomainID());
    }
    
    public void testSetFwParlayVersion() {
        fwSessionProperties.setFwParlayVersion("P_PARLAY_3");
        assertEquals("P_PARLAY_3", fwSessionProperties.getFwParlayVersion());
    }
    
}

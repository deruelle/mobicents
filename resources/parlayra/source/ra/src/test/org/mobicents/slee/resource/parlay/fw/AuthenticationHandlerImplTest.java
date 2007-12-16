
package org.mobicents.slee.resource.parlay.fw;


import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import junit.framework.TestCase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.csapi.IpInterface;
import org.csapi.P_INVALID_INTERFACE_TYPE;
import org.csapi.P_INVALID_VERSION;
import org.csapi.TpCommonExceptions;
import org.csapi.fw.P_ACCESS_DENIED;
import org.csapi.fw.P_INVALID_ACCESS_TYPE;
import org.csapi.fw.P_INVALID_AUTH_TYPE;
import org.csapi.fw.P_INVALID_DOMAIN_ID;
import org.csapi.fw.P_NO_ACCEPTABLE_AUTHENTICATION_MECHANISM;
import org.csapi.fw.P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY;
import org.csapi.fw.TpAuthDomain;
import org.csapi.fw.TpDomainID;
import org.csapi.fw.fw_access.trust_and_security.IpAPILevelAuthentication;
import org.csapi.fw.fw_access.trust_and_security.IpAccess;
import org.csapi.fw.fw_access.trust_and_security.IpAccessHelper;
import org.csapi.fw.fw_access.trust_and_security.IpInitial;
import org.easymock.MockControl;
import org.mobicents.slee.resource.parlay.fw.access.AuthenticationHandlerImpl;
import org.mobicents.slee.resource.parlay.fw.access.TSMBean;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanException;
import org.mobicents.slee.resource.parlay.fw.access.TSMBeanImpl;
import org.mobicents.slee.resource.parlay.util.crypto.CHAPUtil;
import org.mobicents.slee.resource.parlay.util.crypto.RSAUtil;
import org.mobicents.slee.resource.parlay.util.crypto.RSAUtilException;
import org.mobicents.slee.resource.parlay.util.crypto.RSAUtilTest;

/**
 *
 **/
public class AuthenticationHandlerImplTest extends TestCase {

    AuthenticationHandlerImpl authHandler = null;
    
    MockControl tsmBeanControl = null;
    
    TSMBean mockTSMbean = null;
    
    MockControl initialControl = null;
    
    IpInitial mockInital = null;
    
    MockControl apiLevelAuthenticationControl = null;
    
    IpAPILevelAuthentication mockApiLevelAuthentication = null;
    
    TpAuthDomain authDomain = null;
    
    TpDomainID domainID = null;
    IpInterface ipInterface = null;
    
    byte[] response = {0x61, 0x62};
    
    byte identifier = 0x63;
    
    byte[] responseName = {0x61, 0x62, 0x63};

    CHAPUtil chaputil = new CHAPUtil();
    
    FwSessionProperties props = null;
    
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        Security.addProvider(new BouncyCastleProvider());
        
        props = new FwSessionProperties();
        
        tsmBeanControl = MockControl.createControl(TSMBean.class);
        
        mockTSMbean = (TSMBean)tsmBeanControl.getMock();
        
        initialControl = MockControl.createControl(IpInitial.class);
        
        mockInital = (IpInitial) initialControl.getMock();
        
        apiLevelAuthenticationControl = MockControl.createControl(IpAPILevelAuthentication.class);
        
        mockApiLevelAuthentication = (IpAPILevelAuthentication) apiLevelAuthenticationControl.getMock();
        
        authHandler = new AuthenticationHandlerImpl(mockTSMbean) {
            /* (non-Javadoc)
             * @see org.mobicents.slee.resource.parlay.fw.access.AuthenticationHandlerImpl#generateRandomChallenge()
             */
            protected byte[] generateRandomChallenge() {
                return response;
            }
        };
        
        domainID = new TpDomainID();
        
        domainID.ClientAppID("P_CLIENT_APPLICATION1");
        
        authDomain = new TpAuthDomain();
        authDomain.DomainID = domainID;
        authDomain.AuthInterface = mockApiLevelAuthentication;    

    }


    public void testInitiateAuthentication() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, P_INVALID_AUTH_TYPE, P_INVALID_DOMAIN_ID, TSMBeanException {
        
        mockTSMbean.getInitial();
        tsmBeanControl.setReturnValue(mockInital);
        tsmBeanControl.replay();

        mockInital.initiateAuthentication(null, "P_OSA_AUTHENTICATION");
        initialControl.setReturnValue(authDomain);
        initialControl.replay();
        
        authHandler.initiateAuthentication(null);
        
        tsmBeanControl.verify();
        initialControl.verify();
    }

    public void testInitiateAuthenticationWithVersion() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, P_INVALID_VERSION, P_INVALID_AUTH_TYPE, P_INVALID_DOMAIN_ID, TSMBeanException {
        mockTSMbean.getInitial();
        tsmBeanControl.setReturnValue(mockInital);
        tsmBeanControl.replay();

        mockInital.initiateAuthenticationWithVersion(null, "P_OSA_AUTHENTICATION", "P_PARLAY_4");
        initialControl.setReturnValue(authDomain);
        initialControl.replay();
        
        authHandler.initiateAuthenticationWithVersion(null, "P_PARLAY_4");
        
        tsmBeanControl.verify();
        initialControl.verify();
    }

    public void testSelectEncryptionMethod() throws TpCommonExceptions, P_NO_ACCEPTABLE_ENCRYPTION_CAPABILITY, P_ACCESS_DENIED, TSMBeanException {
        apiLevelAuthenticationControl.reset();
        
        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);

        mockApiLevelAuthentication.selectEncryptionMethod("NULL"); 
        apiLevelAuthenticationControl.setReturnValue("NULL");
        apiLevelAuthenticationControl.replay();
        String result = authHandler.selectEncryptionMethod("NULL");
        assertEquals("NULL", result);
        apiLevelAuthenticationControl.verify();

    }

    public void testSelectAuthenticationMechanism() throws TpCommonExceptions, P_NO_ACCEPTABLE_AUTHENTICATION_MECHANISM, P_ACCESS_DENIED, TSMBeanException {

        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);
        
        mockApiLevelAuthentication.selectAuthenticationMechanism("NULL");
        apiLevelAuthenticationControl.setReturnValue("NULL");
        apiLevelAuthenticationControl.replay();
        
        String result = authHandler.selectAuthenticationMechanism("NULL");
        assertEquals("NULL", result);
        apiLevelAuthenticationControl.verify();

    }

    public void testAuthenticate() throws TpCommonExceptions, P_ACCESS_DENIED, TSMBeanException {

        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);
        mockApiLevelAuthentication.authenticate(response);
        apiLevelAuthenticationControl.setDefaultReturnValue(response);
        apiLevelAuthenticationControl.replay();
        authHandler.authenticate("NULL");
        
        apiLevelAuthenticationControl.verify();
    }
    
    public void testAuthenticateRSA() throws RSAUtilException, TpCommonExceptions, P_ACCESS_DENIED, TSMBeanException {

        RSAUtilTest.writeCertificateToPEMFile("P_CLIENT_APPLICATIONCSWAY512.pem");
        
        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);
        mockApiLevelAuthentication.authenticate(response);
        apiLevelAuthenticationControl.setDefaultReturnValue(response);
        apiLevelAuthenticationControl.replay();
        
        mockTSMbean.getFwProperties();
        tsmBeanControl.setReturnValue(props, 2);
        mockTSMbean.getClientID();
        tsmBeanControl.setReturnValue("P_CLIENT_APPLICATION",2);
        tsmBeanControl.replay();
        
        RSAUtil.loadKeyStore(".", "nokeystore.jks");
        authHandler.authenticate("P_RSA_512");
        
        apiLevelAuthenticationControl.verify();
        tsmBeanControl.verify();

    }
    

    public void testChallenge() throws NoSuchAlgorithmException, TpCommonExceptions, P_ACCESS_DENIED, TSMBeanException {       

        byte[] chapChallenge = chaputil.generateCHAPRequestPacket(response);            
        
        byte[] challenge = chaputil.generateMD5HashChallenge(chapChallenge[1], 
        		props.getSharedSecret(), response);
        
        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);
 
        mockApiLevelAuthentication.challenge(chaputil.generateCHAPRequestPacket(challenge));
        
        apiLevelAuthenticationControl.setMatcher(MockControl.ALWAYS_MATCHER);
        apiLevelAuthenticationControl.setReturnValue(chaputil.generateCHAPResponsePacket(identifier, challenge, responseName));
        
        apiLevelAuthenticationControl.replay();
        
        mockTSMbean.getFwProperties();
        tsmBeanControl.setReturnValue(props);
        tsmBeanControl.replay();
        
        authHandler.challenge("P_OSA_MD5");
        
        apiLevelAuthenticationControl.verify();
        tsmBeanControl.verify();

    }

    public void testAuthenticationSucceeded() throws TpCommonExceptions, P_ACCESS_DENIED, TSMBeanException {
        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);
        mockApiLevelAuthentication.authenticationSucceeded();
        apiLevelAuthenticationControl.replay();
        authHandler.authenticationSucceeded();
        apiLevelAuthenticationControl.verify();

    }

    public void testRequestAccess() throws P_INVALID_INTERFACE_TYPE, TpCommonExceptions, P_INVALID_ACCESS_TYPE, P_ACCESS_DENIED, TSMBeanException {
        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);

        IpAccess accessStub = new IpAccessStub();
        mockApiLevelAuthentication.requestAccess(TSMBeanImpl.ACCESS_TYPE, null);
        apiLevelAuthenticationControl.setReturnValue(accessStub);
        apiLevelAuthenticationControl.replay();
        IpAccess access = authHandler.requestAccess(null);
        assertEquals(IpAccessHelper.narrow(accessStub), access);
        apiLevelAuthenticationControl.verify();

    }

    public void testAbortAuthentication() throws TpCommonExceptions, P_ACCESS_DENIED {
        authHandler.setApiLevelAuthentication(mockApiLevelAuthentication);

        mockApiLevelAuthentication.abortAuthentication();

        apiLevelAuthenticationControl.replay();
        authHandler.abortAuthentication();
        apiLevelAuthenticationControl.verify();
    }
    
    public void tearDown() {
        authHandler.cleanup();

        File file = new File("P_CLIENT_APPLICATIONCSWAY512.pem");
        if(file.exists()) {
            file.delete();
        }
    }
    
    public void testToString() {
        assertNotNull(authHandler.toString());
    }


}

package org.mobicents.slee.resource.parlay.util.crypto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;

import junit.framework.TestCase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *  
 */
public class RSAUtilTest extends TestCase {

    byte[] challenge = { 0x61, 0x62, 0x63 };

    KeyPair keyPair = null;

    RSAUtil rsaUtil = null;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        rsaUtil = new RSAUtil();

        Security.addProvider(new BouncyCastleProvider());

        keyPair = RSAUtil.generateRSAKeyPair(512);

        writeCertificateToPEMFile("CLIENT.pem");
    }
    
    protected void tearDown()
            throws Exception {
        super.tearDown();
        File file = new File("CLIENT.pem");
        if(file.exists()) {
            file.delete();
        }
        
        file = new File("mobicents-parlay-ra.jks");
        if(file.exists()) {
            file.delete();
        }
    }

    public void testGenerateRSAKeyPair() {
        try {
            KeyPair result = rsaUtil.generateRSAKeyPair(512);
            result.equals(RSAUtil.generateRSAKeyPair(512));
        } catch (RSAUtilException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetPublicKey() {

        try {
            RSAUtil.loadKeyStore(".", "keystore.jks");
            RSAUtil.getPublicKey("CLIENT.pem", "CLIENT");
        } catch (RSAUtilException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGetPrivateKey() {
    }

    public void testEncryptMessage() {
        try {
            byte[] result1 = RSAUtil.encryptMessage(challenge,
                    (RSAPublicKey) keyPair.getPublic(), "RSA/ECB/PKCS1Padding");
            assertNotNull(result1);
            byte[] result2 = RSAUtil.encryptMessage(challenge,
                    (RSAPublicKey) keyPair.getPublic(), "RSA/ECB/PKCS1Padding");
            assertEquals(result1.length, result2.length);

        } catch (RSAUtilException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testDecryptMessage() {
        
//        try {
//            RSAUtil.decryptMessage(challenge, keyPair.getPrivate(),
//                    "RSA/ECB/PKCS1Padding");
//        } catch (RSAUtilException e) {
//            e.printStackTrace();
//            fail();
//        }
         
    }

    public void testLoadKeyStore() {
        try {
            RSAUtil.loadKeyStore(".", "file.ks");
        } catch (RSAUtilException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSaveKeyStore() {
        try {
            RSAUtil.saveKeyStore(".");
        } catch (RSAUtilException e) {
            e.printStackTrace();
            fail();
        }
    }

    public static void writeCertificateToPEMFile(String filename)
            throws RSAUtilException {

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("-----BEGIN CERTIFICATE-----\n");
            sb.append("MIIBIzCCARegAwIBAwIBADADBgEAMCIxCzAJBgNVBAYTAlVLMRMwEQYDVQQDEwpB\n");
            sb.append("ZXBvbmEgTHRkMB4XDTA1MTIwODE0NDAyMVoXDTA2MTIwODE0NDAyMVowIjELMAkG\n");
            sb.append("A1UEBhMCVUsxEzARBgNVBAMTCkFlcG9uYSBMdGQwgZ8wDQYJKoZIhvcNAQEBBQAD\n");
            sb.append("gY0AMIGJAoGBAKYyxbQhLQHRv26C3gkUpRpAjX4/K8G5auOh0Wr84OvyIpTA9kgc\n");
            sb.append("CVSdBrPPwWQL/FwieyYo0sFhXFnYDjZRBciMco/fV1BWyoYc61HJaRKrcDWwbuiy\n");
            sb.append("WI0z0w9cMNslKtsJJxAX6suVyaEpqUlHvjT1evm9SvTBG99YvE5P4GHPAgMBAAEw\n");
            sb.append("AwYBAAMBAA==");
            sb.append("\n-----END CERTIFICATE-----\n");

            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(sb.toString());
            out.close();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
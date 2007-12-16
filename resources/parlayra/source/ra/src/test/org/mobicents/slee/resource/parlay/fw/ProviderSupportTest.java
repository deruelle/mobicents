package org.mobicents.slee.resource.parlay.fw;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * 
 * Class Description for ProviderSupportTest
 */
public class ProviderSupportTest extends TestCase {
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory
        .getLog(ProviderSupportTest.class);

    public void testRSA512Supported() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

        Security.addProvider(new BouncyCastleProvider());

        traceSecurityProviders();

        KeyPairGenerator kpg;

        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair keyPair = kpg.generateKeyPair();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());

        byte[] before = new byte[] { 1, 2, 3, 4 };

        byte[] encrypted = cipher.doFinal(before);

        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());

        byte[] after = cipher.doFinal(encrypted);

        for (int i = 0; i < after.length; i++) {
            assertEquals(before[i], after[i]);
        }

    }

    public void testRSA1024Supported() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Security.addProvider(new BouncyCastleProvider());

        traceSecurityProviders();

        KeyPairGenerator kpg;

        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair keyPair = kpg.generateKeyPair();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());

        byte[] before = new byte[] { 1, 2, 3, 4 };

        byte[] encrypted = cipher.doFinal(before);

        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());

        byte[] after = cipher.doFinal(encrypted);

        for (int i = 0; i < after.length; i++) {
            assertEquals(before[i], after[i]);
        }

    }

    public void testMD5WithRSASupported() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpg;

        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair keyPair = kpg.generateKeyPair();
        Signature signature = null;
        signature = Signature.getInstance("MD5withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(new String("Test").getBytes());
        signature.update(new String("Test2").getBytes());
        byte[] result = signature.sign();

    }

    /**
     * Method traceSecurityProviders.
     */
    private void traceSecurityProviders() {
        logger.debug("Installed security providers providers:");
        Provider aprovider[] = Security.getProviders();
        for (int i = 0; i < aprovider.length; i++) {
            Provider provider = aprovider[i];
            logger.debug("Provider " + (i + 1) + ": "
                    + provider.getName() + "  version: "
                    + provider.getVersion());
        }
    }
}
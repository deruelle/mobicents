
package org.mobicents.slee.resource.parlay.util.crypto;

import junit.framework.TestCase;

/**
 *
 **/
public class RSAUtilExceptionTest extends TestCase {

    RSAUtilException exception = null;
    
    Throwable throwable = null;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        throwable = new Throwable("Exception");
    }

    /*
     * Class under test for void RSAUtilException(Throwable)
     */
    public void testRSAUtilExceptionThrowable() {
        exception = new RSAUtilException(throwable);
        assertEquals(throwable,exception.getCause());
    }

    /*
     * Class under test for void RSAUtilException(String)
     */
    public void testRSAUtilExceptionString() {
        exception = new RSAUtilException("Some exception");
        assertEquals("Some exception",exception.getLocalizedMessage());
    }

    /*
     * Class under test for void RSAUtilException(String, Throwable)
     */
    public void testRSAUtilExceptionStringThrowable() {
        exception = new RSAUtilException("Some exception", throwable);
        assertEquals("Some exception",exception.getMessage());
        assertEquals(throwable, exception.getCause());
    }


}

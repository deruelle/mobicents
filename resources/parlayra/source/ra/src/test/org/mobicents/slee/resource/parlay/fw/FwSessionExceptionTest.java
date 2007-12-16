
package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

/**
 *
 **/
public class FwSessionExceptionTest extends TestCase {

    
    FwSessionException fwSessionException;
    
    Exception exception;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        exception = new Exception("");
    }
    
    /*
     * Class under test for void FwSessionException(Exception)
     */
    public void testFwSessionExceptionException() {
        fwSessionException = new FwSessionException(exception);
        assertEquals(exception, fwSessionException.getCause());
    }

    /*
     * Class under test for void FwSessionException(String)
     */
    public void testFwSessionExceptionString() {
        fwSessionException = new FwSessionException("error message");
        assertEquals("error message", fwSessionException.getLocalizedMessage());
    }

    /*
     * Class under test for void FwSessionException(String, Exception)
     */
    public void testFwSessionExceptionStringException() {
        fwSessionException = new FwSessionException("error message", exception);
        assertEquals("error message"+System.getProperty("line.separator")+"Root Cause : ", fwSessionException.getLocalizedMessage());
        assertEquals(exception, fwSessionException.getCause());
    }


}

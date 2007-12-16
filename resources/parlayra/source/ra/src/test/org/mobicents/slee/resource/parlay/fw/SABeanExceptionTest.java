
package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

import org.mobicents.slee.resource.parlay.fw.application.SABeanException;

/**
 *
 **/
public class SABeanExceptionTest extends TestCase {

    SABeanException saBeanException;
    
    Exception exception;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        exception = new Exception("");
    }

    /*
     * Class under test for void SABeanException(String)
     */
    public void testSABeanExceptionString() {
        saBeanException = new SABeanException("error message");
        assertEquals("error message", saBeanException.getLocalizedMessage());
    }

    /*
     * Class under test for void SABeanException(Exception)
     */
    public void testSABeanExceptionException() {
        saBeanException = new SABeanException(exception);
        assertEquals(exception, saBeanException.getCause());
    }

    /*
     * Class under test for void SABeanException(String, Exception)
     */
    public void testSABeanExceptionStringException() {
        saBeanException = new SABeanException("error message", exception);
        assertEquals("error message"+System.getProperty("line.separator")+"Root Cause : ", saBeanException.getLocalizedMessage());
        assertEquals(exception, saBeanException.getCause());
    }

}

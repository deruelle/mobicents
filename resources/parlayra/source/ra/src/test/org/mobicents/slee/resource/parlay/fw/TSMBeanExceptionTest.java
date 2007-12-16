
package org.mobicents.slee.resource.parlay.fw;

import org.mobicents.slee.resource.parlay.fw.access.TSMBeanException;

import junit.framework.TestCase;

/**
 *
 **/
public class TSMBeanExceptionTest extends TestCase {

    
    TSMBeanException tsmBeanException;
    
    Exception exception;
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        exception = new Exception("");
    }
    
    /*
     * Class under test for void TSMBeanException(Exception)
     */
    public void testFwSessionExceptionException() {
        tsmBeanException = new TSMBeanException(exception);
        assertEquals(exception, tsmBeanException.getCause());
    }

    /*
     * Class under test for void TSMBeanException(String)
     */
    public void testFwSessionExceptionString() {
        tsmBeanException = new TSMBeanException("error message");
        assertEquals("error message", tsmBeanException.getLocalizedMessage());
    }

    /*
     * Class under test for void TSMBeanException(String, Exception)
     */
    public void testFwSessionExceptionStringException() {
        tsmBeanException = new TSMBeanException("error message", exception);
        assertEquals("error message"+System.getProperty("line.separator")+"Root Cause : ", tsmBeanException.getLocalizedMessage());
        assertEquals(exception, tsmBeanException.getCause());
    }


}

package org.mobicents.slee.resource.parlay.util.corba;

import junit.framework.TestCase;

/**
 *
 **/
public class CorbaUtilExceptionTest extends TestCase {

    CorbaUtilException exception = null;
    Throwable throwable = null;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        throwable = new Throwable("reason");
    }

    /*
     * Class under test for void CorbaUtilException()
     */
    public void testCorbaUtilException() {
        exception = new CorbaUtilException();
        assertNull(exception.getMessage());
    }

    /*
     * Class under test for void CorbaUtilException(String)
     */
    public void testCorbaUtilExceptionString() {
        exception = new CorbaUtilException("exception");
        assertEquals("exception",exception.getLocalizedMessage());
    }

    /*
     * Class under test for void CorbaUtilException(String, Throwable)
     */
    public void testCorbaUtilExceptionStringThrowable() {
        exception = new CorbaUtilException("exception", throwable);
        assertEquals(throwable, exception.getCause());
        assertEquals("exception",exception.getMessage());
    }

    /*
     * Class under test for void CorbaUtilException(Throwable)
     */
    public void testCorbaUtilExceptionThrowable() {
        exception = new CorbaUtilException(throwable);
        assertEquals(throwable, exception.getCause());
    }

}

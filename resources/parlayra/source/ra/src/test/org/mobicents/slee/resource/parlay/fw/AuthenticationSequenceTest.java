package org.mobicents.slee.resource.parlay.fw;

import junit.framework.TestCase;

/**
 * 
 * Class Description for AuthenticationSequenceTest
 */
public class AuthenticationSequenceTest extends TestCase {
    
    AuthenticationSequence sequence = null;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        sequence = AuthenticationSequence.TWO_WAY;
    }

    public void testGetAuthenticationSequence() {
        assertEquals(AuthenticationSequence.ONE_WAY, AuthenticationSequence
                .getAuthenticationSequence("ONE_WAY"));
        assertEquals(AuthenticationSequence.TRUSTED, AuthenticationSequence
                .getAuthenticationSequence("TRUSTED"));
        assertEquals(AuthenticationSequence.TWO_WAY, AuthenticationSequence
                .getAuthenticationSequence("TWO_WAY"));
    }
    
    public void testGetType() {
        
        assertEquals("TWO_WAY", sequence.getType());
        
    }
    
    public void testHashCode() {
        assertEquals("TWO_WAY".hashCode() ,sequence.hashCode());
        
    }

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.naming;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kulikov
 */
public class NumericRangeTest {

    private final static String token = "[1..100]";
    private NumericRange range;
    
    public NumericRangeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        range = new NumericRange(token);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of hasMore method, of class NumericRange.
     */
    @Test
    public void testHasMore() {
        for (int i = 1; i <= 100; i++) {
            assertEquals(true, range.hasMore());
        }
    }

    /**
     * Test of next method, of class NumericRange.
     */
    @Test
    public void testNext() {
        for (int i = 1; i <= 100; i++) {
            assertEquals(Integer.toString(i), range.next());
        }
    }

}
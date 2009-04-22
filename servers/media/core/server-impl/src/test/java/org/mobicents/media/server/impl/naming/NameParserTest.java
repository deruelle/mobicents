/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.naming;

import java.util.Collection;
import java.util.Iterator;
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
public class NameParserTest {

    public NameParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parse method, of class NameParser.
     */
    @Test
    public void testParse() {
        String name = "/media/aap/[1..100]";
        NameParser parser = new NameParser();
        Collection<NameToken> tokens = parser.parse(name);
        assertEquals(3, tokens.size());
        
        Iterator<NameToken> it = tokens.iterator();
        NameToken t = it.next();
        
        assertEquals(true, t instanceof FixedToken);
        
        t = it.next();
        assertEquals(true, t instanceof FixedToken);
        
        t = it.next();
        assertEquals(true, t instanceof NumericRange);
    }

}
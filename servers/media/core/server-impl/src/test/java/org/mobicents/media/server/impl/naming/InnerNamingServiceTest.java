/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.naming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.server.spi.Endpoint;

/**
 *
 * @author kulikov
 */
public class InnerNamingServiceTest {

    private InnerNamingService namingService;
    private final String name = "/media/aap/[1..10]";
    private final String name2 = "/media/aap/[1..10]/[1..2]";
    
    public InnerNamingServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        namingService = new InnerNamingService();
        namingService.start();
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of getNames method, of class InnerNamingService.
     */
    @Test
    public void testGetNames() {
        NameParser parser = new NameParser();
        Iterator<NameToken> tokens = parser.parse(name).iterator();

        ArrayList<String> prefixes = new ArrayList();        
        prefixes.add("");
        
        Collection<String> names = namingService.getNames(prefixes, tokens.next(), tokens);
        
        Iterator<String> it = names.iterator();
        for (int i = 1; i <= 10; i++ ) {
            assertEquals("/media/aap/" + i, it.next());
        }
    }

    @Test
    public void testGetNames2() {
        NameParser parser = new NameParser();
        Iterator<NameToken> tokens = parser.parse(name2).iterator();

        ArrayList<String> prefixes = new ArrayList();        
        prefixes.add("");
        
        Collection<String> names = namingService.getNames(prefixes, tokens.next(), tokens);
        
        Iterator<String> it = names.iterator();
        for (int i = 1; i <= 2; i++ ) {
            for (int j = 1; j <= 10; j++) {
                assertEquals("/media/aap/" + j +"/" + i, it.next());
            }
        } 
    }
    
}
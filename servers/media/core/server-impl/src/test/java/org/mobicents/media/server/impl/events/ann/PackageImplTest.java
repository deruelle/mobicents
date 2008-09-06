/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.ann;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.impl.EventPackageFactory;
import static org.junit.Assert.*;
import org.mobicents.media.server.spi.events.EventPackage;
import org.mobicents.media.server.spi.events.Options;
import org.mobicents.media.server.spi.events.Signal;
import org.mobicents.media.server.spi.events.announcement.AnnParams;

/**
 *
 * @author Oleg Kulikov
 */
public class PackageImplTest {

    public PackageImplTest() {
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

    @Test
    public void testGetPackage() {
        try {
            EventPackage pkg = EventPackageFactory.load("org.mobicents.media.ann");
            if (!(pkg instanceof PackageImpl)) {
                fail("Announcement package expected");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    /**
     * Test of getSignal method, of class PackageImpl.
     */
    @Test
    public void testGetSignal() {
        try {
            EventPackage pkg = EventPackageFactory.load("org.mobicents.media.ann");
            Options options = new Options();
            options.add(AnnParams.URL, "");
            Signal signal = pkg.getSignal("PLAY", options);
            if (!(signal instanceof AnnSignal)) {
                fail("Announcement signal expected");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    
    /**
     * Test of getDetector method, of class PackageImpl.
     */
    @Test
    public void testGetDetector() {
        
    }

}
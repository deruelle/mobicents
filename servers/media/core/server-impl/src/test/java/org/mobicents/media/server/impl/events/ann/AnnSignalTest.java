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
import static org.junit.Assert.*;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.EventPackageFactory;
import org.mobicents.media.server.spi.events.EventPackage;
import org.mobicents.media.server.spi.events.Options;
import org.mobicents.media.server.spi.events.Signal;
import org.mobicents.media.server.spi.events.announcement.AnnParams;

/**
 *
 * @author Oleg Kulikov
 */
public class AnnSignalTest {

    public AnnSignalTest() {
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
     * Test of getFormats method, of class AnnSignal.
     */
    @Test
    public void testGetFormats() {
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        
        try {
            EventPackage pkg = EventPackageFactory.load("org.mobicents.media.ann");
            Options options = new Options();
            options.add(AnnParams.URL, "");
            Signal signal = pkg.getSignal("PLAY", options);
            
            assertEquals(1, signal.getFormats().length);
            assertEquals(LINEAR, signal.getFormats()[0]);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
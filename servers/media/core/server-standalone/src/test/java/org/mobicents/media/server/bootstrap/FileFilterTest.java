package org.mobicents.media.server.bootstrap;

import java.io.File;
import java.io.FileFilter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * 
 * @author amit bhayani
 *
 */
public class FileFilterTest {   
	
	private FileFilter fileFilter = null;
	
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    	fileFilter = new FileFilterImpl();
    	
    }
    
    @Test
    public void testFilter() throws Exception {
    	File f1 = new File("myTest-beans.xml");
    	assertTrue(fileFilter.accept(f1));
    	
    	File f2 = new File("myTest-beans.xml~");
    	assertFalse(fileFilter.accept(f2));
    	
    	File f3 = new File("mgcp-conf.xml");
    	assertTrue(fileFilter.accept(f3));
    	
    }
    
    @After
    public void tearDown() {
    }

}

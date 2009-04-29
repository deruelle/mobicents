/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.naming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jboss.test.kernel.junit.MicrocontainerTest;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.ResourceUnavailableException;

/**
 * 
 * @author kulikov
 */
public class InnerNamingServiceTestCase extends MicrocontainerTest {

	Logger logger = Logger.getLogger(InnerNamingServiceTestCase.class);

	private InnerNamingService namingService;
	private final String name = "/media/aap/[1..10]";
	private final String name2 = "/media/aap/[1..10]/[1..2]";

	public InnerNamingServiceTestCase(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		namingService = new InnerNamingService();
		namingService.start();
	}

	/**
	 * Test of getNames method, of class InnerNamingService.
	 */
	public void testGetNames() {
		NameParser parser = new NameParser();
		Iterator<NameToken> tokens = parser.parse(name).iterator();

		ArrayList<String> prefixes = new ArrayList();
		prefixes.add("");

		Collection<String> names = namingService.getNames(prefixes, tokens.next(), tokens);

		Iterator<String> it = names.iterator();
		for (int i = 1; i <= 10; i++) {
			assertEquals("/media/aap/" + i, it.next());
		}
	}

	public void testGetNames2() {
		NameParser parser = new NameParser();
		Iterator<NameToken> tokens = parser.parse(name2).iterator();

		ArrayList<String> prefixes = new ArrayList();
		prefixes.add("");

		Collection<String> names = namingService.getNames(prefixes, tokens.next(), tokens);

		Iterator<String> it = names.iterator();
		for (int i = 1; i <= 2; i++) {
			for (int j = 1; j <= 10; j++) {
				assertEquals("/media/aap/" + j + "/" + i, it.next());
			}
		}
	}

	public void testFind() {

		namingService = (InnerNamingService) getBean("MediaServer");
		try {
			Endpoint endPt = namingService.find("/mobicents/media/aap/1", true);
			assertNotNull(endPt);

			try {
				endPt = namingService.find("/mobicents/media/aap/1", false);
				fail("ResourceUnavailableException should have been thrown");
			} catch (ResourceUnavailableException e) {
				logger.debug("Expected Error", e);
			}

		} catch (ResourceUnavailableException e) {
			e.printStackTrace();
			fail("testFind failed");
		}

	}

}
package org.mobicents.media.server.spi;

import org.jboss.test.kernel.junit.MicrocontainerTest;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.EndpointQuery;

public class EndpointQueryTest extends MicrocontainerTest {

	private EndpointQuery endpointQuery = null;

	public EndpointQueryTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		endpointQuery = EndpointQuery.getInstance();
	}

	public void testEndpointQuery() throws Exception {
		assertNotNull(endpointQuery);

		long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			Endpoint endpointPr = endpointQuery
					.findAny("media/trunk/PacketRelay/$");
			assertNotNull(endpointPr);
		}
		System.out.println("\n The look up of 10 Endpoints took "
				+ (System.currentTimeMillis() - time) + "\n");

	}
}

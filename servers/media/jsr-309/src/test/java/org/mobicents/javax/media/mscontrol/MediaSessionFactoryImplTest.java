package org.mobicents.javax.media.mscontrol;

import static org.junit.Assert.assertNotNull;

import javax.media.mscontrol.MediaSession;
import javax.media.mscontrol.MediaSessionFactory;
import javax.media.mscontrol.MscontrolException;
import javax.media.mscontrol.MscontrolFactory;
import javax.media.mscontrol.resource.Parameters;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MediaSessionFactoryImplTest {

	private MediaSessionFactory theMediaSessionFactory = null;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		
		try {
			MscontrolFactory.getInstance().setPathName("org.mobicents");

			theMediaSessionFactory = MscontrolFactory.getInstance().createMediaSessionFactory(null);
		} catch (MscontrolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testMediaSessionFactoryImpl() {

		assertNotNull(theMediaSessionFactory);
	}

	@Test
	public void testCreateParameters() {
		Parameters parameters = theMediaSessionFactory.createParameters();
		assertNotNull(parameters);

	}

	@Test
	public void testcreateMediaSession() throws MscontrolException {
		MediaSession mediaSession = theMediaSessionFactory.createMediaSession();
		assertNotNull(mediaSession);
	}

	@Test
	public void testcreateMediaSessionWithParameters() throws MscontrolException {
		Parameters parameters = theMediaSessionFactory.createParameters();
		MediaSession mediaSession = theMediaSessionFactory.createMediaSession(parameters);
		assertNotNull(mediaSession);
	}

}

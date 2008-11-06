package org.mobicents.mscontrol.impl.events.audio;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.mobicents.mscontrol.impl.AbstractTest;

public class ZMsAudioPackageTest extends AbstractTest {

	private static Logger logger = Logger.getLogger(ZMsAudioPackageTest.class);

	private final static String ANN_WAV_FILE_PASS = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1")
			+ ":8080/mobicents-media-server-test/audio/dtmf.wav";

	private final static String ANN_WAV_FILE_FAIL = "http://" + System.getProperty("jboss.bind.address", "127.0.0.1")
			+ ":8080/mobicents-media-server-test/audio/dtmfblabla.wav";

	public ZMsAudioPackageTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZMsAudioPackageTest.class);
	}
}

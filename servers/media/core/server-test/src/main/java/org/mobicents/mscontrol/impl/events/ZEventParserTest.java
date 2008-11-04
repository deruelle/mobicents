package org.mobicents.mscontrol.impl.events;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mobicents.media.server.spi.events.EventIdentifier;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.dtmf.DtmfEvent;
import org.mobicents.media.server.spi.events.pkg.Announcement;
import org.mobicents.media.server.spi.events.pkg.Audio;
import org.mobicents.media.server.spi.events.pkg.EventID;
import org.mobicents.mscontrol.MsNotifyEvent;
import org.mobicents.mscontrol.events.MsEventIdentifier;
import org.mobicents.mscontrol.events.dtmf.MsDtmfNotifyEvent;
import org.mobicents.mscontrol.events.pkg.DTMF;
import org.mobicents.mscontrol.events.pkg.MsAnnouncement;
import org.mobicents.mscontrol.events.pkg.MsAudio;
import org.mobicents.mscontrol.events.pkg.MsPackageNotSupported;

public class ZEventParserTest extends TestCase {

	private EventParser parser = null;

	private static final String MSPROVIDER = "MSPROVIDER";

	public ZEventParserTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ZEventParserTest.class);
	}

	public void setUp() throws ClassNotFoundException {

		parser = new EventParser();

	}

	public void tearDown() {

	}

	public void testDtmfParsing() {
		NotifyEvent dtmf = new DtmfEvent("1");
		MsNotifyEvent msDtmf = parser.parse(this, dtmf);
		assertNotNull(msDtmf);
		MsEventIdentifier msEventIdentifier = msDtmf.getEventID();
		assertEquals(DTMF.TONE, msEventIdentifier);

		MsDtmfNotifyEvent msDtmfNotifyEvent = (MsDtmfNotifyEvent) msDtmf;
		assertEquals("1", msDtmfNotifyEvent.getSequence());
	}

	// Announcement Parsing Test

	public void testAnnouncementPlayParsing() {
		NotifyEvent annEventPlay = new NotifyEventImpl(Announcement.PLAY);
		MsNotifyEvent msAnnEventPlay = parser.parse(this, annEventPlay);
		assertNotNull(msAnnEventPlay);
		MsEventIdentifier msEventIdentifier = msAnnEventPlay.getEventID();
		assertEquals(MsAnnouncement.PLAY, msEventIdentifier);
	}

	public void testAnnouncementCompletedParsing() {
		NotifyEvent annEventCompleted = new NotifyEventImpl(Announcement.COMPLETED);
		MsNotifyEvent msAnnEventCompleted = parser.parse(this, annEventCompleted);
		assertNotNull(msAnnEventCompleted);
		MsEventIdentifier msEventIdentifier = msAnnEventCompleted.getEventID();
		assertEquals(MsAnnouncement.COMPLETED, msEventIdentifier);
	}

	public void testAnnouncementFailedParsing() {
		NotifyEvent annEventFailed = new NotifyEventImpl(Announcement.FAILED);
		MsNotifyEvent msAnnEventFailed = parser.parse(this, annEventFailed);
		assertNotNull(msAnnEventFailed);
		MsEventIdentifier msEventIdentifier = msAnnEventFailed.getEventID();
		assertEquals(MsAnnouncement.FAILED, msEventIdentifier);
	}

	// Audio Parsing Test

	public void testAudioRecordParsing() {
		NotifyEvent audioEventRecord = new NotifyEventImpl(Audio.RECORD);
		MsNotifyEvent msAudioEventRecord = parser.parse(this, audioEventRecord);
		assertNotNull(msAudioEventRecord);
		MsEventIdentifier msEventIdentifier = msAudioEventRecord.getEventID();
		assertEquals(MsAudio.RECORD, msEventIdentifier);
	}

	public void testAudioFailedParsing() {
		NotifyEvent audioEventFailed = new NotifyEventImpl(Audio.FAILED);
		MsNotifyEvent msAudioEventFailed = parser.parse(this, audioEventFailed);
		assertNotNull(msAudioEventFailed);
		MsEventIdentifier msEventIdentifier = msAudioEventFailed.getEventID();
		assertEquals(MsAudio.FAILED, msEventIdentifier);
	}

	// Package Not Supported Parsing Test
	public void testAnnPkgNotSupportedParsing() {

		EventIdentifier evt = new EventID(Announcement.PACKAGE_NAME, "PACKAGE_NOT_SUPPORTED");
		NotifyEvent notifyEvent = new NotifyEventImpl(evt);

		MsNotifyEvent msNotifyEvent = parser.parse(this, notifyEvent);
		assertNotNull(msNotifyEvent);
		MsEventIdentifier msEventIdentifier = msNotifyEvent.getEventID();
		assertEquals("Expected " + MsPackageNotSupported.ANNOUNCEMENT.getFqn() + " actual is "
				+ msEventIdentifier.getFqn(), MsPackageNotSupported.ANNOUNCEMENT, msEventIdentifier);
	}

	public void testAudioPkgNotSupportedParsing() {

		EventIdentifier evt = new EventID(Audio.PACKAGE_NAME, "PACKAGE_NOT_SUPPORTED");
		NotifyEvent notifyEvent = new NotifyEventImpl(evt);

		MsNotifyEvent msNotifyEvent = parser.parse(this, notifyEvent);
		assertNotNull(msNotifyEvent);
		MsEventIdentifier msEventIdentifier = msNotifyEvent.getEventID();
		assertEquals("Expected " + MsPackageNotSupported.AUDIO.getFqn() + " actual is " + msEventIdentifier.getFqn(),
				MsPackageNotSupported.AUDIO, msEventIdentifier);
	}

	public void testDtmfPkgNotSupportedParsing() {

		EventIdentifier evt = new EventID(DTMF.PACKAGE_NAME, "PACKAGE_NOT_SUPPORTED");
		NotifyEvent notifyEvent = new NotifyEventImpl(evt);

		MsNotifyEvent msNotifyEvent = parser.parse(this, notifyEvent);
		assertNotNull(msNotifyEvent);
		MsEventIdentifier msEventIdentifier = msNotifyEvent.getEventID();
		assertEquals("Expected " + MsPackageNotSupported.DTMF.getFqn() + " actual is " + msEventIdentifier.getFqn(),
				MsPackageNotSupported.DTMF, msEventIdentifier);
	}

	private class NotifyEventImpl implements NotifyEvent {

		private EventIdentifier id = null;

		NotifyEventImpl(EventIdentifier id) {
			this.id = id;
		}

		public EventIdentifier getEventID() {
			return id;
		}
	}

}

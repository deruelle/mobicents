package org.mobicents.media.server.impl.events.dtmf;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.EndpointImpl;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.clock.TimerImpl;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.Timer;

/**
 * 
 * @author amit bhayani
 * 
 */
public class Rfc2833GeneratorTest {

	private Timer timer = null;
	private Endpoint endpoint = null;
	private Rfc2833Generator generator = null;
	private Semaphore semaphore;

	private volatile boolean isFormatCorrect = true;
	private volatile boolean isSizeCorrect = false;
	private volatile boolean isDurationCorrect = false;
	private volatile boolean isSeqCorrect = false;
	private volatile boolean isCorrectTimestamp = false;
	private volatile boolean isEndEventReceived = false;
	private volatile boolean isCorrectDigit = false;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		semaphore = new Semaphore(0);

		timer = new TimerImpl();

		endpoint = new EndpointImpl();
		endpoint.setTimer(timer);

		generator = new Rfc2833Generator("Rfc2833DetectorTest");

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGenerator() throws Exception {
		generator.connect(new TestSink("TestSink"));
		generator.setDuraion(100); // 100 ms
		generator.setVolume(10);
		generator.setDigit("9");
		generator.setEndpoint(endpoint);

		generator.start();

		semaphore.tryAcquire(5, TimeUnit.MILLISECONDS);

		assertEquals(true, isFormatCorrect);
		assertEquals(true, isSizeCorrect);
		assertEquals(true, isDurationCorrect);
		assertEquals(true, isSeqCorrect);
		assertEquals(true, isCorrectTimestamp);
		assertEquals(true, isEndEventReceived);

	}

	private class TestSink extends AbstractSink {

		private long lastDuration = 0;
		private long lastSeqNo = 0;
		private long timeStamp = 0;

		private TestSink(String name) {
			super(name);
		}

		public Format[] getFormats() {
			return new Format[0];
		}

		public boolean isAcceptable(Format format) {
			return true;
		}

		public void receive(Buffer buffer) {
			
			isFormatCorrect &= buffer.getFormat().matches(Rfc2833Detector.DTMF);
			isSizeCorrect = ((buffer.getLength() - buffer.getOffset()) == 4);

			byte[] data = (byte[]) buffer.getData();

			int high = data[2] & 0xff;
			int low = data[3] & 0xff;

			int theDuration = (int) ((high << 8) | low);

			if (lastDuration > 0) {
				isDurationCorrect = theDuration - lastDuration == 160;
			}
			lastDuration = theDuration;

			if (lastSeqNo > 0) {
				isSeqCorrect = (buffer.getSequenceNumber() - lastSeqNo) == 1;

			}
			lastSeqNo = buffer.getSequenceNumber();
			
			if(timeStamp > 0){
				isCorrectTimestamp = (buffer.getTimeStamp() == timeStamp);
			}
			timeStamp = buffer.getTimeStamp();
			
			isEndEventReceived = ((data[1] & 0x80) != 0);
			
			isCorrectDigit = ("9".equals(Rfc2833Detector.TONE[data[0]]));
			
			
		}

	}
}

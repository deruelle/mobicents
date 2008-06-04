package org.mobicents.media.server.impl.dtmf.test;


import org.mobicents.media.server.impl.dtmf.InbandGenerator;
import org.mobicents.media.server.spi.events.NotifyEvent;

public class InbandDTMFGeneratorDetectorTest extends
		InbandDTMFGeneratorSequentialTest {

	protected boolean timeCaptureTest = false;

	public InbandDTMFGeneratorDetectorTest() {
		super();
		// 70ms
		super.TEST_DURATION = 70;
	}

	protected void setUp(int row, int column) throws Exception {

		String toneName = InbandGenerator.getToneName(row, column);
		if (toneName != null) {
			gen = new InbandGenerator(toneName, TEST_DURATION);

//			analyzer = new InbandDetector();

		} else {
			throw new RuntimeException("Failed to obtain tone name for [" + row
					+ "][" + column + "]");
		}

	}

	public void testChangingCaptureTime() throws Exception {
		// Here we will test until failure to see how far we can go
		try {
			timeCaptureTest = true;
			super.TEST_DURATION = 100;

			for (; super.TEST_DURATION > 50; super.TEST_DURATION--) {
				//testDigit6();
			}
		} finally {
			timeCaptureTest = false;
		}

	}

	@Override
	public void update(NotifyEvent event) {

		//super.notified = true;
		String toneName = InbandGenerator.getToneName(super.currentRow,
				super.currentColumn);
		if (event.getMessage().compareToIgnoreCase(toneName) != 0
				&& !timeCaptureTest) {
			doFail("Tone name[" + toneName
					+ "] didnt match detected tone name[" + event.getMessage()
					+ "].");
		} else if (timeCaptureTest) {
			logger.info("Failed to capture properly tone[" + toneName
					+ "] - captured value indicates[" + event.getMessage()
					+ "], this happened for capture time["
					+ super.TEST_DURATION
					+ "]. This is only informational procedure.");
		} else {

		}

	}

}

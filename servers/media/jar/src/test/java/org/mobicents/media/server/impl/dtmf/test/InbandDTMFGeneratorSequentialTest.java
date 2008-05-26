package org.mobicents.media.server.impl.dtmf.test;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.server.impl.BaseResource;
import org.mobicents.media.server.impl.NewSuperXCase;
import org.mobicents.media.server.impl.dtmf.InbandGenerator;

import org.mobicents.media.server.impl.fft.SpectralAnalyser;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.events.NotifyEvent;
import org.mobicents.media.server.spi.events.test.SpectrumEvent;

public class InbandDTMFGeneratorSequentialTest extends NewSuperXCase implements NotificationListener{

	protected final Logger logger = Logger
			.getLogger(getClass().getCanonicalName());

	
	
	
	public InbandDTMFGeneratorSequentialTest() {
		super();
		TEST_DURATION = 2000;
	}

	protected static int TEST_DURATION = 2000;

	protected BaseResource analyzer = null;
	protected InbandGenerator gen = null;
	protected int currentRow=-1;
	protected int currentColumn=-1;
	protected boolean notified=false;
	protected void setUp(int row, int column) throws Exception {

		String toneName = InbandGenerator.getToneName(row, column);
		if (toneName != null) {
			gen = new InbandGenerator(toneName, TEST_DURATION);

			analyzer = new SpectralAnalyser();

		} else {
			throw new RuntimeException("Failed to obtain tone name for [" + row
					+ "][" + column + "]");
		}

	}
	
	
	public void testDigit1() throws Exception
	{
		
		currentColumn=0;
		currentRow=0;
		runTest(currentRow,currentColumn);
	}

	public void testDigit2() throws Exception
	{
		
		currentColumn=1;
		currentRow=0;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit3() throws Exception
	{
		
		currentColumn=2;
		currentRow=0;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit4() throws Exception
	{
		
		currentColumn=0;
		currentRow=1;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit5() throws Exception
	{
		
		currentColumn=1;
		currentRow=1;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit6() throws Exception
	{
		
		currentColumn=2;
		currentRow=1;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit7() throws Exception
	{
		
		currentColumn=0;
		currentRow=2;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit8() throws Exception
	{
		
		currentColumn=1;
		currentRow=2;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit9() throws Exception
	{
		
		currentColumn=2;
		currentRow=2;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigitStar() throws Exception
	{
		
		currentColumn=0;
		currentRow=3;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigit0() throws Exception
	{
		
		currentColumn=1;
		currentRow=3;
		runTest(currentRow,currentColumn);
	}
	
	public void testDigitHash() throws Exception
	{
		
		currentColumn=2;
		currentRow=3;
		runTest(currentRow,currentColumn);
	}
	
	public void testLetterA() throws Exception
	{
		
		currentColumn=3;
		currentRow=0;
		runTest(currentRow,currentColumn);
	}
	
	public void testLetterB() throws Exception
	{
		
		currentColumn=3;
		currentRow=1;
		runTest(currentRow,currentColumn);
	}
	public void testLetterC() throws Exception
	{
		
		currentColumn=3;
		currentRow=2;
		runTest(currentRow,currentColumn);
	}
	public void testLetterD() throws Exception
	{
		
		currentColumn=3;
		currentRow=3;
		runTest(currentRow,currentColumn);
	}
	
	protected void runTest(int currentRow,int currentColumn) throws Exception
	{
		logger.info("Starting tone test - "+InbandGenerator.getToneName(currentRow, currentColumn));
		setUp(currentRow, currentColumn);
		analyzer.addListener(this);
		analyzer.start();
		gen.setTransferHandler((BufferTransferHandler) analyzer);
		if(!doTest(TEST_DURATION/1000+2))
			fail(getReason());
		
		//if(notified)
		//{
			notified=false;
			logger.info("Test completed for tone - "+InbandGenerator.getToneName(currentColumn, currentColumn));
		//}else
		//{
		//	fail("No Spectra analysis performed for tone - "+InbandGenerator.getToneName(currentColumn, currentColumn));
		//}
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		analyzer.stop();
		gen.setTransferHandler(null);
		currentRow=currentColumn=-1;
	}





	public void update(NotifyEvent event) {
		notified=true;
		
		double spectra[] = ((SpectrumEvent) event).getSpectra();
		// Here we have spectra, lets narrow frequencies error from max to
		// minimal value and see when test "fail"
		FrequenciesHolder _row, _column;
		int ERROR = -1;

		_row = rowFrequencies[currentRow];
		_column = columnFrequencies[currentColumn];

		ERROR = Math.min((_column.high - _column.low) / 2,
				(_row.high - _row.low) / 2);
		int MAX_ERROR = ERROR;
		logger.info("Setting frequencies ["+((_column.high + _column.low) / 2)+"]["+((_row.high + _row.low) / 2)+"]");
		int[] FREQUENCIES = new int[] { (_column.high + _column.low) / 2,
				(_row.high + _row.low) / 2 };
		//t.f
		
		while (ERROR > 2) {
			boolean result = checkFreq(spectra, FREQUENCIES, ERROR);
			if (!result && ERROR == MAX_ERROR) {
				
				//fail("Failed to pass test for tone[" + InbandGenerator.getToneName(currentRow, currentColumn)
				//		+ "] - row[" + currentRow + "] col[" + currentColumn
				//		+ "] for MAX_ERROR[" + MAX_ERROR
				//		+ "] on first run[" + getReason() + "] PASSED F:"+Arrays.toString(FREQUENCIES));
				doFail("Failed to pass test for tone[" + InbandGenerator.getToneName(currentRow, currentColumn)
						+ "] - row[" + currentRow + "] col[" + currentColumn
						+ "] for MAX_ERROR[" + MAX_ERROR
						+ "] on first run[" + getReason() + "] PASSED F:"+Arrays.toString(FREQUENCIES));
			} else if (!result) {
				logger
						.info("This message indicates that inband generator has deviated from avg frequency, however it still works properly. This message is for info purposes only!!!\n" +
								"tone[" + InbandGenerator.getToneName(currentRow, currentColumn)
						+ "] - row[" + currentRow + "] col[" + currentColumn
						+ "] for MAX_ERROR[" + MAX_ERROR
						+ "] on first run[" + getReason() + "]");
				return;
			}else
			{
				ERROR-=2;
			}

		}
		
		
		
		//performFrequenciesCheks(MAX_ERROR,ERROR,FREQUENCIES,spectra);

	
		
		
	}
	
	
	
	
	

	static class FrequenciesHolder {

		int low = -1;
		int high = -1;

		public FrequenciesHolder(int low, int high) {
			super();
			this.low = low;
			this.high = high;
		}
	}

	// Yeah we do generate exact, but we are allowed to generate in certain
	// range.
	public static final FrequenciesHolder[] columnFrequencies = new FrequenciesHolder[4];
	public static final FrequenciesHolder[] rowFrequencies = new FrequenciesHolder[4];

	static {
		columnFrequencies[0] = new FrequenciesHolder(1189, 1229);
		columnFrequencies[1] = new FrequenciesHolder(1314, 1358);
		columnFrequencies[2] = new FrequenciesHolder(1453, 1501);
		columnFrequencies[3] = new FrequenciesHolder(1607, 1659);

		rowFrequencies[0] = new FrequenciesHolder(685, 709);
		rowFrequencies[1] = new FrequenciesHolder(756, 784);
		rowFrequencies[2] = new FrequenciesHolder(837, 867);
		rowFrequencies[3] = new FrequenciesHolder(925, 957);
	}

	

}

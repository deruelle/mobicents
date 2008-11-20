package org.mobicents.media.server.load.test.gui;

public class CSVHeaders {

	public static final String CODEC = "Codec";
	public static final String START_TIME = "StartTime";
	public static final String CURRENT_TIME = "CurrentTime";
	public static final String ELPASED_TIME_P = "ElpasedTime(P)";
	public static final String ELPASED_TIME_C = "ElpasedTime(C)";
	public static final String ANNOUNCEMENT_RATE = "AnnouncementRate";
	public static final String ANNOUNCEMENT_COMPLETED = "AnnouncementCompleted(C)";
	public static final String ANNOUNCEMENT_SUCCESSFULL = "AnnouncementSuccessfull(C)";
	public static final String ANNOUNCEMENT_FAILED = "AnnouncementFailed(C)";

	public static final String CSV_FILE = "MMSLoadTestCSV.csv";

	public static final String CSV_SEPARATOR = ",";

	public static String[] getHeaders() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(CODEC);
		buffer.append(CSV_SEPARATOR);
		
		buffer.append(START_TIME);
		buffer.append(CSV_SEPARATOR);
		
		buffer.append(CURRENT_TIME);
		buffer.append(CSV_SEPARATOR);

		buffer.append(ELPASED_TIME_P);
		buffer.append(CSV_SEPARATOR);

		buffer.append(ELPASED_TIME_C);
		buffer.append(CSV_SEPARATOR);

		buffer.append(ANNOUNCEMENT_RATE);
		buffer.append(CSV_SEPARATOR);
		
		buffer.append(ANNOUNCEMENT_COMPLETED);
		buffer.append(CSV_SEPARATOR);
		
		buffer.append(ANNOUNCEMENT_SUCCESSFULL);
		buffer.append(CSV_SEPARATOR);
		
		buffer.append(ANNOUNCEMENT_FAILED);

		String[] headers = (buffer.toString()).split(CSV_SEPARATOR);

		return headers;
	}

}

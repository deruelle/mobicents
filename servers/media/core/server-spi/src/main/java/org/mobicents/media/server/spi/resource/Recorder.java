package org.mobicents.media.server.spi.resource;

import org.mobicents.media.MediaSink;

/**
 * 
 * @author amit bhayani
 * 
 */
public interface Recorder extends MediaSink {
	
	public static final String EMPTY_STRING = "";
	
	//Default record time set is 60 seconds
	public static final int DEFAULT_RECORD_TIME = 60;

	/**
	 * Set the Recording time in seconds
	 * 
	 * @param recordTime
	 */
	public void setRecordTime(int recordTime);

	/**
	 * Start the Recorder passing the path of file. For example
	 * myapp/recordedFile.wav
	 * 
	 * @param file
	 */
	public void start(String file);

	
	public void stop();
	/**
	 * Set the Record path. This will be the parent path and file path passed in
	 * start(String file) will be appended to this base record path. For example
	 * if recordDir = "/home/user/recordedfiles" (for Win OS c:/recordedfiles),
	 * then calling start("myapp/recordedFile.wav") will create recorded file
	 * /home/user/recordedfiles/myapp/recordedFile.wav (for win OS
	 * c:/recordedfiles/myapp/recordedFile.wav)
	 * 
	 * @param recordDir
	 */
	public void setRecordDir(String recordDir);

}

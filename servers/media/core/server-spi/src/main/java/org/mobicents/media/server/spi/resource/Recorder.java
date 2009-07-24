package org.mobicents.media.server.spi.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.mobicents.media.MediaSink;

/**
 * 
 * @author amit bhayani
 * 
 */
public interface Recorder extends MediaSink {
	
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

        /**
         * Assign file for recording.
         * 
         * @param uri the URI which points to a file.
         * @throws java.io.IOException
         * @throws java.io.FileNotFoundException
         */
        public void setRecordFile(String uri) throws IOException, FileNotFoundException;
}

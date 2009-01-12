/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.ann;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.Format;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.format.AudioFormat;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;
import org.mobicents.media.server.impl.events.announcement.AudioPlayer;
import org.mobicents.media.server.impl.rtp.RtpSocket;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.ResourceUnavailableException;
import org.mobicents.media.server.spi.events.pkg.Announcement;

/**
 *
 * @author Oleg Kulikov
 */
public class AudioPlayerTest {
	
	
    private final static AudioFormat PCMA = new AudioFormat(AudioFormat.ALAW, 8000, 8, 1);
    private final static AudioFormat PCMU = new AudioFormat(AudioFormat.ULAW, 8000, 8, 1);
    private final static AudioFormat SPEEX = new AudioFormat(AudioFormat.SPEEX, 8000, 8, 1);
    private final static AudioFormat LINEAR_AUDIO = new AudioFormat(
            AudioFormat.LINEAR, 8000, 16, 1,
            AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
    private final static AudioFormat GSM = new AudioFormat(AudioFormat.GSM, 8000, 8, 1);

    public AudioPlayerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFormats method, of class AudioPlayer.
     */
    @Test
    public void testGetFormats() {
    	TestEndpoint enp = new TestEndpoint("test");
        AudioPlayer p = new AudioPlayer(enp);
        AudioFormat LINEAR = new AudioFormat(AudioFormat.LINEAR,8000, 16, 1,
                AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED);
        assertEquals(5, p.getFormats().length);
        
        Format[] formats = p.getFormats();
        
        assertEquals(true, contains(formats, PCMA));
        assertEquals(true, contains(formats, PCMU));
        assertEquals(true, contains(formats, SPEEX));        
        assertEquals(true, contains(formats, LINEAR));       
        assertEquals(true, contains(formats, GSM));
        
       
    }
    
    private boolean contains(Format[] fmts, Format fmt) {
        for (int i = 0; i < fmts.length; i++) {
            if (fmts[i].matches(fmt)) {
                return true;
            }
        }
        return false;
    }
    
	public class TestEndpoint extends BaseEndpoint {

		public TestEndpoint(String localName) {
			super(localName);
		}

		@Override
		public void allocateMediaSinks(Connection connection) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void allocateMediaSources(Connection connection, Format[] formats) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public RtpSocket allocateRtpSocket(Connection connection) throws ResourceUnavailableException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void deallocateRtpSocket(RtpSocket rtpSocket, Connection connection) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Format[] getFormats() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected MediaSink getMediaSink(MediaResource id, Connection connection) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected MediaSource getMediaSource(MediaResource id, Connection connection) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MediaSink getPrimarySink(Connection connection) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MediaSource getPrimarySource(Connection connection) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void releaseMediaSinks(Connection connection) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void releaseMediaSources(Connection connection) {
			// TODO Auto-generated method stub
			
		}

		public String[] getSupportedPackages() {
			String[] supportedpackages = new String[] { Announcement.PACKAGE_NAME };
			return supportedpackages;
		}


	}

}
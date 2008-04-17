package org.mobicents.media.server.impl.fft;

import java.io.IOException;
import java.util.Properties;


import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.BaseResource;
import org.mobicents.media.server.impl.ann.LocalProxy;
import org.mobicents.media.server.impl.jmf.dsp.*;
import org.mobicents.media.server.impl.test.audio.SineGenerator;
import org.mobicents.media.server.impl.test.audio.SineStream;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.MediaResource;
import org.mobicents.media.server.spi.MediaSource;
import org.mobicents.media.server.spi.NotificationListener;
import org.mobicents.media.server.spi.ResourceStateListener;

public class DFTSineSource extends BaseResource implements MediaSource {

	private static Logger logger = Logger.getLogger(DFTSineSource.class);

	private DFTEndpointImpl enpoint = null;
	private Connection connection = null;

	//private int[] sineFrequencies = new int[] { 10, 100, 1000, 15,
	//		150, 150, 1500 };
	
	private int[] sineFrequencies = new int[] { 100 };
	
	private int sineDuruation = 10000;
	private PushBufferStream[] streams = new SineGenerator(sineDuruation,
			sineFrequencies, true).getStreams();

	
	//private PushBufferStream sineSource = streams[2];
	private PushBufferStream sineSource = streams[0];
	
	public DFTSineSource(DFTEndpointImpl enpoint, Connection connection,
			PushBufferStream sineSource) {
		super();
		this.enpoint = enpoint;
		this.connection = connection;
		// this.sineSource = sineSource;

	}

	public PushBufferStream prepare() {

		return sineSource;

	}

	public void addListener(NotificationListener listener) {
		// TODO Auto-generated method stub

	}

	public void configure(Properties config) {

	}

	public void release() {

	}

	public void removeListener(NotificationListener listener) {

	}

	public void start() {

		((SineStream) sineSource).start();

	}

	public void stop() {
		((SineStream) sineSource).stop();

	}

}

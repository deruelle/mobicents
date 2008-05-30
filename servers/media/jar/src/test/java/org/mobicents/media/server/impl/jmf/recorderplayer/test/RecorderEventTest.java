package org.mobicents.media.server.impl.jmf.recorderplayer.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.protocol.BufferTransferHandler;
import org.mobicents.media.protocol.ContentDescriptor;
import org.mobicents.media.protocol.PushBufferStream;
import org.mobicents.media.server.impl.SuperXCase;
import org.mobicents.media.server.impl.common.events.RecorderEventType;
import org.mobicents.media.server.impl.jmf.recorder.Recorder;
import org.mobicents.media.server.impl.jmf.recorder.RecorderEvent;
import org.mobicents.media.server.impl.jmf.recorder.RecorderListener;
import org.mobicents.media.server.impl.jmf.recorderplayer.test.EventSuperCase.EventTypeListener;
import org.mobicents.media.server.impl.test.audio.SineGenerator;
import org.mobicents.media.server.impl.test.audio.SineStream;

public class RecorderEventTest extends EventSuperCase{

	
	
	protected int sineDuration = 5000;
	protected int[] sineF = new int[] { 155 };
	protected SineStream ss = null;
	protected Recorder r=null;
	//protected RecorderEventTestNotificationListener currentListener=null;
	
	protected void setUp() throws Exception {

		super.setUp();
		ss = (SineStream) new SineGenerator(sineDuration, sineF, true)
				.getStreams()[0];
		setUpFiles("wav");
		r=new Recorder(AudioFileFormat.Type.WAVE,this.sineDuration);
		
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		
		super.tearDown();
		destroyFiles();
		r.stop();
		
	}


	public void testNormalSequence()
	{
		RecorderEventType[] sequence= new RecorderEventType[]{RecorderEventType.STARTED,RecorderEventType.STOP_BY_REQUEST};
		currentListener.setExpectedQueue(sequence);
		r.addListener((RecorderListener)super.currentListener);
		ss.start();
		r.start(recordFile.toString(), ss);
		if(!doTest(sineDuration/1000+1))
		{
			fail(getReason());
		}
	}


		
	public void testErrorSequence_ErrorOnPassedStream_1() throws Exception
	{
		RecorderEventType[] sequence= new RecorderEventType[]{RecorderEventType.FACILITY_ERROR};
		//RecorderEventTestNotificationListener retnl=new RecorderEventTestNotificationListener(sequence);
		super.currentListener.setExpectedQueue(sequence);
		r.addListener((RecorderListener)currentListener);
		
		setUpFiles("wav",true);
		
		r.start(recordFile.toString(), null);
		if(!doTest(1))
		{
			fail(getReason());
		}
	}

	public void testErrorSequence_ErrorOnPassedStream_RuntimeExceptionThrower_1() throws Exception
	{
		RecorderEventType[] sequence= new RecorderEventType[]{RecorderEventType.STARTED,RecorderEventType.FACILITY_ERROR};
		//RecorderEventTestNotificationListener retnl=new RecorderEventTestNotificationListener(sequence);
		//r.addListener(retnl);
		super.currentListener.setExpectedQueue(sequence);
		r.addListener((RecorderListener)currentListener);

		PushBufferStream thrower=new PushBufferStream()
		{

			public Format getFormat() {
				// TODO Auto-generated method stub
				return null;
			}

			public void read(Buffer buffer) throws IOException {
				// TODO Auto-generated method stub
				
			}

			public void setTransferHandler(BufferTransferHandler transferHandler) {
				throw new RuntimeException("Throw from setTransferHandler.");
				
			}

			public boolean endOfStream() {
				
				return false;
			}

			public ContentDescriptor getContentDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}

			public long getContentLength() {
				// TODO Auto-generated method stub
				return 0;
			}

			public Object getControl(String controlType) {
				// TODO Auto-generated method stub
				return null;
			}

			public Object[] getControls() {
				// TODO Auto-generated method stub
				return null;
			}};
			setUpFiles("wav",true);
		r.start(recordFile.toString(), thrower);
		if(!doTest(1))
		{
			fail(getReason());
		}
	}


	public void testErrorSequence_ErrorOnPassedStream_RuntimeExceptionThrower_2() throws Exception
	{
		RecorderEventType[] sequence= new RecorderEventType[]{RecorderEventType.STARTED,RecorderEventType.FACILITY_ERROR};
		super.currentListener.setExpectedQueue(sequence);
		r.addListener((RecorderListener) currentListener);

		PushBufferStream thrower=new PushBufferStream()
		{

			public Format getFormat() {
				// TODO Auto-generated method stub
				return null;
			}

			public void read(Buffer buffer) throws IOException {
				throw new RuntimeException("Throw from setTransferHandler.");
				
			}

			public void setTransferHandler(BufferTransferHandler transferHandler) {
				
				
			}

			public boolean endOfStream() {
				
				return false;
			}

			public ContentDescriptor getContentDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}

			public long getContentLength() {
				// TODO Auto-generated method stub
				return 0;
			}

			public Object getControl(String controlType) {
				// TODO Auto-generated method stub
				return null;
			}

			public Object[] getControls() {
				// TODO Auto-generated method stub
				return null;
			}};
			setUpFiles("wav",true);
		r.start(recordFile.toString(), thrower);
		if(!doTest(1))
		{
			fail(getReason());
		}
	}

	
	
	
	
	
	
	protected class RecorderEventTestNotificationListener extends EventTypeListener implements RecorderListener
	{
		

		public RecorderEventTestNotificationListener() {
			
		}


		public RecorderEventTestNotificationListener(
				RecorderEventType[] expectedEventsQueue) {
			super(expectedEventsQueue);
			
			
		}




		public void update(RecorderEvent event) {
			
			super.doOnEventCheck(event.getEventType());
			
		}
		
		
	}



	@Override
	protected EventTypeListener getEventTypeListener() {
		return new RecorderEventTestNotificationListener();
	}
	
	
	
	
	
	
	
	
	
	
}

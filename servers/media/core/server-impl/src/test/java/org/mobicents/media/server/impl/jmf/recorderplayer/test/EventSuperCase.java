package org.mobicents.media.server.impl.jmf.recorderplayer.test;

import java.io.File;

import org.mobicents.media.server.impl.SuperXCase;

public abstract class EventSuperCase extends SuperXCase {

	protected EventTypeListener currentListener=null;
	protected String recordDirName = System.getenv("JBOSS_HOME");
	protected File recorDir = new File(recordDirName + File.separator
			+ "server" + File.separator + "default" + File.separator + "tmp");
	protected String recordFileName = "RecorderEventTest";
	protected File recordFile = null;
	protected boolean tmpCreated, defaultCreated;

	
	
	protected void setUpFiles(String extension, boolean makeFakeFile) throws Exception {

		if (!recorDir.getParentFile().exists()) {
			recorDir.getParentFile().mkdir();
			defaultCreated = true;
		}

		if (!recorDir.exists()) {
			recorDir.mkdir();
			tmpCreated = true;
		}

		recordFile = new File(recorDir, recordFileName + "." + extension);
		if(makeFakeFile)
		{
			recordFile.createNewFile();
		}
	}

	protected void setUpFiles(String extension) throws Exception
	{
		 this.setUpFiles(extension, false);
		
	}
	
	protected void destroyFiles() {
		if (defaultCreated) {
			recorDir.getParentFile().delete();

		} else if (tmpCreated) {
			recorDir.delete();
		}

		defaultCreated = tmpCreated = false;
		if (recordFile.exists())
			recordFile.delete();

	}
	
	
	
	
	
	
	
	
	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
		this.currentListener=getEventTypeListener();
	}

	@Override
	protected void tearDown() throws Exception {
		
		this.currentListener.doEndCheck();
		this.destroyFiles();
		super.tearDown();
		
	}

	protected class EventTypeListener
	{
		protected boolean[] receivalIndex=null;
		protected Enum[] expectedQueue=null;
		
		
		
		
		public void setExpectedQueue(Enum[] expectedQueue) {
			this.expectedQueue = expectedQueue;
			this.receivalIndex=new boolean[this.expectedQueue.length];
		}

		public EventTypeListener(Enum[] expectedQueue) {
			super();
			this.setExpectedQueue(expectedQueue);
		}

		public EventTypeListener() {
			
		}
		protected void doOnEventCheck(Enum eventType)
		{
			
			boolean matched=false;
			if(this.receivalIndex[this.receivalIndex.length-1])
			{
				doFail("Out of queue event, too many events delivered,["+eventType+"]!!!");
			}
			for(int i=0;i<receivalIndex.length;i++)
			{
				if(!this.receivalIndex[i])
				{
					if(this.expectedQueue[i]==eventType)
					{
						this.receivalIndex[i]=true;
						matched=true;
						break;
					}else
					{
						matched=false;
					}
				}	
			}
			
			if(!matched)
			{
				doFail("Oout of queue event - it didnt match defined aligment["+eventType+"]");
			}
			
			
		}
		
		public void doEndCheck()
		{
			for(int i=0;i<receivalIndex.length;i++)
			{
				if(!receivalIndex[i])
				{
					doFail("Didnt receive one or more messages!!!. Index of lakcign message ["+i+"] type ["+expectedQueue[i]+"]");
				}
			}
		}
		
		
	}
	
	protected abstract EventTypeListener getEventTypeListener();
	
	
}

package org.mobicents.mscontrol.impl;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Start time:16:25:53 2008-12-10<br>
 * Project: mobicents-media-server-core<br>
 * This class is super class which should be extendsed by all elements that performs some actions - send events, schedule transactions etc. It enforces synced execution without sync
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public abstract class MsActionPerformer implements Runnable{

	/**
     * This list holds submited actions
     */
    protected LinkedList<Runnable> taskList=new LinkedList<Runnable>();	
	
    /**
     * Adds runnable to queue, schedules to execution
     * @param r
     */
    public void submit(Runnable r)
    {
    	//No need to sync, since we schedule r first.
    	//this.taskList.add(r);
    	MsProviderImpl.submit(r);
    }


	public void run() {
		
		//Remove is not atomic, we need to sync
		synchronized (taskList) {

			try{
				taskList.remove().run();
			}catch(NoSuchElementException nsee)
			{
			//This should not happen
				nsee.printStackTrace();
			}
		}
	}
    
    
	
}

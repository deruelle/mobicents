/**
 * Start time:11:29:01 2009-08-10<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.cluster.test;

import java.io.Serializable;
import java.util.TimerTask;

/**
 * Start time:11:29:01 2009-08-10<br>
 * Project: mobicents-jainslee-server-core<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public class SerTimerTask extends TimerTask implements Serializable
{

	private String id = ""+( (Math.random()*10000)/1l);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.mobicents.slee.core.timers.TimerTask#run()
	 */
	@Override
	public void run() {
		System.err.println("------------------------ TIMER RUN COMPLETER["+id+"] ------------------------");
		
	}}

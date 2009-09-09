/**
 * 
 */
package org.mobicents.slee.core.timers;

import javax.transaction.Status;
import javax.transaction.Synchronization;

/**
 * @author martins
 *
 */
public class TransactionSynchronization implements Synchronization {

	/**
	 * 
	 */
	private final Runnable commitAction;
	
	/**
	 * 
	 */
	private final Runnable rollbackAction;
	
	/**
	 * @param commitAction
	 * @param rollbackAction
	 */
	TransactionSynchronization(Runnable commitAction,
			Runnable rollbackAction) {
		this.commitAction = commitAction;
		this.rollbackAction = rollbackAction;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.transaction.Synchronization#afterCompletion(int)
	 */
	public void afterCompletion(int status) {
		switch (status) {
			case Status.STATUS_COMMITTED:
				if (commitAction != null)
					commitAction.run();
				break;

			case Status.STATUS_ROLLEDBACK:
				if (rollbackAction != null)
					rollbackAction.run();
				break;

			default:				
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.transaction.Synchronization#beforeCompletion()
	 */
	public void beforeCompletion() {			
		
	}
	
}

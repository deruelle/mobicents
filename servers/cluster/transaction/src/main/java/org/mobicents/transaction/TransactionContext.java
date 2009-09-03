/***************************************************
 *                                                 *
 *  Mobicents: The Open Source VoIP Platform      *
 *                                                 *
 *  Distributable under LGPL license.              *
 *  See terms of license at gnu.org.               *
 *                                                 *
 *  Created on 2005-5-28                             *
 *                                                 *
 ***************************************************/

package org.mobicents.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * The local context of a transaction.
 * 
 * Provides a {@link Map} to store data in the transaction.
 * 
 * Provides various {@link List}s for {@link TransactionalAction}s to be added.
 * Those lists can be:
 * 
 * + Before Commit Action, an action to execute before the transaction is
 * committed
 * 
 * 
 * + After Commit Action, an action to execute after the transaction is
 * committed and the priority actions execution
 * 
 * + After Rollback Action, an action to execute after the transaction rollbacks
 * 
 * @author ?
 * @author martins 
 * 
 */
public class TransactionContext {

	private static Logger log = Logger.getLogger(TransactionContext.class);

	// this code was hack to trap setRollbackOnly() due to jboss cache, don't
	// remove it may be needed again
	// private boolean rollbackOnly = false;

	/**
	 * {@link TransactionalAction}s which should be executed after transaction
	 * commit succeeds
	 */
	protected List<TransactionalAction> afterCommitActions;

	/**
	 * {@link TransactionalAction}s which should be executed after transaction
	 * rollback
	 */
	protected List<TransactionalAction> afterRollbackActions;

	/**
	 * {@link TransactionalAction}s which should be executed before transaction
	 * commits
	 */
	protected List<TransactionalAction> beforeCommitActions;

	/**
	 * transaction data
	 */
	protected Map data;

	/**
	 * Retrieves the list of actions which should be executed after commit
	 * succeeds
	 * 
	 * @return
	 */
	public List<TransactionalAction> getAfterCommitActions() {
		if (afterCommitActions == null) {
			afterCommitActions = new ArrayList<TransactionalAction>();
		}
		return afterCommitActions;
	}

	/**
	 * Retrieves the list of actions which should be executed after rollback
	 * 
	 * @return
	 */
	public List<TransactionalAction> getAfterRollbackActions() {
		if (afterRollbackActions == null) {
			afterRollbackActions = new ArrayList<TransactionalAction>();
		}
		return afterRollbackActions;
	}

	/**
	 * Retrieves the list of actions which should be executed before commit
	 * 
	 * @return
	 */
	public List<TransactionalAction> getBeforeCommitActions() {
		if (beforeCommitActions == null) {
			beforeCommitActions = new ArrayList<TransactionalAction>();
		}
		return beforeCommitActions;
	}

	// ------- DATA MANAGEMENT

	public Map getData() {
		if (data == null) {
			data = new HashMap();
		}
		return data;
	}

	// ------- ACTIONS EXECUTION

	/**
	 * Executes actions scheduled after commit succeeds
	 */
	protected void executeAfterCommitActions() {

		if (afterCommitActions != null) {
			if (log.isDebugEnabled()) {
				log.debug("Executing after commit actions");
			}
			executeActions(afterCommitActions);
		}
	}

	/**
	 * Executes actions scheduled for after a rollback
	 */
	protected void executeAfterRollbackActions() {

		if (afterRollbackActions != null) {
			if (log.isDebugEnabled()) {
				log.debug("Executing rollback actions");
			}
			executeActions(afterRollbackActions);
		}
	}

	/**
	 * Executes actions scheduled for before commit
	 */
	protected void executeBeforeCommitActions() {

		if (beforeCommitActions != null) {
			if (log.isDebugEnabled()) {
				log.debug("Executing before commit actions");
			}
			executeActions(beforeCommitActions);
		}
	}

	protected void executeActions(List<TransactionalAction> actions) {

		for (TransactionalAction action : actions) {
			if (log.isDebugEnabled())
				log.debug("Executing action:" + action);
			try {
				action.execute();
			} catch (Throwable t) {
				throw new RuntimeException("Failed while executing action", t);
			}
		}
	}

	/**
	 * Cleanups any state the entry has created.
	 */
	protected void cleanup() {
		afterCommitActions = null;
		afterRollbackActions = null;
		beforeCommitActions = null;
		data = null;
	}


}
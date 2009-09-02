/**
 * Start time:20:11:00 2009-09-02<br>
 * Project: mobicents-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.transaction;

import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;

/**
 * Start time:20:11:00 2009-09-02<br>
 * Project: mobicents-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class ExtendedTransactionManagerImpl implements ExtendedTransactionManager {

	private static final transient Logger log = Logger.getLogger(ExtendedTransactionManagerImpl.class);
	protected TransactionManager transactionManager;

	public ExtendedTransactionManagerImpl(TransactionManager transactionManager) {
		super();
		this.transactionManager = transactionManager;
	}

	/**
	 * transaction context per transaction map
	 */
	protected ConcurrentHashMap<Transaction, TransactionContext> transactionContexts = new ConcurrentHashMap<Transaction, TransactionContext>();

	public void addAfterCommitAction(TransactionalAction action) throws SystemException {
		getTransactionContext().getAfterCommitActions().add(action);
	}

	public void addAfterRollbackAction(TransactionalAction action) throws SystemException {
		getTransactionContext().getAfterRollbackActions().add(action);
	}

	public void addBeforeCommitAction(TransactionalAction action) throws SystemException {
		getTransactionContext().getBeforeCommitActions().add(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.transaction.ExtendedTransactionManager#
	 * displayOngoingSleeTransactions()
	 */
	public String displayOngoingSleeTransactions() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.transaction.ExtendedTransactionManager#getRollbackOnly()
	 */
	public boolean getRollbackOnly() throws SystemException {
		Transaction tx = getTransaction();
		if (tx == null) {
			throw new SystemException("no transaction");
		}
		return tx.getStatus() == Status.STATUS_MARKED_ROLLBACK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.transaction.ExtendedTransactionManager#mandateTransaction()
	 */
	public void mandateTransaction() throws IllegalStateException {

		Transaction tx = null;
		try {
			tx = getTransaction();
		} catch (SystemException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		if (tx == null)
			throw new IllegalStateException("Transaction Mandatory");
		int status = -1;
		try {
			status = tx.getStatus();
		} catch (SystemException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		if (status != Status.STATUS_ACTIVE && status != Status.STATUS_MARKED_ROLLBACK) {
			throw new IllegalStateException("There is no active tx, tx is in state: " + status);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.transaction.ExtendedTransactionManager#requireTransaction()
	 */
	public boolean requireTransaction() {
		try {
			Transaction tx = getTransaction();
			int status = -1;
			if (tx != null) {
				status = tx.getStatus();
			}
			if (tx == null || (status != Status.STATUS_ACTIVE && status != Status.STATUS_MARKED_ROLLBACK)) {
				// start a new one
				this.begin();
				return true;
			}
		} catch (NotSupportedException e) {
			log.error("Exception creating transaction", e);
		} catch (SystemException e) {
			log.error("Caught SystemException in checking transaction", e);
		}
		return false;
	}

	public Transaction getTransaction() throws SystemException {
		if (transactionManager != null) {
			return transactionManager.getTransaction();
		} else {
			throw new SystemException("tx manager unavailable");
		}
	}

	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		if (log.isDebugEnabled()) {
			log.debug("Starting rollback of tx " + getTransaction());
		}
		transactionManager.rollback();
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		if (transactionManager != null) {
			transactionManager.setRollbackOnly();
			if (log.isDebugEnabled()) {
				log.debug("rollbackonly set on tx " + getTransaction());
			}
		} else {
			throw new SystemException("tx manager unavailable");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.TransactionManager#begin()
	 */
	public void begin() throws NotSupportedException, SystemException {
		// begin transaction
		transactionManager.begin();
		Transaction tx = getTransaction();
		if (log.isDebugEnabled()) {
			log.debug("Transaction started: " + tx);
		}
		// register for call-backs
		try {
			tx.registerSynchronization(new SynchronizationHandler(tx));
		} catch (RollbackException e) {
			throw new SystemException("Unable to register listener for created transaction. Error: " + e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.TransactionManager#commit()
	 */
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
		if (log.isDebugEnabled()) {
			log.debug("Starting commit of tx " + getTransaction());
		}

		transactionManager.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.TransactionManager#getStatus()
	 */
	public int getStatus() throws SystemException {
		int status = transactionManager.getStatus();

		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.transaction.TransactionManager#resume(javax.transaction.Transaction
	 * )
	 */
	public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
		if (transactionManager != null) {
			if (log.isDebugEnabled()) {
				log.debug("Resuming tx " + transaction);
			}
			transactionManager.resume(transaction);
		} else {
			throw new SystemException("tx manager unavailable");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.TransactionManager#setTransactionTimeout(int)
	 */
	public void setTransactionTimeout(int seconds) throws SystemException {
		if (transactionManager != null) {
			transactionManager.setTransactionTimeout(seconds);
		} else {
			throw new SystemException("tx manager unavailable");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.transaction.TransactionManager#suspend()
	 */
	public Transaction suspend() throws SystemException {
		if (transactionManager != null) {
			if (log.isDebugEnabled()) {
				log.debug("Suspending tx " + getTransaction());
			}
			return transactionManager.suspend();
		} else {
			throw new SystemException("tx manager unavailable");
		}
	}

	public TransactionContext getTransactionContext() throws SystemException {
		Transaction tx = getTransaction();
		if (tx == null) {
			throw new SystemException("no transaction");
		}
		TransactionContext tc = transactionContexts.get(tx);
		if (tc == null) {
			tc = new TransactionContext();
			transactionContexts.put(tx, tc);
		}
		return tc;
	}

	// --- TX MANAGER LISTENER

	class SynchronizationHandler implements Synchronization {
		private Transaction tx;

		public SynchronizationHandler(Transaction tx) {
			this.tx = tx;
		}

		public void afterCompletion(int status) {
			// get tx context
			TransactionContext txContext = transactionContexts.remove(tx);
			if (txContext != null) {
				switch (status) {

				case Status.STATUS_COMMITTED:
					if (log.isDebugEnabled()) {
						log.debug("Transaction committed: " + tx);
					}

					txContext.executeAfterCommitActions();
					break;

				case Status.STATUS_ROLLEDBACK:
					if (log.isDebugEnabled()) {
						log.debug("Transaction rollback: " + tx);
					}
					txContext.executeAfterRollbackActions();
					break;

				default:
					throw new IllegalStateException("Unexpected transaction state " + status);
				}
				txContext.cleanup();
			}
		}

		public void beforeCompletion() {
			// get entry and execute before commit actions
			TransactionContext tc = transactionContexts.get(tx);
			if (tc != null) {

				tc.executeBeforeCommitActions();
			}
		}
	}
}

/**
 * Start time:20:06:22 2009-09-02<br>
 * Project: mobicents-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.mobicents.transaction;


import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

/**
 * Start time:20:06:23 2009-09-02<br>
 * Project: mobicents-cluster<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author emmartins
 */
public interface ExtendedTransactionManager extends TransactionManager{

	/**
	 * Ensures a transaction exists, i.e., if there is no transaction one is
	 * created
	 * 
	 * @return true if a transaction was created
	 */
	public boolean requireTransaction();

	/**
	 * Verifies if we are in the context of a transaction.
	 * 
	 * @throws TransactionRequiredLocalException
	 *             if we are not in the context of a transaction.
	 */
	public void mandateTransaction() throws IllegalStateException;

	/**
	 * adds a new {@link TransactionalAction} that will be executed after the
	 * priority actions are executed, which is when the transaction commits
	 * 
	 * @param action
	 * @throws SystemException
	 */
	public void addAfterCommitAction(TransactionalAction action) throws SystemException;

	/**
	 * Convinience method to add a {@link TransactionalAction} to the
	 * {@link TransactionContext} and execute it if rollback occurrs. This
	 * method will ignore the action if there is no valid transaction, instead
	 * of failing.
	 * 
	 * @param action
	 * @throws SystemException
	 */
	public void addAfterRollbackAction(TransactionalAction action) throws SystemException;

	/**
	 * adds a new {@link TransactionalAction} that will be executed before the
	 * transaction is committed
	 * 
	 * @param action
	 * @throws SystemException
	 */
	public void addBeforeCommitAction(TransactionalAction action) throws SystemException;

	/**
	 * 
	 * @return true if the current transaction is marked for rollback
	 * @throws SystemException
	 */
	public boolean getRollbackOnly() throws SystemException;

	/**
	 * @return String - a list of ongoing SLEE transactions
	 */
	public String displayOngoingSleeTransactions();

	public void rollback() throws IllegalStateException, SecurityException, SystemException;

	public void setRollbackOnly() throws IllegalStateException, SystemException;
}

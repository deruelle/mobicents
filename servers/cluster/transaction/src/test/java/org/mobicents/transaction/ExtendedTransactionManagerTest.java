package org.mobicents.transaction;

import javax.transaction.RollbackException;
import javax.transaction.Status;

import org.jboss.test.kernel.junit.MicrocontainerTest;

public class ExtendedTransactionManagerTest extends MicrocontainerTest {
	
	private ExtendedTransactionManagerImpl _txMgr;
		
	public ExtendedTransactionManagerTest(String name) {
		super(name);
	}
	
	private ExtendedTransactionManagerImpl getExtendedTransactionManagerImpl() {
		if (_txMgr == null) {
			_txMgr = (ExtendedTransactionManagerImpl) getBean("Mobicents.TransactionManagerMBean");
		}
		return _txMgr;
	}
	
	public void testCommit() throws Exception {
		ExtendedTransactionManagerImpl txMgr = getExtendedTransactionManagerImpl();
		txMgr.begin();
		txMgr.mandateTransaction();
		txMgr.commit();		
		assertTrue("getTransaction should return null instead it returned "+txMgr.getTransaction(), txMgr.getTransaction() == null);
	}
	
	public void testRequireTransaction() throws Exception {
		ExtendedTransactionManagerImpl txMgr = getExtendedTransactionManagerImpl();
		if (!txMgr.requireTransaction()) {
			fail("requireTransaction() didn't create a transaction");
		}
		txMgr.commit();		
		assertTrue("getTransaction should return null instead it returned "+txMgr.getTransaction(), txMgr.getTransaction() == null);
	}
	
	public void testRollback() throws Exception {
		ExtendedTransactionManagerImpl txMgr = getExtendedTransactionManagerImpl();
		txMgr.begin();
		txMgr.mandateTransaction();
		txMgr.rollback();		
		assertTrue("getTransaction should return null instead it returned "+txMgr.getTransaction(), txMgr.getTransaction() == null);
	}
	
	public void testRollbackOnly() throws Exception {
		ExtendedTransactionManagerImpl txMgr = getExtendedTransactionManagerImpl();
		txMgr.begin();
		txMgr.mandateTransaction();
		assertFalse("tx manager getRollbackOnly should be false", txMgr.getRollbackOnly());		
		txMgr.setRollbackOnly();
		assertTrue("tx manager getRollbackOnly after setRollbackOnly should be true", txMgr.getRollbackOnly());
		assertTrue("tx state should be marked for rollback, instead it is "+txMgr.getStatus(), txMgr.getStatus() == Status.STATUS_MARKED_ROLLBACK);
		try {
			txMgr.commit();	
			fail("tx committed when it should had rollback");
		} catch (RollbackException e) {
			// test passes
		}
	}
	
	
	

	
}

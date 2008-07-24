package org.mobicents.qa.performance.jainsip.inspector;

import java.util.concurrent.atomic.AtomicInteger;

public class TestInspector implements TestProbe, TestObserver {

    private AtomicInteger createdAmount;
    private AtomicInteger terminatedAmount;
    private AtomicInteger unexpectedAmount;
    
    private AtomicInteger createdAmountAcc;
    private AtomicInteger terminatedAmountAcc;
    private AtomicInteger unexpectedAmountAcc;
    
    public TestInspector () {
	createdAmount = new AtomicInteger(0);
	terminatedAmount = new AtomicInteger(0);
	unexpectedAmount = new AtomicInteger(0);
	
	createdAmountAcc = new AtomicInteger(0);
	terminatedAmountAcc = new AtomicInteger(0);
	unexpectedAmountAcc = new AtomicInteger(0);
    }
    
    public void dialogCreated() {
	createdAmount.getAndIncrement();
    }
    
    public void dialogTerminated() {
	terminatedAmount.getAndIncrement();
    }
    
    public void dialogTerminatedUnexpectedly() {
	unexpectedAmount.getAndIncrement();
    }
    
    public int getCreatedDialogCount() {
	return createdAmountAcc.get() + createdAmount.get();
    }
    
    public int getTerminatedDialogCount() {
	return terminatedAmountAcc.get() + terminatedAmount.get();
    }
    
    public int getTerminatedUnexpectedlyDialogCount() {
	return unexpectedAmountAcc.get() + unexpectedAmount.get();
    }

    public int getAndResetCreatedDialogCount() {
	int value = createdAmount.getAndSet(0);
	createdAmountAcc.addAndGet(value);
	return value;
    }

    public int getAndResetTerminatedDialogCount() {
	int value = terminatedAmount.getAndSet(0);
	terminatedAmountAcc.addAndGet(value);
	return value;
    }

    public int getAndResetTerminatedUnexpectedlyDialogCount() {
	int value = unexpectedAmount.getAndSet(0);
	unexpectedAmountAcc.addAndGet(value);
	return value;
    }
}

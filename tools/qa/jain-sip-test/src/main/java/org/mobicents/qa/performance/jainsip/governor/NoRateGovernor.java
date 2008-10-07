package org.mobicents.qa.performance.jainsip.governor;

import java.util.Timer;
import java.util.TimerTask;

import org.mobicents.qa.performance.jainsip.inspector.TestObserver;
import org.mobicents.qa.performance.jainsip.util.SippController;

public class NoRateGovernor implements Governor {

    private TestObserver observer;
    private SippController controller;
    private boolean endTest;

    public NoRateGovernor(TestObserver observer, SippController controller) {
	this.observer = observer;
	this.controller = controller;
	this.endTest = false;

	new Timer().schedule(new NoRateGovernorTimerTask(), 0l, 1 * 1000);
    }

    public TestObserver getObserver() {
	return this.observer;
    }

    public void endTest() {
	this.controller.quit();
	endTest = true;
    }

    private class NoRateGovernorTimerTask extends TimerTask {

	public NoRateGovernorTimerTask() {
	    controller.setRate(0);
	}

	public void run() {
	    if (endTest) {
		return;
	    }
	    if (observer.getCreatedDialogCount() == 0) {
		System.out.println("FixedRateGovernor: Trying to lock ... ");
	    } else {
		System.out.println("FixedRateGovernor: Dialog count; " + observer.getAndResetCreatedDialogCount());
	    }
	}
    }

}

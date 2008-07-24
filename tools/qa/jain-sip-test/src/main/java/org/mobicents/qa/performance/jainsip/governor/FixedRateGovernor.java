package org.mobicents.qa.performance.jainsip.governor;

import java.util.Timer;
import java.util.TimerTask;

import org.mobicents.qa.performance.jainsip.inspector.TestObserver;
import org.mobicents.qa.performance.jainsip.util.SippController;

public class FixedRateGovernor implements Governor {

    private TestObserver observer;
    private SippController controller;
    private boolean endTest;

    public FixedRateGovernor(TestObserver observer, SippController controller, int rate) {
	this.observer = observer;
	this.controller = controller;
	this.endTest = false;

	new Timer().schedule(new FixedRateGovernorTimerTask(rate), 0l, 1 * 1000);
    }

    public TestObserver getObserver() {
	return this.observer;
    }

    public void endTest() {
	this.controller.quit();
	endTest = true;
    }

    private class FixedRateGovernorTimerTask extends TimerTask {
	
	private int rate;
	
	public FixedRateGovernorTimerTask(int rate) {

	    controller.setRate(0);
	    this.rate = rate;
	}

	public void run() {
	    if (endTest) {
		return;
	    }
	    if (observer.getCreatedDialogCount() == 0) {
		controller.setRate(rate);
		System.out.println("FixedRateGovernor: Trying to lock ... ");
	    } else {
		System.out.println("FixedRateGovernor: Keeping same rate: " + rate);
	    }
	}
    }

}

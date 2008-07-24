package org.mobicents.qa.performance.jainsip.governor;

import java.util.Timer;
import java.util.TimerTask;

import org.mobicents.qa.performance.jainsip.inspector.TestObserver;
import org.mobicents.qa.performance.jainsip.util.SippController;

public class LinearRateGovernor implements Governor {

    private TestObserver observer;
    private SippController controller;
    private boolean endTest;

    public LinearRateGovernor(TestObserver observer, SippController controller, int period, int increment) {
	this.observer = observer;
	this.controller = controller;
	this.endTest = false;

	new Timer().schedule(new LinearTestGovernorTimerTask(increment), 0l, period * 1000);
    }

    public TestObserver getObserver() {
	return this.observer;
    }

    public void endTest() {
	this.controller.quit();
	endTest = true;
    }

    private class LinearTestGovernorTimerTask extends TimerTask {

	private int increment;
	private int currentRate;

	public LinearTestGovernorTimerTask(int increment) {
	    this.increment = increment;
	    this.currentRate = 0;
	    controller.setRate(0);
	}

	public void run() {
	    if (endTest) {
		return;
	    }
	    if (observer.getCreatedDialogCount() == 0) {
		controller.setRate(0);
		controller.increaseRate(increment);
		currentRate = increment;
		System.out.println("LinearRateGovernor: Trying to lock ... ");
	    } else {
		controller.increaseRate(increment);
		System.out.println("LinearRateGovernor: New rate: " + (currentRate += increment));
	    }
	}
    }

}

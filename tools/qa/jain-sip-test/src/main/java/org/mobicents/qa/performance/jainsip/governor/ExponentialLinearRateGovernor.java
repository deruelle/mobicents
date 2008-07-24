package org.mobicents.qa.performance.jainsip.governor;

import java.util.Timer;
import java.util.TimerTask;

import org.mobicents.qa.performance.jainsip.inspector.TestObserver;
import org.mobicents.qa.performance.jainsip.util.SippController;

public class ExponentialLinearRateGovernor implements Governor {

    private TestObserver observer;
    private SippController controller;
    private boolean endTest;

    public ExponentialLinearRateGovernor(TestObserver observer, SippController controller, int period, double exponentialFactor, int linearIncrement) {
	this.observer = observer;
	this.controller = controller;
	this.endTest = false;

	new Timer().schedule(new ExponentialLinearRateGovernorTimerTask(exponentialFactor, linearIncrement, period), 0l, 1 * 1000);
    }

    public TestObserver getObserver() {
	return this.observer;
    }

    public void endTest() {
	this.controller.quit();
	endTest = true;
    }

    private class ExponentialLinearRateGovernorTimerTask extends TimerTask {

	private double factor;
	private int increment;
	private int period;

	private int tickCount;
	private int currentRate;
	private double exponentialThreshold;

	public ExponentialLinearRateGovernorTimerTask(double exponentialFactor, int linearIncrement, int period) {
	    this.factor = exponentialFactor;
	    this.increment = linearIncrement;
	    this.period = period;

	    this.tickCount = 0;
	    this.currentRate = 0;
	    this.exponentialThreshold = linearIncrement;
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
		System.out.println("ExponentialLinearRateGovernor: Trying to lock ... ");
	    } else {
		if (tickCount++ == period) {
		    tickCount = 0;

		    if (currentRate < exponentialThreshold) {
			currentRate = (int) Math.round(currentRate * factor);
			controller.setRate(currentRate);
			System.out.println("ExponentialLinearRateGovernor: Exponential growth: " + currentRate);
		    } else {
			currentRate += increment;
			controller.setRate(currentRate);
			System.out.println("ExponentialLinearRateGovernor: Linear growth: " + currentRate);
		    }
		    if (currentRate > (exponentialThreshold * factor * factor)) {
			exponentialThreshold = exponentialThreshold * factor;
			System.out.println("ExponentialLinearRateGovernor: New exponential threshold: " + exponentialThreshold);
		    }
		}
		int count = observer.getAndResetCreatedDialogCount();
		int backOff = (int) Math.round(currentRate / factor);
		if (count < backOff) {
		    currentRate = backOff;
		    controller.setRate(currentRate);
		    System.out.println("ExponentialLinearRateGovernor: Dialogs count is " + count + ": Backing off: " + backOff);
		}
	    }
	}
    }

}

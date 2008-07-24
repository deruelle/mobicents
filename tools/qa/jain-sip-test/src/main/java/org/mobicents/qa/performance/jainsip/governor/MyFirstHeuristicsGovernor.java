package org.mobicents.qa.performance.jainsip.governor;

import java.util.Timer;
import java.util.TimerTask;

import org.mobicents.qa.performance.jainsip.inspector.TestObserver;
import org.mobicents.qa.performance.jainsip.util.SippController;

public class MyFirstHeuristicsGovernor implements Governor {

    private TestObserver observer;
    private SippController controller;
    private boolean endTest;

    public MyFirstHeuristicsGovernor(TestObserver observer, SippController controller) {
	this.observer = observer;
	this.controller = controller;
	this.endTest = false;

	new Timer().schedule(new MyFirstHeuristicsGovernorTimerTask(), 0, 1 * 1000);
    }

    public TestObserver getObserver() {
	return this.observer;
    }

    public void endTest() {
	this.controller.quit();
	endTest = true;
    }

    private class MyFirstHeuristicsGovernorTimerTask extends TimerTask {

	private double backOffThreasholdFactor = 0.9;
	private int currentRate;

	public MyFirstHeuristicsGovernorTimerTask() {
	    this.currentRate = 0;
	    controller.setRate(0);
	}

	public void run() {
	    if (endTest) {
		return;
	    }
	    if (observer.getCreatedDialogCount() == 0) {
		controller.setRate(0);
		controller.increaseRate();
		currentRate = 1;
		System.out.println("MyFirstHeuristicsGovernor: Trying to lock ... ");
	    } else {
		int count = observer.getAndResetCreatedDialogCount();
		int backOffThreashold = (int) Math.round(currentRate * backOffThreasholdFactor);
		
		if ((count < backOffThreashold) && (currentRate >= 10)) {
		    currentRate -= 10;
		    controller.decreaseRateTen();
		    System.out.println("MyFirstHeuristicsGovernor: Dialogs count is " + count + ": Backing off: " + currentRate);
		} else if (count == currentRate) {
		    currentRate++;
		    controller.increaseRate();
		    System.out.println("MyFirstHeuristicsGovernor: Dialogs count is " + count + ": Increasing rate: " + currentRate);
		} else {
		    System.out.println("MyFirstHeuristicsGovernor: Dialogs count is " + count + ": Keeping rate: " + currentRate);
		}
	    }
	}
    }

}

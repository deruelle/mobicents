package org.mobicents.media.server.load.test.gui;


public class RunnableImpl implements Runnable {

	private int myName;

	public RunnableImpl(int i) {
		myName = i;
	}
	
	public void run() {
		System.out.println("Thread : " + getMyName());
		try {
			Thread.sleep(myName);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Thread : "+getMyName() +" Wakes up");
		
		
	}
	
	public int getMyName() {
		return myName;
	}

	public void setMyName(int myName) {
		this.myName = myName;
	}	

}

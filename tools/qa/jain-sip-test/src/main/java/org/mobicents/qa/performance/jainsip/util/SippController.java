package org.mobicents.qa.performance.jainsip.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SippController {

    private static final boolean debugFlag = false;

    public static final String INCREASE_ONE_COMMAND = new String("+");
    public static final String INCREASE_TEN_COMMAND = new String("*");
    public static final String DECREASE_ONE_COMMAND = new String("-");
    public static final String DECREASE_TEN_COMMAND = new String("/");
    public static final String QUIT_COMMAND = new String("q");

    public static final String SET_RATE_COMMAND = new String("c set rate");
    public static final String DUMP_TASKS_COMMAND = new String("c dump tasks");

    private DatagramSocket socket;

    private int controlPort;
    private InetAddress controlAddress;

    private int sippCallRate;

    public SippController(String controlIp, String controlPort) {
	try {
	    this.socket = new DatagramSocket();
	    this.controlPort = Integer.parseInt(controlPort);
	    this.controlAddress = InetAddress.getByName(controlIp);
	    this.sippCallRate = 0;
	    
	    Runtime.getRuntime().addShutdownHook(new Thread() {
		public void run() {
		    quit();
		}
	    });
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void setRate(int n) {
	sendMessage(SET_RATE_COMMAND + " " + n);
	sippCallRate = n;
    }
    
    public void increaseRate() {
	sendMessage(INCREASE_ONE_COMMAND);
	sippCallRate++;
    }
    
    public void increaseRate(int n) {
	if (n < 0) throw new IllegalArgumentException();
	for (int i = 0; i < n; i++){ 
	    increaseRate();
	}
    }
    
    public void decreaseRate() {
	sendMessage(DECREASE_ONE_COMMAND);
	sippCallRate++;
    }
    
    public void decreaseRate(int n) {
	if (n < 0) throw new IllegalArgumentException();
	for (int i = 0; i < n; i++){ 
	    decreaseRate();
	}
    }
    
    public void increaseRateTen() {
	sendMessage(INCREASE_TEN_COMMAND);
	sippCallRate++;
    }
    
    public void increaseRateTen(int n) {
	if (n < 0) throw new IllegalArgumentException();
	for (int i = 0; i < n; i++){ 
	    increaseRateTen();
	}
    }
    
    public void decreaseRateTen() {
	sendMessage(DECREASE_TEN_COMMAND);
	sippCallRate++;
    }
    
    public void decreaseRateTen(int n) {
	if (n < 0) throw new IllegalArgumentException();
	for (int i = 0; i < n; i++){ 
	    decreaseRateTen();
	}
    }
    
    public void quit() {
	sendMessage(QUIT_COMMAND);
	sendMessage(QUIT_COMMAND);
    }
    
    private void sendMessage(String msg) {
	try {
	    byte[] msgbytes = msg.getBytes();
	    socket.send(new DatagramPacket(msgbytes, msgbytes.length, controlAddress, controlPort));

	    if (debugFlag) {
		System.out.println("SippController: message sent: " + msg);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
}

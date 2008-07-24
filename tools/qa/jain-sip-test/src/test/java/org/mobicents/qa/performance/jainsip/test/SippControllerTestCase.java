package org.mobicents.qa.performance.jainsip.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.qa.performance.jainsip.util.SippController;

public class SippControllerTestCase {

    private SippController testSubject;
    private DatagramSocket socket;

    @Before
    public void init() {
	try {
	    String ip = "127.0.0.1";
	    String port = "" + ((int) Math.round(Math.random() * 50000) + 10000);

	    this.testSubject = new SippController(ip, port);
	    this.socket = new DatagramSocket(Integer.parseInt(port));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Test
    public void testSetRate() {
	int newRate = ((int) Math.round(Math.random() * 50000) + 10000);
	testSubject.setRate(newRate);
	testMsg(SippController.SET_RATE_COMMAND + " " + newRate);
    }

    @Test
    public void testIncreaseRateOne() {
	testSubject.increaseRate();
	testMsg(SippController.INCREASE_ONE_COMMAND);
    }

    @Test
    public void testIncreaseRateOneSeveral() {
	try {
	    testSubject.increaseRate(-1);
	    fail("Negative value accepted for increaseRate(int)");
	} catch (IllegalArgumentException e) {
	    // expected
	} catch (Throwable t) {
	    fail("Unexpected exception '" + t.getMessage() + "' for increaseRate(int)");
	}

	int newRate = (int) Math.round(Math.random() * 10);
	testSubject.increaseRate(newRate);

	for (int i = 0; i < newRate; i++) {
	    testMsg(SippController.INCREASE_ONE_COMMAND);
	}
    }

    @Test
    public void testIncreaseRateTen() {
	testSubject.increaseRateTen();
	testMsg(SippController.INCREASE_TEN_COMMAND);
    }

    @Test
    public void testIncreaseRateTenSeveral() {
	try {
	    testSubject.increaseRateTen(-1);
	    fail("Negative value accepted for increaseRateTen(int)");
	} catch (IllegalArgumentException e) {
	    // expected
	} catch (Throwable t) {
	    fail("Unexpected exception '" + t.getMessage() + "' for increaseRateTen(int)");
	}

	int newRate = (int) Math.round(Math.random() * 10);
	testSubject.increaseRateTen(newRate);

	for (int i = 0; i < newRate; i++) {
	    testMsg(SippController.INCREASE_TEN_COMMAND);
	}
    }
    
    @Test
    public void testDecreaseRateOne() {
	testSubject.decreaseRate();
	testMsg(SippController.DECREASE_ONE_COMMAND);
    }

    @Test
    public void testDecreaseRateOneSeveral() {
	try {
	    testSubject.decreaseRate(-1);
	    fail("Negative value accepted for decreaseRate(int)");
	} catch (IllegalArgumentException e) {
	    // expected
	} catch (Throwable t) {
	    fail("Unexpected exception '" + t.getMessage() + "' for decreaseRate(int)");
	}

	int newRate = (int) Math.round(Math.random() * 10);
	testSubject.decreaseRate(newRate);

	for (int i = 0; i < newRate; i++) {
	    testMsg(SippController.DECREASE_ONE_COMMAND);
	}
    }
    
    @Test
    public void testDecreaseRateTen() {
	testSubject.decreaseRateTen();
	testMsg(SippController.DECREASE_TEN_COMMAND);
    }

    @Test
    public void testDecreaseRateTenSeveral() {
	try {
	    testSubject.decreaseRateTen(-1);
	    fail("Negative value accepted for decreaseRateTen(int)");
	} catch (IllegalArgumentException e) {
	    // expected
	} catch (Throwable t) {
	    fail("Unexpected exception '" + t.getMessage() + "' for decreaseRateTen(int)");
	}

	int newRate = (int) Math.round(Math.random() * 10);
	testSubject.decreaseRateTen(newRate);

	for (int i = 0; i < newRate; i++) {
	    testMsg(SippController.DECREASE_TEN_COMMAND);
	}
    }
    
    @Test
    public void testQuit() {
	testSubject.quit();

	for (int i = 0; i < 2; i++) {
	    testMsg(SippController.QUIT_COMMAND);
	}
    }

    @After
    public void tearDown() {
	testSubject = null;
	socket.close();
	socket = null;
    }

    private void testMsg(String expectedMsg) {
	try {
	    byte[] buf = new byte[256];
	    DatagramPacket packet = new DatagramPacket(buf, buf.length);
	    socket.receive(packet);
	    String receivedMsg = new String(packet.getData()).substring(0, packet.getLength());

	    if (expectedMsg.compareTo(receivedMsg) != 0) {
		fail("Received '" + receivedMsg + "' while expecting '" + expectedMsg + "'");
	    }
	} catch (IOException e) {
	    fail("IOException on getMsg");
	}
    }
}

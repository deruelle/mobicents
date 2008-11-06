package org.mobicents.mscontrol.impl;

import junit.framework.TestCase;

import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsEndpoint;
import org.mobicents.mscontrol.MsLink;
import org.mobicents.mscontrol.MsPeer;
import org.mobicents.mscontrol.MsPeerFactory;
import org.mobicents.mscontrol.MsProvider;
import org.mobicents.mscontrol.MsSession;

/**
 * 
 * @author amit bhayani
 *
 */
public abstract class AbstractTest extends TestCase {

	protected boolean testPassed = false;
	protected String message = null;
	protected MsProvider msProvider = null;
	protected MsConnection msConnection = null;
	protected MsLink msLink = null;
	protected MsEndpoint msEndpoint = null;
	protected MsSession msSession = null;

	public AbstractTest(String name) {
		super(name);
	}

	public void setUp() throws ClassNotFoundException {
		testPassed = false;
		message = null;
		MsPeer msPeer = MsPeerFactory.getPeer("org.mobicents.mscontrol.impl.MsPeerImpl");
		msProvider = msPeer.getProvider();

	}

	public void tearDown() {

		// Let us wait till all event firing is done
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(message, true, testPassed);

		// Cleaning up
		if (msConnection != null) {
			msConnection.release();

			// Let us wait till all event firing is done
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (msLink != null) {
			msLink.release();
			
			// Let us wait till all event firing is done
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
	}

}

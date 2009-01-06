package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.text.ParseException;
import java.util.TooManyListenersException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

import org.junit.Assert;
import org.junit.Test;

public class ResourceListServerSipTest {

	private String[] publishers = {"sip:alice@"+ServerConfiguration.SERVER_HOST,"sip:bob@"+ServerConfiguration.SERVER_HOST};
	private String subscriber = "sip:carol@"+ServerConfiguration.SERVER_HOST;
	private String resourceList = "sip:carol_enemies@"+ServerConfiguration.SERVER_HOST;
	
	private RlsServicesManager rlsServicesManager = new RlsServicesManager(resourceList,publishers,this);
	
	@Test
	public void test() throws InterruptedException, SipException, InvalidArgumentException, ParseException, TooManyListenersException {
		// create rls services
		rlsServicesManager.putRlsServices();
		// create and init publishers
		Publisher publisher1 = new Publisher(publishers[0],6060,this);
		publisher1.publish();
		Publisher publisher2 = new Publisher(publishers[1],6061,this);
		publisher2.publish();
		// create and init subscriber
		Subscriber subscriber = new Subscriber(this.subscriber,resourceList,6062,this);
		subscriber.subscribe();
		// sleep half a sec
		Thread.sleep(90000);
		// unpublish
		publisher1.unpublish();
		publisher2.unpublish();
		// unsubscribe
		subscriber.unsubscribe();
		// remove rls services
		rlsServicesManager.deleteRlsServices();
	}
		
	protected void failTest(String message) {
		Assert.fail(message);
		rlsServicesManager.deleteRlsServices();
	}
}

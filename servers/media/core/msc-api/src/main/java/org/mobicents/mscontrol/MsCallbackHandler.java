package org.mobicents.mscontrol;

/**
 * An application implements MsCallbackHandler and passes it to underlying
 * MsProvider so that they may interact with application and pass the MS Objects
 * like MsConnection, MsLink, MsSession and MsResource synchronously and give a
 * chance to application execute some logic before the event firing mechanism
 * starts.
 * 
 * This interface is different from Listener interfaces such that this is for
 * synchronous call while listeners are for asynchronous call
 * 
 * @author amit bhayani
 * 
 */
public interface MsCallbackHandler {
	public void handle(Object object);
}

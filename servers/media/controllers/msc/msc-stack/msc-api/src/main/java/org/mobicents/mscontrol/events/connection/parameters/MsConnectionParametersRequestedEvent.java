package org.mobicents.mscontrol.events.connection.parameters;

import org.mobicents.mscontrol.events.MsRequestedEvent;

/**
 * Instance of <code>MsConnectionParametersRequestedEvent</code> is passed to endpoint to
 * request endpoint to fire event containing parameters of a connection
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 * MsEventFactory factory = msProvider.getEventFactory();
 * MsDtmfRequestedEvent params = (MsDtmfRequestedEvent) factory.createRequestedEvent();
 * MsRequestedSignal[] signals = new MsRequestedSignal[] {};
 * MsRequestedEvent[] events = new MsRequestedEvent[] { params };
 * 
 * msEndpoint.execute(signals, events, connection);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 

 */
public interface MsConnectionParametersRequestedEvent extends MsRequestedEvent {
	
}

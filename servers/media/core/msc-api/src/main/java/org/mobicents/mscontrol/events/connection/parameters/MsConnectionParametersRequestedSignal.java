package org.mobicents.mscontrol.events.connection.parameters;

import org.mobicents.mscontrol.events.MsRequestedSignal;

/**
 * Instance of <code>MsConnectionParametersRequestedSignal</code> is passed to endpoint to
 * request endpoint to generate parameters signal and pass it on to the CA
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * MsEventFactory eventFactory = msProvider.getEventFactory();
 * 
 * MsRequestedSignal params = eventFactory.createRequestedSignal();
 * params.setTone(&quot;1&quot;);
 * MsRequestedSignal[] signals = new MsRequestedSignal[] { params };
 * MsRequestedEvent[] events = new MsRequestedEvent[];
 * 
 * msEndpoint.execute(signals, events, connection);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 */
public interface MsConnectionParametersRequestedSignal extends MsRequestedSignal {

}

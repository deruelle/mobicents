/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */

package org.mobicents.mscontrol.events.dtmf;

import org.mobicents.mscontrol.events.MsRequestedEvent;

/**
 * Instance of <code>MsDtmfRequestedSignal</code> is passed to endpoint to
 * request endpoint to fire DTMF (captured by endpoint) to listening application
 * (that implements {@link org.mobicents.mscontrol.MsNotificationListener})
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 * MsEventFactory factory = msProvider.getEventFactory();
 * MsDtmfRequestedEvent dtmf = (MsDtmfRequestedEvent) factory.createRequestedEvent(DTMF.TONE);
 * MsRequestedSignal[] signals = new MsRequestedSignal[] {};
 * MsRequestedEvent[] events = new MsRequestedEvent[] { dtmf };
 * 
 * msEndpoint.execute(signals, events, connection);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author Oleg Kulikov
 */
public interface MsDtmfRequestedEvent extends MsRequestedEvent {
	public String getPattern();

	/**
	 * Application can pass the pattern to be matched by Endpoint. Only if the
	 * pattern matches endpoint will fire DTMF events containing matched pattern
	 * of DTMF
	 * 
	 * @param pattern
	 */
	public void setPattern(String pattern);
}

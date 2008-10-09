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

import org.mobicents.mscontrol.events.MsRequestedSignal;

/**
 * Instance of <code>MsDtmfRequestedSignal</code> is passed to endpoint to
 * request endpoint to generate DTMF signal and pass it on to the user-agent
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * MsEventFactory eventFactory = msProvider.getEventFactory();
 * 
 * MsRequestedSignal dtmf = eventFactory.createRequestedSignal(DTMF.TONE);
 * dtmf.setTone(&quot;1&quot;);
 * MsRequestedSignal[] signals = new MsRequestedSignal[] { dtmf };
 * MsRequestedEvent[] events = new MsRequestedEvent[];
 * 
 * msEndpoint.execute(signals, events, connection);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author Oleg Kulikov
 */
public interface MsDtmfRequestedSignal extends MsRequestedSignal {
	public void setTone(String pattern);

	public String getTone();
}

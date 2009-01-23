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

package org.mobicents.mscontrol.events.ann;

import org.mobicents.mscontrol.events.MsRequestedSignal;

/**
 * Instance of <code>MsPlayRequestedSignal</code> is passed to endpoint to
 * play an announcement
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * MsEventFactory eventFactory = msProvider.getEventFactory();
 * 
 * MsPlayRequestedSignal play = null;
 * play = (MsPlayRequestedSignal) eventFactory.createRequestedSignal(MsAnnouncement.PLAY);
 * play.setURL(url);
 * 
 * MsRequestedSignal[] requestedSignals = new MsRequestedSignal[] { play };
 * MsRequestedEvent[] requestedEvents = new MsRequestedEvent[];
 * 
 * msEndpoint.execute(requestedSignals, requestedEvents, link);
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author Oleg Kulikov
 */
public interface MsPlayRequestedSignal extends MsRequestedSignal {
	public String getURL();

	public void setURL(String url);
}

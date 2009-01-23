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

package org.mobicents.mscontrol.events;

/**
 * <p>
 * These are the list of Actions that Endoint is suppose to follow when Call
 * Agent executes RequestedEvent on Endpoint.
 * </p>
 * <ul>
 * <li> <code>NOTIFY</code>Endpoint Notifies the event immediately to Call
 * Agent </li>
 * <br/>
 * <li> <code>IGNORE</code>As the name suggests Endpoint simply ignores this
 * event </li>
 * <br/>
 * <li> <code>COLLECT</code>Endpoint accumulates these events and then fires
 * accumulated event when some precondition is matched </li>
 * <br/>
 * </ul>
 * 
 * @author Oleg Kulikov
 */
public enum MsEventAction {
	NOTIFY, IGNORE, COLLECT
}

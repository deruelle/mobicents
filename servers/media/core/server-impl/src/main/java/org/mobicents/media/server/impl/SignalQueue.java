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
package org.mobicents.media.server.impl;

import java.io.Serializable;
import org.mobicents.media.server.spi.events.RequestedSignal;

/**
 *
 * @author kulikov
 */
public class SignalQueue implements Serializable {

    private BaseEndpoint endpoint;
    private BaseConnection connection;
    private RequestedSignal[] signals;
    private int index = 0;
    private AbstractSignal signal;

    protected SignalQueue(BaseEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void offer(RequestedSignal[] signals, BaseConnection connection) {
        this.signals = signals;
        this.connection = connection;
        next();
    }

    public void reset() {
        if (signal != null) {
            signal.cancel();
        }
        signal = null;
        signals = null;
        connection = null;
        index = 0;
    }

    protected void next() {
        try {
            if (index < signals.length) {
                signal = endpoint.getSignal(signals[index++]);
                signal.apply(connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

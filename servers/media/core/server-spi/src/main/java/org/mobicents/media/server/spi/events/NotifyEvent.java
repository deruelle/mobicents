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

package org.mobicents.media.server.spi.events;

import java.io.Serializable;
import org.mobicents.media.Component;

/**
 *
 * @author Oleg Kulikov
 */
public interface NotifyEvent extends Serializable {
    public final static int STARTED = 10000;
    public final static int COMPLETED = 20000;
    public final static int STOPPED = 30000;

    public final static int START_FAILED = 10001;
    public final static int TX_FAILED = 10002;
    public final static int RX_FAILED = 10003;
    
    public Component getSource();
    public int getEventID();
}

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

package org.mobicents.media.server.impl.events.line;

import org.mobicents.media.server.impl.AbstractSignal;
import org.mobicents.media.server.impl.BaseConnection;
import org.mobicents.media.server.impl.BaseEndpoint;
import org.mobicents.media.server.impl.MediaResource;

/**
 *
 * @author Oleg Kulikov
 */
public class CongestionSignal extends AbstractSignal {

    @Override
    public void apply(BaseConnection connection) {
        BaseEndpoint endpoint = (BaseEndpoint) connection.getEndpoint();
        CongestionToneGen generator = (CongestionToneGen) getMediaSource(MediaResource.AUDIO_PLAYER, connection);
        generator.start();
    }

    @Override
    public void apply(BaseEndpoint endpoint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

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

package org.mobicents.media.server.spi;

import org.mobicents.media.MediaSource;

/**
 * The component which is a source of synchronization for other components.
 * 
 * The example of source of synchronization is a Timer. The another example is a source component 
 * which is synchronized from stream.
 * 
 * @author kulikov
 */
public interface SyncSource {
    /**
     * Synchronize the process of media content processing from this timer.
     * 
     * @param mediaSource the media sources to synchronize.
     */
    public void sync(MediaSource mediaSource);
    
    /**
     * Disable synchronization process.
     * 
     * @param mediaSource the media source component.
     */
    public void unsync(MediaSource mediaSource);
    
    /**
     * Gets the absolute timestamp value.
     * 
     * @return the timestamp value in milliseconds.
     */
    public long getTimestamp(); 
    
    /**
     * Starts this component.
     */
    public void start();
    
    /**
     * Stops this component.
     */
    public void stop();

}

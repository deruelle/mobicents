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
package org.mobicents.media.server.spi.resource.audio;

import org.mobicents.media.server.spi.resource.AudioPlayer;

/**
 * Extends audio player with text to speech capabilities.
 * 
 * @author kulikov
 */
public interface AdvancedAudioPlayer extends AudioPlayer {
    /**
     * Assigns text to say.
     * 
     * @param text the text to say.
     */
    public void setText(String text);
    
    /**
     * Assign voice 
     * 
     * @param name the name of the voice
     */
    public void setVoice(String name);
}

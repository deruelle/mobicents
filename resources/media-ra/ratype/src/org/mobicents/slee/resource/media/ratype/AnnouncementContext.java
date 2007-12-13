/*
 * AnnouncementContext.java
 *
 * Created on 7 Èþíü 2007 ã., 18:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.ratype;

import java.net.URL;

/**
 *
 * @author Oleg Kulikov
 */
public interface AnnouncementContext extends MediaContext {
    public void play(URL url) throws IllegalStateException;
    public void stopPlayer() throws IllegalStateException;
    public boolean isPlaying();
}

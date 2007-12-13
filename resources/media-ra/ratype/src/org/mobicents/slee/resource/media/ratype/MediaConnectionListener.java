/*
 * MediaConnectionListener.java
 *
 * Created on 3 Èþíü 2007 ã., 16:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.mobicents.slee.resource.media.ratype;

/**
 *
 * @author Oleg Kulikov
 */
public interface MediaConnectionListener {
    public void onConnecting(ConnectionEvent evt);
    public void onConnected(ConnectionEvent evt);
    public void onDisconnected(ConnectionEvent evt);
    public void onFailed(ConnectionEvent evt);
}

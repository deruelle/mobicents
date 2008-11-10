/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.clock;

/**
 *
 * @author kulikov
 */
public interface TimerTask extends Runnable {
    public void started();
    public void ended();
}

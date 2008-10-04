/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.spi.events.pkg;

import org.mobicents.media.server.spi.events.EventIdentifier;

/**
 *
 * @author Oleg Kulikov
 */
public interface Test {
    public final static String PACKAGE_NAME = "org.mobicents.media.events.dtmf";
    
    public final static EventIdentifier SPECTRA = new EventID(PACKAGE_NAME, "SPECTRA");

}

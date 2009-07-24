/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.fft;

import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.impl.NotifyEventImpl;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class SpectrumEvent extends NotifyEventImpl implements NotifyEvent {

    public final static int SPECTRA = 40 ;
    
    private double[] spectra;
    
    public SpectrumEvent(BaseComponent component, double[] spectra) {
        super(component, SPECTRA);
        this.spectra = spectra;
    }
    

    public double[] getSpectra() {
        return spectra;
    }
    
}

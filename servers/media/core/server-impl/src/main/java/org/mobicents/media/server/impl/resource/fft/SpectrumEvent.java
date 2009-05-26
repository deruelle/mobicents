/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.fft;

import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.events.NotifyEvent;

/**
 *
 * @author kulikov
 */
public class SpectrumEvent implements NotifyEvent {

    public final static int SPECTRA = 1;
    
    private Endpoint endpoint;
    private Connection connection;
    private String resourceName;
    
    private double[] spectra;
    
    public SpectrumEvent(Endpoint endpoint, Connection connection, String resourceName, double[] spectra) {
        this.endpoint = endpoint;
        this.connection = connection;
        this.resourceName = resourceName;
        this.spectra = spectra;
    }
    
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public Connection getConnection() {
        return connection;
    }

    public double[] getSpectra() {
        return spectra;
    }
    
    public int getResourceID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getEventID() {
        return SPECTRA;
    }

    public String getResourceName() {
        return resourceName;
    }

}

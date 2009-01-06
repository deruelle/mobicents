/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.dsp;

import org.mobicents.media.server.spi.dsp.Codec;
import org.mobicents.media.server.spi.dsp.SignalingProcessor;

/**
 *
 * @author kulikov
 */
public abstract class BaseCodec implements Codec {
    protected SignalingProcessor dsp;


    public void setProc(SignalingProcessor processor) {
        dsp = processor;
    }
    
    
}

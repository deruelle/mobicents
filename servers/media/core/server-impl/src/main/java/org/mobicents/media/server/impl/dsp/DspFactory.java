/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.dsp;

import org.mobicents.media.Component;
import org.mobicents.media.ComponentFactory;

/**
 *
 * @author kulikov
 */
public class DspFactory implements ComponentFactory {
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Component newInstance(String name) {
        return new Processor(name);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.events.dtmf;

/**
 *
 * @author kulikov
 */
public class DtmfGeneratorFactory {
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public DtmfGenerator newInstance() {
        return new DtmfGenerator(name);
    }
}

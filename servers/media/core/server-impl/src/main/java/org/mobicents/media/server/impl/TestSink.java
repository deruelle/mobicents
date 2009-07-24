/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.format.AudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class TestSink extends AbstractSink {

    private String name;
    
    public TestSink(String name) {
        super(name);
        this.name = name;
    }
    
    private Format[] formats = new Format[]{
        new AudioFormat(AudioFormat.LINEAR, 8000, 16, 1, AudioFormat.LITTLE_ENDIAN, AudioFormat.SIGNED)
    };

    public Format[] getFormats() {
        return formats;
    }

    public boolean isAcceptable(Format format) {
        return true;
    }

    public void onMediaTransfer(Buffer buffer) {
        System.out.println("src=" + name + ", recv=" + buffer);
    }

}

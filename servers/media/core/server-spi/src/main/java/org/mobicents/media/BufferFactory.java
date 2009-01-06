/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author kulikov
 */
public class BufferFactory implements Serializable {

    private final static int BUFF_SIZE = 320;
    private ArrayList<Buffer> list = new ArrayList();
    private int size;

    public BufferFactory(int size) {
        this.size = size;
        for (int i = 0; i < size; i++) {
            Buffer buffer = new Buffer();
            buffer.setData(new byte[BUFF_SIZE]);
            buffer.setFactory(this);
            list.add(buffer);
        }
    }

    public Buffer allocate() {
        Buffer buffer = null;
        if (!list.isEmpty()) {
            buffer = list.remove(0);
        }

        if (buffer != null) {
            buffer.setOffset(0);
            buffer.setLength(0);
            return buffer;
        }
        
        buffer = new Buffer();
        buffer.setFactory(this);
        buffer.setData(new byte[BUFF_SIZE]);
        
        return buffer;
    }
    
    public void deallocate(Buffer buffer) {
       if (list.size() < size && buffer != null && buffer.getData() != null)  {
           buffer.setOffset(0);
           buffer.setLength(0);
           list.add(buffer);
       }
    }
}
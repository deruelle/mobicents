package org.mobicents.media;

import java.util.ArrayList;

public class BufferFactory {

    private int BUFF_SIZE = 8192;
    private ArrayList<Buffer> list = new ArrayList<Buffer>();
    private int size;

    public BufferFactory(int size) {
        this.size = size;
        init();
    }    
    
    public BufferFactory(int size, int buffSize) {
        this.size = size;
        this.BUFF_SIZE = buffSize;
        init();
    }

    private void init() {
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
        if (list.size() < size && buffer != null && buffer.getData() != null) {
            buffer.setOffset(0);
            buffer.setLength(0);
            buffer.setHeader(null);
            list.add(buffer);
        }
    }
}

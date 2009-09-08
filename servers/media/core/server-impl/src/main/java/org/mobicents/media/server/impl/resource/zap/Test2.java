/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.resource.zap;

/**
 *
 * @author kulikov
 */
public class Test2 {
    
    private byte[] data;
    public Test2(byte[] data) {
        this.data = data;
    }
    
    public void doAction() {
            data[2] = (byte)(data[0] + data[1]);
    }
    
    public static void main(String[] args) {
        byte[] data = new byte[272];
        
        double t1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int c = data[0] + data[1];
            data[2] = (byte)c;
        }
        double t2 = System.currentTimeMillis();
        System.out.println("Duration = " + ((t2-t1)/1000000));

        t1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            Test2 test = new Test2(data);
            test.doAction();
        }
        t2 = System.currentTimeMillis();
        System.out.println("Duration = " + ((t2-t1)/1000000));
        
    }

}

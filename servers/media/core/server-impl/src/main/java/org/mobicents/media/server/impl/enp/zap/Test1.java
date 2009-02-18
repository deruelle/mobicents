/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mobicents.media.server.impl.enp.zap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;

/**
 *
 * @author kulikov
 */
public class Test1 {

    public static void main(String[] args) throws Exception {
        FileInputStream fin = new FileInputStream("c:\\temp\\hdlc-data.hex");

        BufferedReader reader = new BufferedReader(new FileReader("c:\\temp\\mtp.txt"));
        String line = reader.readLine();
        String[] data = line.split(" ");

        FastHDLC hdlc = new FastHDLC();
        HdlcFrame h = new HdlcFrame();
        hdlc.fasthdlc_precalc();
        hdlc.fasthdlc_init(h);
        int b = -1;
        int i = 0;

        int frameLen = 0;
        String s = "";
        while ( i < data.length) {

            while (h.bits <= 24 && i < data.length) {
                b = Integer.parseInt(data[i++],16);
                hdlc.fasthdlc_rx_load_nocheck(h, b);
            }

            int res = hdlc.fasthdlc_rx_run(h);
            if (res == FastHDLC.RETURN_COMPLETE_FLAG) {
                frameLen = 0;
                System.out.println("Return complete flag");
            } else if (res == FastHDLC.RETURN_DISCARD_FLAG) {
                frameLen = 0;
                System.out.println("Return discard tag");
            } else if (res == FastHDLC.RETURN_EMPTY_FLAG) {
                frameLen = 0;
                System.out.println("Return empty flag");
            } else {
                if (frameLen > 279) {
                    System.out.println("To long");
                    frameLen = 0;
                } else {
                    System.out.print(Integer.toHexString(res) + " ");
                    frameLen++;
                }
            }

        }
    /*        System.out.println(s);
    String frames[] = s.split("01111110");
    System.out.println("Frame count " + frames.length);
    for (int k = 0; k < frames.length; k++) {
    if ((frames[k].length() / 8) * 8 == frames[k].length()) {
    System.out.println("GOOD FRAME " + k + ", len=" + frames[k].length());
    } else {
    System.out.println("BAD FRAME " + k + ", len=" + frames[k].length());
    }
    }
     */
    }
}

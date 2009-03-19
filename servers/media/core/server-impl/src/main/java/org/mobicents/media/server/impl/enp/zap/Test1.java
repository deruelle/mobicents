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
        //FileInputStream fin = new FileInputStream("c:\\temp\\hdlc-data.hex");

        //BufferedReader reader = new BufferedReader(new FileReader("c:\\temp\\mtp.txt"));
        //String line = reader.readLine();
        String[] data = "bf 7d f7 d8 00 0e 46 77 ef be fb 00 01 c8 ce fd f7 df 60 00 39 19 df be fb ec 00 07 23 3b f7 df 7d 80 00 e4 67 7e fb ef b0 00 1c 8c ef 7d f6".split(" ");

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
            	if(data[i].length()<=0) continue;
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
                    System.out.println("-- " + alignNumber(Integer.toHexString(res),2) + " " + alignNumber(Integer.toBinaryString(res),8));
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
    private static String alignNumber(String str, int num) {
    	while(str.length()<num) str = "0" + str;
    	return str;
    }
}

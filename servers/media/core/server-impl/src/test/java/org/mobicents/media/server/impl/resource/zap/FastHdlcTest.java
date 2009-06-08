package org.mobicents.media.server.impl.resource.zap;

import org.mobicents.media.server.impl.resource.zap.HdlcFrame;
import org.mobicents.media.server.impl.resource.zap.FastHDLC;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic test to see if the algoritmh can recognize unaligned bit frames and decode the bit stuffing properly.
 * 
 * @author Vladimir Ralev
 *
 */
public class FastHdlcTest {
    public FastHdlcTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Test
    public void fastHdlcTestUnalignedFrameRecognition() throws Exception {

        String[] data = "bf 7d f7 d8 00 0e 46 77 ef be fb 00 01 c8 ce fd f7 df 60 00 39 19 df be fb ec 00 07 23 3b f7 df 7d 80 00 e4 67 7e fb ef b0 00 1c 8c ef 7d f6".split(" ");
        String[] results = "ff ff 01 00 27 e6".split(" "); // This is the sample frame repeated in the above sequence with different alignments
        
        FastHDLC hdlc = new FastHDLC();
        HdlcFrame h = new HdlcFrame();
        hdlc.fasthdlc_precalc();
        hdlc.fasthdlc_init(h);
        int b = -1;
        int i = 0;
        int resultIndex = 0;

        int frameLen = 0;
        int completed = 0;
        String s = "";
        while ( i < data.length) {

            while (h.bits <= 24 && i < data.length) {
            	if(data[i].length()<=0) continue;
                b = Integer.parseInt(data[i++],16);
                hdlc.fasthdlc_rx_load_nocheck(h, b);
            }

            int res = hdlc.fasthdlc_rx_run(h);
            
            
            if(res == FastHDLC.RETURN_COMPLETE_FLAG) {
            	// A frame is completed
            	completed++;
            	continue;
            }
            
            // We don't expect bad frames here
            assertFalse(res == FastHDLC.RETURN_DISCARD_FLAG);
            assertFalse(res == FastHDLC.RETURN_EMPTY_FLAG);
            assertFalse(frameLen>279);

//            System.out.println("-- "
//            		+ alignNumber(Integer.toHexString(res), 2) +
//            		" " 
//            		+ alignNumber(Integer.toBinaryString(res), 8));
            frameLen++;
            
            // Check if the recognized sequence matches the frame we expected
            // (it is repeated so we expect it go in cycles 5 times and a bit more)
            assertEquals(results[resultIndex%results.length],
            		alignNumber(Integer.toHexString(res), 2));

            resultIndex++;

        }
        assertEquals(completed, 5);
    }

    // We just add some zeros so it's easy to see binary and hex numbers from the console
    private static String alignNumber(String str, int num) {
    	while(str.length()<num) str = "0" + str;
    	return str;
    }
}

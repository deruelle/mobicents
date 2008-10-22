/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.server.impl.jmx.rtp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.sdp.RTPAudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class FormatDescription implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 220438899785296125L;

	public HashMap<Integer, Format> parse(String description) {
        HashMap<Integer, Format> map = new HashMap<Integer, Format>();
        String tokens[] = description.split(";");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].length() == 0) continue;
            String params[] = tokens[i].split("=");
            
            //parse payload 
            String payloadStr = params[0].trim(); 
            int payload = Integer.parseInt(payloadStr);
            
            String fmtParams[] = params[1].split(",");
            
            String encodingName = fmtParams[0].trim();
            
            if (fmtParams.length == 1) {
                Format fmt = new RTPAudioFormat(payload, encodingName);
                map.put(payload, fmt);

                continue;
            }
            
            int sampleRate = 8000;
            if (fmtParams.length > 1) {
                sampleRate = Integer.parseInt(fmtParams[1].trim());
            }
            
            int sampleSize = 8;
            if (fmtParams.length > 2) {
                sampleSize = Integer.parseInt(fmtParams[2].trim());
            }
            
            int channels = 1;
            if (fmtParams.length > 3) {
                String p = fmtParams[3].trim();
                if (p.equals("Mono")) {
                    channels = 1;
                } else if (p.equals("Stereo")) {
                    channels = 2;
                } else channels = Integer.parseInt(fmtParams[3].trim());
            }
            
            Format fmt = new RTPAudioFormat(payload, encodingName, sampleRate, sampleSize, channels);
            map.put(payload, fmt);
        }
        return map;
    }
    
    public String getDescription(HashMap<Integer, Format> formats) {
        String s ="";
        Set<Integer> payloads = formats.keySet();
        for (Integer payload : payloads) {
            s += payload + "=";
                    
            Format fmt = (Format) formats.get(payload);
            s += fmt.toString();
            
            s += ";";
        }
        return s;
    }
    
    public static void main(String args[]) throws Exception {
        String desc="8 = ALAW, 8000, 8, 1;\n" +
            "0 = ULAW, 8000, 8, 1;";
        FormatDescription fd = new FormatDescription();
        HashMap<Integer, Format> fmts = fd.parse(desc);
        System.out.println(fmts);
        
        System.out.println(fd.getDescription(fmts));
        
        //RTPAudioFormat rTPAudioFormat = fmts.get(0);
    }
}

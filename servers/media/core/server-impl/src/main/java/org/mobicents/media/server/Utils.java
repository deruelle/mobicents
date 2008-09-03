/*
 * Utils.java
 *
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
package org.mobicents.media.server;

import org.mobicents.media.format.AudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class Utils {

    /** Creates a new instance of Utils */
    public Utils() {
    }

    /**
     * Creates audio format object from given format description.
     *
     * @param formatDesc the description of the format. Format description is 
     * as follows: codec, sampleRate Hz, sampleSize-bits, channels.
     * example: G729, 8000.0 Hz, 8-bits, Mono
     */
    public static AudioFormat parseFormat(String formatDesc) {
        String tokens[] = formatDesc.split(",");

        if (tokens.length != 4) {
            throw new IllegalArgumentException("Invalid format definition: " + formatDesc);
        }

        String encoding = tokens[0];

        String srDesc = tokens[1].substring(0, tokens[1].indexOf("Hz"));
        double sampleRate = Double.parseDouble(srDesc.trim());

        String szDesc = tokens[2].substring(0, tokens[2].indexOf("-bits"));
        int sampleSize = Integer.parseInt(szDesc.trim());

        int channels = 1;
        if (tokens[3].trim().equals("Mono")) {
            channels = 1;
        } else if (tokens[3].trim().equals("Stereo")) {
            channels = 2;
        } else {
            throw new IllegalArgumentException("Invalid format description: " + tokens[3]);
        }

        return new AudioFormat(encoding, sampleRate, sampleSize, channels);
    }

    public static String doMessage(Throwable t) {
        StringBuffer sb = new StringBuffer();
        int tick = 0;
        Throwable e = t;
        do {
            StackTraceElement[] trace = e.getStackTrace();
            if (tick++ == 0) {
                sb.append(e.getClass().getCanonicalName() + ":" + e.getLocalizedMessage() + "\n");
            } else {
                sb.append("Caused by: " + e.getClass().getCanonicalName() + ":" + e.getLocalizedMessage() + "\n");
            }
            for (StackTraceElement ste : trace) {
                sb.append("\t" + ste + "\n");
            }
            e = e.getCause();
        } while (e != null);

        return sb.toString();

    }
}

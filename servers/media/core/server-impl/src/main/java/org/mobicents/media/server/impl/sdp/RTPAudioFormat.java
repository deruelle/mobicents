/*
 * RTPAudioFormat.java
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
package org.mobicents.media.server.impl.sdp;

import org.mobicents.media.format.AudioFormat;

/**
 *
 * @author Oleg Kulikov
 */
public class RTPAudioFormat extends AudioFormat {

    private int payload;

    /** Creates a new instance of RTPAudioFormat */
    public RTPAudioFormat(int payload, String encodingName) {
        super(encodingName);
        this.payload = payload;
    }

    public RTPAudioFormat(int payload, String encodingName, double sampleRate,
            int bits, int chans) {
        super(encodingName, sampleRate, bits, chans);
        this.payload = payload;
    }

    public int getPayload() {
        return payload;
    }

    private static int getBits(String enc) {
        if (enc.equals("pcm")) {
            return 8;
        } else if (enc.equals("gsm")) {
            return 13;
        } else if (enc.equals("dvi")) {
            return 4;
        } else {
            return 8;
        }
    }

    public static RTPAudioFormat parseFormat(String rtpmap) {
        String tokens[] = rtpmap.toLowerCase().split(" ");

        //split params
        int p = Integer.parseInt(tokens[0]);
        tokens = tokens[1].split("/");

        String encodingName = tokens[0];
        double clockRate = Double.parseDouble(tokens[1]);

        int chans = 1;
        if (tokens.length == 3) {
            chans = Integer.parseInt(tokens[2]);
        }


        if (encodingName.equals("pcmu")) {
            return new RTPAudioFormat(p, AudioFormat.ULAW, clockRate, 8, chans);
        } else if (encodingName.equals("pcma")) {
            return new RTPAudioFormat(p, AudioFormat.ALAW, clockRate, 8, chans);
        } else if (encodingName.equals("telephone-event")) {
            return new DtmfFormat(p, "telephone-event");
        } else {
            return new RTPAudioFormat(p, encodingName, clockRate, getBits(encodingName), chans);
        }
    }

    @Override
    public String toString() {
        String encName = this.getEncoding().toLowerCase();
        StringBuffer buff = new StringBuffer();
        buff.append(payload);
        buff.append(" ");

        if (encName.equals("alaw")) {
            buff.append("pcma");
        } else if (encName.equals("ulaw")) {
            buff.append("pcmu");
        } else {
            buff.append(encName);
        }
        buff.append("/");

        double sr = getSampleRate();
        if ((sr - (int) sr) < 1E-6) {
            buff.append((int) sr);
        } else {
            buff.append(sr);
        }

        if (getChannels() > 1) {
            buff.append("/" + getChannels());
        }

        return buff.toString();

    }
}

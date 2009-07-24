/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Oleg Kulikov
 */
public class Utils {
    private static String reason;
    
    public static String getReason() {
        return reason;
    }
    
    private static double[] diff(double[] f) {
        double[] diff = new double[f.length];
        for (int i = 0; i < f.length - 1; i++) {
            diff[i] = f[i + 1] - f[i];
        }
        return diff;
    }

    private static double findMax(double[] f) {
        double max = f[0];
        for (int i = 1; i < f.length; i++) {
            max = Math.max(max, f[i]);
        }
        return max;
    }

    private static int[] findExtremums(double[] f) {
        List<Integer> ext = new ArrayList();
        for (int i = 0; i < f.length - 1; i++) {
            if (f[i] > 0 && f[i + 1] < 0) {
                ext.add(i);
            }
        }

        int[] res = new int[ext.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = ext.get(i);
        }
        return res;
    }
    
    public static int[] getFreq(double[] s) {
        double max = findMax(s);

        int len = s.length / 2;
        double ss[] = new double[len];

        for (int i = 0; i < len; i++) {
            ss[i] = s[i] / max;
            ss[i] = ss[i] < 0.7 ? 0 : ss[i];
//            System.out.println(i + " " + ss[i]);
        }

        double[] diff = diff(ss);
        int[] ext = findExtremums(diff);
        
        return ext;
    }
    
    public static synchronized boolean checkFreq(int[] ext, int[] F, int error) {
        if (ext.length < F.length) {
            reason = "Expected " + F.length + " peaks but found " + ext.length;
            return false;
        }
        for (int i = 0; i < F.length; i++) {
            if (Math.abs(ext[i] - F[i]) > error) {
                reason = "Expected " + F[i] + " but found " + ext[i];
                return false;
            }
        }

        return true;
    }

    public static boolean checkFormats(Format[] formats, Format[] supported) {
        for (int i = 0; i < supported.length; i++) {
            boolean found = false;
            for (int j = 0; j < formats.length; j++) {
                if (supported[i].equals(formats[j])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
}

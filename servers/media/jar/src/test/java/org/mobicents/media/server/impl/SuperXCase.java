package org.mobicents.media.server.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;



public abstract class SuperXCase extends TestCase {

    private boolean isFailed = false;
    private String reason;
    private String mcHOME = System.getenv("MOBICENTS_HOME");
    protected String dumpDir = (mcHOME + File.separator + "servers" + File.separator + "media" + File.separator + "jar" + File.separator + "target" + File.separator + "surefire-reports").replace("core", "");

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {
    }

    public String getReason() {
        return reason;
    }

    private double[] diff(double[] f) {
        double[] diff = new double[f.length];
        for (int i = 0; i < f.length - 1; i++) {
            diff[i] = f[i + 1] - f[i];
        }
        return diff;
    }

    private double findMax(double[] f) {
        double max = f[0];
        for (int i = 1; i < f.length; i++) {
            max = Math.max(max, f[i]);
        }
        return max;
    }

    private int[] findExtremums(double[] f) {
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

    public boolean checkFreq(double[] s, int[] F, int error) {
        double max = findMax(s);

        int len = s.length / 2;
        double ss[] = new double[len];

        for (int i = 0; i < len; i++) {
            ss[i] = s[i] / max;
            ss[i] = ss[i] < 0.3 ? 0 : ss[i];
//            System.out.println(i + " " + ss[i]);
        }

        double[] diff = diff(ss);
        int[] ext = findExtremums(diff);

//        for (int i = 0; i < ext.length; i++) {
//            System.out.println("ext=" + ext[i]);
//        }
//        System.out.println("--------------");

        for (int i = 0; i < F.length; i++) {
            if (Math.abs(ext[i] - F[i]) > error) {
                reason = "Expected " + F[i] + " but found " + ext[i];
                return false;
            }
        }

        return true;
    }

    public boolean doTest(int duration) {
        synchronized (this) {
            try {
                wait(duration * 1000);
                
            } catch (InterruptedException e) {
            	
                reason = "Test interrupted\n"+doMessage(e);
                return false;
            }
        }
        return !isFailed;
    }

    
    public void doFail(String reason) {
        synchronized (this) {
        	
            isFailed = true;
            //if (reason != null) {
                this.reason = reason+", "+this.reason;
            //}
            notifyAll();
        }
    }
    
    protected boolean getIsFailed()
    {
    	return isFailed;
    }

    public static String doMessage(Throwable t) {
		StringBuffer sb = new StringBuffer();
		int tick = 0;
		Throwable e = t;
		do {
			StackTraceElement[] trace = e.getStackTrace();
			if (tick++ == 0)
				sb.append(e.getClass().getCanonicalName() + ":"
						+ e.getLocalizedMessage() + "\n");
			else
				sb.append("Caused by: " + e.getClass().getCanonicalName() + ":"
						+ e.getLocalizedMessage() + "\n");

			for (StackTraceElement ste : trace)
				sb.append("\t" + ste + "\n");

			e = e.getCause();
		} while (e != null);

		return sb.toString();

	}
    
}

package org.mobicents.media.server;

public class DebugUtils {

	public static String debugArray(byte[] a) {
		String dbg = "";
		for(int q=0; q<a.length; q++) {
			dbg += a[q] + " ";
		}
		return dbg;
	}
	public static String debugArray(short[] a) {
		String dbg = "";
		for(int q=0; q<a.length; q++) {
			dbg += a[q] + " ";
		}
		return dbg;
	}
	public static String debugArray(int[] a) {
		String dbg = "";
		for(int q=0; q<a.length; q++) {
			dbg += a[q] + " ";
		}
		return dbg;
	}
}

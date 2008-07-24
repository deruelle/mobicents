package org.mobicents.qa.performance.jainsip.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

public class SimpleCallSetupTestFileUtils {

    public static void extract(String filename, String destFolder) {
	String home = SimpleCallSetupTestFileUtils.class.getProtectionDomain().getCodeSource().getLocation().toString();
	home = home.replaceAll("%20", " ").replaceFirst("file:", "");
	String destFilename = null;

	InputStream in;
	OutputStream out;

	try {
	    try {
		JarFile jar = new JarFile(home);
		ZipEntry entry = jar.getEntry(filename);
		destFilename = entry.getName();
		in = new BufferedInputStream(jar.getInputStream(entry));
	    } catch (ZipException ze) {

		// see if it from an exploded jar
		try {
		    File inputFile = new File(home + filename);
		    destFilename = inputFile.getName();
		    in = new BufferedInputStream(new FileInputStream(inputFile));
		} catch (Exception e) {
		    e.printStackTrace();
		    return;
		}
	    }

	    File destFile = new File(destFolder, destFilename);
	    destFile.deleteOnExit();
	    out = new BufferedOutputStream(new FileOutputStream(destFile));

	    byte[] buffer = new byte[2048];
	    for (;;) {
		int nBytes = in.read(buffer);
		if (nBytes <= 0)
		    break;
		out.write(buffer, 0, nBytes);
	    }
	    out.flush();
	    out.close();
	    in.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void writeSippStartFile(String filename, String destFolder, String configFile, String interfaceString, String portString, String controlPort) {
	try {
	    File startFile = new File(destFolder, filename);
	    startFile.deleteOnExit();
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(startFile));

	    int portValue = Integer.parseInt(portString) - 1;

	    StringBuilder msg = new StringBuilder();
	    msg.append("./sipp -sf ").append(configFile).append(" -i ").append(interfaceString).append(" -p ").append(portValue).append(" -r 0 -l 1000000 ");
	    msg.append(interfaceString).append(":").append(portString).append(" -cp ").append(controlPort).append(" -fd 1 -trace_stat ");

	    byte[] buffer = msg.toString().getBytes();
	    out.write(buffer, 0, buffer.length);
	    out.flush();
	    out.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void executize(String dir) {

	File myDir = new File(dir);
	if (!myDir.isDirectory()) {
	    myDir = myDir.getParentFile();
	}

	for (String file : myDir.list()) {
	    if (!file.endsWith(".sh")) {
		continue;
	    }
	    
	    try {
		ProcessBuilder pb = new ProcessBuilder("chmod", "755", file);
		pb.directory(myDir);
		pb.redirectErrorStream(true);

		Process p = pb.start();
		System.out.print("executizing " + file + " ... ");

		final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		try {
		    while ((line = reader.readLine()) != null) {
			System.out.println(line);
		    }
		} catch (Exception e) {
		    e.printStackTrace(System.err);
		}

		p.waitFor();
		System.out.println("done (" + p.exitValue() + ")");
	    } catch (Exception e) {
		System.out.println("failed");
	    }
	}
	System.out.println();
    }
}

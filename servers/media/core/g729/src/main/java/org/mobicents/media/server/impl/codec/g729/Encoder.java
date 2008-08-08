package org.mobicents.media.server.impl.codec.g729;

import java.io.*;

public class Encoder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		InputStream f_speech = null; /* Speech data */
		OutputStream f_serial = null; /* Serial bit stream */
		OutputStream f_serial_b = null;

		float[] new_speech; /* Pointer to new speech data */

		int prm[] = new int[LD8KConstants.PRM_SIZE]; /* Transmitted parameters */
		short serial[] = new short[LD8KConstants.SERIAL_SIZE]; /*
																 * Output bit
																 * stream buffer
																 */
		short sp16[] = new short[LD8KConstants.L_FRAME]; /*
														 * Buffer to read 16
														 * bits speech
														 */

		int i;
		int frame;

		/*
		 * ----------------------------------------------------------------------
		 * - Open speech file and result file (output serial bit stream)
		 * ----------
		 * -------------------------------------------------------------
		 */

		if (args.length != 2) {
			throw new RuntimeException("Must use 3 args.");
		}

		try {
			f_speech = new FileInputStream(args[0]);
			f_serial = new FileOutputStream(args[1] + ".itu");
			f_serial_b = new FileOutputStream(args[1]);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}

		/*
		 * ------------------------------------------------- Initialization of
		 * the coder.-------------------------------------------------
		 */
		PreProc preProc = new PreProc();
		CodLD8K coder = new CodLD8K();
		preProc.init_pre_process();
		coder.init_coder_ld8k(); /* Initialize the coder */

		/*
		 * ----------------------------------------------------------------------
		 * --- Loop for every analysis/transmission frame. -New L_FRAME data are
		 * read. (L_FRAME = number of speech data per frame) -Conversion of the
		 * speech data from 16 bit integer to real -Call cod_ld8k to encode the
		 * speech. -The compressed serial output stream is written to a file.
		 * -The synthesis speech is written to a file
		 * ----------------------------
		 * ---------------------------------------------
		 */

		frame = 0;
		byte[] tmp = new byte[LD8KConstants.L_FRAME * 2];
		try {
			while (f_speech.read(tmp) == LD8KConstants.L_FRAME * 2) {
				sp16 = Util.byteArrayToShortArray(tmp);
				frame++;
				System.out.println(" Frame: r" + frame);

				new_speech = new float[sp16.length];
				for (i = 0; i < LD8KConstants.L_FRAME; i++)
					new_speech[i] = (float) sp16[i];

				preProc.pre_process(new_speech, LD8KConstants.L_FRAME);

				coder.loadSpeech(new_speech);
				coder.coder_ld8k(prm, 0);

				byte[] a = new byte[10];
				Bits.prm2bits_ld8k_b(prm, a);
				Bits.prm2bits_ld8k(prm, serial);
				if (frame < 3) {
					System.out.println("A: " + debugArray(sp16));
					System.out.println("PRM: " + debugArray(prm));
				}

				f_serial_b.write(a);
				f_serial.write(Util.shortArrayToByteArray(serial));

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				f_serial.close();
				f_speech.close();
				f_serial_b.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	} /* end of main() */

	public static String debugArray(byte[] a) {
		String dbg = "";
		for (int q = 0; q < a.length; q++) {
			dbg += a[q] + " ";
		}
		return dbg;
	}

	public static String debugArray(short[] a) {
		String dbg = "";
		for (int q = 0; q < a.length; q++) {
			dbg += a[q] + " ";
		}
		return dbg;
	}

	public static String debugArray(int[] a) {
		String dbg = "";
		for (int q = 0; q < a.length; q++) {
			dbg += a[q] + " ";
		}
		return dbg;
	}

}

package org.mobicents.media.server.impl.codec.g729;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class Decoder {

	/*-----------------------------------------------------------------*
	 *            Main decoder routine                                 *
	 *-----------------------------------------------------------------*/

	public static void main( String[] args)
	{
	   float  synth_buf[] = new float[LD8KConstants.L_FRAME+LD8KConstants.M];            /* Synthesis                  */
	   int    synth;
	   int    parm[] = new int[LD8KConstants.PRM_SIZE+1];                /* Synthesis parameters + BFI */
	   short  serial[] = new short[LD8KConstants.SERIAL_SIZE];             /* Serial stream              */
	   float  Az_dec[] = new float[2*LD8KConstants.MP1];
	   int ptr_Az;          /* Decoded Az for post-filter */
	   IntegerPointer    t0_first = new IntegerPointer();
	   float  pst_out[] = new float[LD8KConstants.L_FRAME];                /* postfilter output          */

	   int voicing;                    /* voicing for previous subframe */
	   IntegerPointer sf_voic = new IntegerPointer(0);                    /* voicing for subframe */

	   int   frame;

	   InputStream f_serial = null;                     /* Speech data        */
	   OutputStream f_syn = null;                     /* Serial bit stream  */
	   int   i;

	   DecLD8K decLD = new DecLD8K();
	   PostFil postFil = new PostFil();
	   PostPro postPro = new PostPro();

	   /* Passed arguments */

	   if (args.length != 2)
	   {
	        throw new RuntimeException("I need 2 args");
	   }

	   /* Open file for synthesis and packed serial stream */
	   try {
		f_serial = new FileInputStream(args[0]);
		f_syn = new FileOutputStream(args[1]);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	   
	

	/*-----------------------------------------------------------------*
	 *           Initialization of decoder                             *
	 *-----------------------------------------------------------------*/

	  for (i=0; i<LD8KConstants.M; i++) synth_buf[i] = (float)0.0;
	  synth = 0 + LD8KConstants.M;

	  decLD.init_decod_ld8k();
	  postFil.init_post_filter();
	  postPro.init_post_process();
	  voicing = 60;

	/*-----------------------------------------------------------------*
	 *            Loop for each "L_FRAME" speech data                  *
	 *-----------------------------------------------------------------*/

	   frame =0;
	   byte[] tmp = new byte[LD8KConstants.SERIAL_SIZE * 2];
	   try {
	   while( f_serial.read(tmp, 0, LD8KConstants.SERIAL_SIZE*2) == LD8KConstants.SERIAL_SIZE*2)
	   {
		  serial = Util.byteArrayToShortArray(tmp);
	      frame++;
	      

	      Bits.bits2prm_ld8k( serial,2, parm,1);

	      /* the hardware detects frame erasures by checking if all bits
	         are set to zero
	      */
	      parm[0] = 0;           /* No frame erasure */
	      for (i=2; i < LD8KConstants.SERIAL_SIZE; i++)
	        if (serial[i] == 0 ) parm[0] = 1; /* frame erased     */

	      /* check parity and put 1 in parm[4] if parity error */

	      parm[4] = PParity.check_parity_pitch(parm[3], parm[4] );

	      decLD.decod_ld8k(parm, 0, voicing, synth_buf,synth, Az_dec, t0_first);  /* Decoder */

	      /* Post-filter and decision on voicing parameter */
	      voicing = 0;
	      ptr_Az = 0;//Az_dec;
	      for(i=0; i<LD8KConstants.L_FRAME; i+=LD8KConstants.L_SUBFR) {
	        postFil.post(t0_first.value, synth_buf, synth + i, Az_dec, ptr_Az, pst_out, i, sf_voic);
	        if (sf_voic.value != 0) { voicing = sf_voic.value;}
	        ptr_Az += LD8KConstants.MP1;
	      }
	      Util.copy(synth_buf,LD8KConstants.L_FRAME, synth_buf, 0, LD8KConstants.M);

	      postPro.post_process(pst_out, LD8KConstants.L_FRAME);

	      Util.fwrite16(pst_out, LD8KConstants.L_FRAME, f_syn);
	   }
	   } catch (Exception t) {
		   throw new RuntimeException(t);
	   } finally {
		   try {
		   f_syn.close();
		   f_serial.close();
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }

	}
}

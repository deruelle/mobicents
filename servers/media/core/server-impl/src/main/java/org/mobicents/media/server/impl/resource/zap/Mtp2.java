/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.media.server.impl.resource.zap;

import org.apache.log4j.Logger;


/**
 *
 * @author kulikov
 */
public class Mtp2 {

    protected final static int MTP_MAX_PCK_SIZE = 300;
    private final static int ZAP_BUF_SIZE = 16;
    
    public final static int STATE_DOWN = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_INSERVICE = 2;
    
    private String channelName;
    private int state;

  /* Counts of raw bytes read and written, used to timestamp raw dumps.
     Make them double to avoid overflow for quite a while. */
 private double readcount, writecount;

  /* Sequence numbers and indicator bits to be sent in signalling units. */
  private int send_fib;
  private int send_bsn, send_bib;

  /* Send initial SLTM? */
  private int send_sltm;

  /* Timeslot for signalling channel */
  private int schannel;
  private int slinkno;
    
  private int sls;
  private int subservice;
  

  /* Receive buffer. */
  private byte[] rx_buf = new byte[272 + 7];
  private int rx_len;
  private int rx_crc;

  /* Transmit buffer. */
  private byte[] tx_buffer = new byte[272 + 7 + 5];
  private int tx_len;
  private int tx_sofar;
  private int tx_do_crc;                /* Flag used to handle writing CRC bytes */
  private int tx_crc;

  /* Zaptel transmit buffer. */
  private byte[] zap_buf = new byte[ZAP_BUF_SIZE];
  int zap_buf_full;

  /* HDLC encoding and decoding state. */
  private HdlcFrame h_rx;
  private HdlcFrame h_tx;

  /* Last few raw bytes received, for debugging link errors. */
  private byte[] backbuf = new byte[36];
  int backbuf_idx;

  /* Retransmit buffer. */
  private DataBuffer[] retrans_buf = new DataBuffer[128];
  /* Retransmit counter; if this is != -1, it means that retransmission is
     taking place, with this being the next sequence number to retransmit. */
  private int retrans_seq;
  /* Last sequence number ACK'ed by peer. */
  private int retrans_last_acked;
  /* Last sequence number sent to peer. */
  private int retrans_last_sent;
  /* Counter for signal unit/alignment error rate monitors (Q.703 (10)). */
  private int error_rate_mon;
  /* Counters matching the D and N values of the error rate monitors. */
  private int emon_ncount, emon_dcount;
  /* Counter for bad BSN */
  private int bsn_errors;
  /* Q.703 timer T1 "alignment ready" (waiting for peer to end initial
     alignment after we are done). */
  private int mtp2_t1;
  /* Q.703 timer T2 "not aligned" (waiting to receive O, E, or N after sending
     O). */
  private int mtp2_t2;
  /* Q.703 timer T3 "aligned" (waiting to receive E or N after sending E or
     N). */
  private int mtp2_t3;
  /* Q.703 timer T4 "proving period" - proving time before ending own initial
     alignment. */
  private int mtp2_t4;
  /* Q.703 timer T7 "excessive delay of acknowledgement" . */
  private int mtp2_t7;

  /* Set true when SLTA is received and User Parts (ie. ISUP) is notified that
     the link is now in service. */
  private int level4_up;

  /* Hm, the rest is actually MTP3 state. Move to other structure, or
     rename this structure. */
  private int sltm_t1;                  /* Timer T1 for SLTM (Q.707) */
  private int sltm_t2;                  /* Timer T2 for SLTM (Q.707) */
  private int sltm_tries;               /* For SLTM retry (Q.707 (2.2)) */

  /* Q.704 timer T17, "initial alignment restart delay". */
  private int mtp3_t17;
    
    private final static Logger logger = Logger.getLogger(Mtp2.class);
    
    public Mtp2(String channelName) {
        this.channelName = channelName;
    }

    private int MTP_NEXT_SEQ(int bsn) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void mtp2_good_frame(byte[] buf, int len) {
        int fsn, fib, bsn, bib;
        int li;

        /* Count this frame into the error rate monitor counters. */
        //mtp2_error_mon_count_frame(m);



  
        
        if (state == STATE_DOWN) {
            return;
        }


        bsn = buf[0] & 0x7f;
        bib = buf[0] >> 7;
        fsn = buf[1] & 0x7f;
        fib = buf[1] >> 7;

        li = buf[2] & 0x3f;

        if (li + 3 > len) {
            logger.warn("Got unreasonable length indicator " + li + " (len=" + len + ") on link " + channelName);
            return;
        }

        if (li == 1 || li == 2) {
            /* Link status signal unit. */
            mtp2_process_lssu(buf, fsn, fib);
            return;
        }

        /* Process the BSN of the signal unit.
        According to Q.703 (5), only FISU and MSU should have FSN and BSN
        processing done. */
        if (state != STATE_INSERVICE) {
            if (state == STATE_READY) {
                t1_stop();
                t7_stop();
                
                send_fib = bib;
                send_bsn = fsn;
                send_bib = fib;
                //      retrans_last_acked = fsn;xxx
                //      retrans_last_sent = fsn;xxx
                retrans_last_acked = bsn;//xxx
                error_rate_mon = 0;
                emon_dcount = 0;
                emon_ncount = 0;
                level4_up = 0;

                /* Send TRA (traffic restart allowed) immediately, since we have no
                routing capabilities that could be prohibited/restricted. */
            } else {
                return;
            }
        } else if (state == STATE_READY) {
            t1_stop();
            t7_stop();
        }

        /* ToDo: Check for FIB flipover when we haven't requested retransmission,
        and fault the frame if so. See last part of Q.703 (5.3.2). */

        /* Process the BSN of the received frame. */
        if ((retrans_last_acked <= retrans_last_sent &&
                (bsn < retrans_last_acked || bsn > retrans_last_sent)) ||
                (retrans_last_acked > retrans_last_sent &&
                (bsn < retrans_last_acked && bsn > retrans_last_sent))) {
            /* They asked for a retransmission of a sequence number not available. */
            logger.warn("Received illegal BSN=" + bsn +
                    " (retrans=" + retrans_last_acked +"," + retrans_last_sent +
                    ") on link "+ channelName +", len=" + len);
            /* ToDo: Fault the link if this happens again within the next
            two SUs, see second last paragraph of Q.703 (5.3.1). */
            if (bsn_errors++ > 2) {
                bsn_errors = 0;
                mtp3_link_fail(1);
            }
            return;
        }
        bsn_errors = 0;

        /* Reset timer T7 if new acknowledgement received (Q.703 (5.3.1) last
        paragraph). */
        if (retrans_last_acked != bsn) {
            t7_stop();
            retrans_last_acked = bsn;
            if (retrans_last_acked != retrans_last_sent) {
                mtp2_t7_start();
            }
        }

        if (bib != send_fib) {
            /* Received negative acknowledge, start re-transmission. */
            send_fib = bib;
            if (bsn == retrans_last_sent) {
                /* Nothing to re-transmit. */
                retrans_seq = -1;
            } else {
                retrans_seq = MTP_NEXT_SEQ(bsn);
            }
        }

        /* Process the signal unit content. */
        if (li == 0) {
            /* Fill-in signal unit. */

            /* Process the FSN of the received frame. */
            if (fsn != send_bsn) {
                /* This indicates the loss of a message. */
                if (fib == send_bib) {
                    /* Send a negative acknowledgement, to request retransmission. */
                    if (send_bib == 1) {
                        send_bib = 0;
                    } else {
                        send_bib= 0;
                    }
//                    send_bib = !send_bib;
                }
            }
        } else {
            /* Message signal unit. */
            /* Process the FSN of the received frame. */
            if (fsn == send_bsn) {
                /* Q.703 (5.2.2.c.i): Redundant retransmission. */
                return;
            } else if (fsn == MTP_NEXT_SEQ(send_bsn)) {
                /* Q.703 (5.2.2.c.ii). */
                if (fib == send_bib) {
                    /* Successful frame reception. Do explicit acknowledge on next frame. */
                    send_bsn = fsn;
                } else {
                    /* Drop frame waiting for retransmissions to arrive. */
                    return;
                }
            } else {
                /* Q.703 (5.2.2.c.iii). Frame lost before this frame, discart it
                (will be retransmitted in-order later). */
                if (fib == send_bib) {
                    /* Send a negative acknowledgement, to request retransmission. */
                    if (send_bib == 1) {
                        send_bib = 0;
                    } else {
                        send_bib= 0;
                    }
//                    send_bib = !send_bib;
                }
                return;
            }

            /* Length indicator (li) is number of bytes in MSU after LI, so the valid
            bytes are buf[0] through buf[(li + 3) - 1]. */
            if (li < 5) {
                logger.warn("Got short MSU (no label), li=" + li +" on link " + channelName);
                return;
            }
            process_msu(buf, len);
        }
    }

    private void mtp2_process_lssu(byte[] buf, int fsn, int fib) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void mtp2_t7_start() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void mtp3_link_fail(int i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void process_msu(byte[] buf, int len) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private void t1_stop() {
        
    }
    private void t7_stop() {
        
    }
}
class DataBuffer {
    private byte[] data = new byte[Mtp2.MTP_MAX_PCK_SIZE];
    private int len;        
    
    public DataBuffer(byte[] data, int len) {
        this.data = data;
        this.len = len;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public int len() {
        return len;
    }
}

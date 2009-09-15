package org.mobicents.media.server.ctrl.rtsp;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.sdp.Attribute;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;

import org.apache.log4j.Logger;

public class SdpUtils {

	private static Logger logger = Logger.getLogger(SdpUtils.class);
	private static SdpFactory sdpFactory = SdpFactory.getInstance();

	// TODO : Do we care for SDP Port
	// TODO : The IP is same as where RTSP stack is listening. In multi NIC machine this may not be true
	// TODO : We didn't even consider Formats like Speex, GSM etc
	// TODO : Should we cache the SDP for some configurable duration once created ?
	// TODO : Where is Video?
	public String getSdp(File file, String ipAddress, int port, String controlAu) throws Exception {
		AudioFileFormat auFileFmt = AudioSystem.getAudioFileFormat(file);
		AudioFormat auFmt = auFileFmt.getFormat();
		int payload = -1;
		String rtpMap = null;

		Encoding encoding = auFmt.getEncoding();
		if (encoding == Encoding.ALAW) {
			payload = 8;
			rtpMap = "8 pcma/8000";
		} else if (encoding == Encoding.ULAW) {
			payload = 0;
			rtpMap = "0 pcmu/8000";
		} else if (encoding == Encoding.PCM_SIGNED) {
			int sampleSize = auFmt.getSampleSizeInBits();
			if (sampleSize != 16) {
				throw new Exception("Found unsupported Format " + auFileFmt);
			}
			int sampleRate = (int) auFmt.getSampleRate();
			if (sampleRate == 44100) {
				int channels = auFmt.getChannels();
				if (channels == 1) {
					payload = 11;
					rtpMap = "11 l16/44100/1";
				} else {
					payload = 10;
					rtpMap = "10 l16/44100/2";
				}
			} else {
				// return null;
				throw new Exception("Found unsupported Format " + auFileFmt);
			}
		}

		SessionDescription sdp = null;
		String userName = "MobicentsMediaServer";

		long sessionID = System.currentTimeMillis() & 0xffffff;
		long sessionVersion = sessionID;

		String networkType = javax.sdp.Connection.IN;
		String addressType = javax.sdp.Connection.IP4;

		try {
			sdp = sdpFactory.createSessionDescription();
			sdp.setVersion(sdpFactory.createVersion(0));
			sdp.setOrigin(sdpFactory.createOrigin(userName, sessionID, sessionVersion, networkType, addressType,
					ipAddress));
			sdp.setSessionName(sdpFactory.createSessionName("session"));
			sdp.setConnection(sdpFactory.createConnection(networkType, addressType, ipAddress));

			int[] fmtList = new int[] { payload };
			Vector descriptions = new Vector();
			MediaDescription md = sdpFactory.createMediaDescription("audio", port, 1, "RTP/AVP", fmtList);
			Vector attributes = new Vector();
			Attribute a = sdpFactory.createAttribute("rtpmap", rtpMap);
			attributes.add(a);

			Attribute a1 = sdpFactory.createAttribute("control", controlAu);
			attributes.add(a1);

			md.setAttributes(attributes);
			descriptions.add(md);
			sdp.setMediaDescriptions(descriptions);

			return sdp.toString();

		} catch (SdpException e) {
			logger.error("Erro while creating SDP");
			throw e;
		}

	}

	public int getAudioPort(String sdp) throws Exception {
		int port = -1;
		SessionDescription sdpObj = sdpFactory.createSessionDescription(sdp);
		Vector mds = sdpObj.getMediaDescriptions(false);
		Iterator itr = mds.iterator();

		while (itr.hasNext()) {
			MediaDescription md = (MediaDescription) itr.next();
			Media m = md.getMedia();
			if (m.getMediaType().equals("audio")) {
				port = m.getMediaPort();
				break;
			}
		}

		return port;
	}
}

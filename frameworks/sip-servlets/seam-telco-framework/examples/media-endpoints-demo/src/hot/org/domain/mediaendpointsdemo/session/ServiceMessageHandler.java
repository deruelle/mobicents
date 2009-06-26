package org.domain.mediaendpointsdemo.session;

import java.io.IOException;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.mobicents.mscontrol.MsConnection;
import org.mobicents.mscontrol.MsConnectionEvent;
import org.mobicents.mscontrol.MsLinkEvent;
import org.mobicents.mscontrol.MsLinkMode;
import org.mobicents.servlet.sip.seam.entrypoint.media.MediaController;
import org.mobicents.servlet.sip.seam.media.framework.IVRHelper;
import org.mobicents.servlet.sip.seam.media.framework.MediaSessionStore;

@Name("serviceMessageHandler")
@Scope(ScopeType.STATELESS)
public class ServiceMessageHandler {
	
	@In MediaController mediaController;
	@In MediaSessionStore mediaSessionStore;
	@In IVRHelper ivrHelper;
	@In SipSession sipSession;
	
	/*
	 * Represents the current state of the call. I.e. - are we conneted to IVR
	 * or a conference/1 or conference/$. Basically we store here the last pressed
	 * DTMF button as it identifies the mode uniquely for this application.
	 */
	@In(required=false,scope=ScopeType.SESSION) 
	@Out(required=false,scope=ScopeType.SESSION) 
	String mode;
	
	@Logger 
	private static Log log;
	
	/* [0]
	 * We receive an incoming call. INVITE has been sent from some SIP client
	 * to our applications.
	 */
	@Observer("INVITE")
	public void doInvite(SipServletRequest request) throws Exception {
		/* [1]
		 *  Let's store the invite in the session. This is equivalent to
		 *  what we do with the "mode" variable, which is session-scoped,
		 *  but declared in the Seam-way.
		 */
		sipSession.setAttribute("inviteRequest", request);
		
		/* [2]
		 * Let's tell the caller that we received the INVITE and we are
		 * ringing now.
		 */
		request.createResponse(180).send();
		
		/* [3]
		 * Take the remote SDP sent by the caller.
		 */
		String sdp = new String((byte[]) request.getContent());
		
		/* [4]
		 * Now create a Media connection between the caller and any available
		 * PacketRelay endpoint in our media server. Note that we pass the SDP
		 * with the modify method. The remote SDP contains the RTP port where the caller
		 * want to receive the RTP stream. By the following line of code we are telling our 
		 * Media Server to create a local UDP socket and start preparing to stream
		 * to the remote socket using the parameters contained in this SDP.
		 * Other than the RTP port of the caller, the SDP contains information about
		 * the supported codecs and media capabilities. We usually dont care about
		 * it as it is responsibility of the media server to process the data. SDP is
		 * not very human-readable anyway.
		 */
		mediaController.createConnection("media/trunk/PacketRelay/$").modify("$", sdp);
	}
	
	/* [5]
	 * Once the RTP connection between fully negotiated from step [4], the Media
	 * Server notified us with this callback and the RTP is ready for streaming
	 * immediately after we send our SDP to the caller. So far we have built the
	 * following topology:
	 * 
	 * Caller <----MsConnection----> PacketRelay
	 */
	@Observer("connectionOpen")
	public void connectionOpenRequest(MsConnectionEvent event) throws Exception {
		/* [6]
		 * Let's get the MsConnection object that connects the caller to the
		 * PacketRelay Endpoint. We can take it from mediaSessionStore, where STF
		 * collects objects related to the Media Topology of the current call(session).
		 */
		MsConnection connection = mediaSessionStore.getMsConnection();

		/* [7]
		 * Let's take the local SDP, where the information about our own RTP port and
		 * supported codecs is contained. We must send this info to the caller so they
		 * will know where to begin streaming they media and what codecs they can use.
		 */
		String sdp = event.getConnection().getLocalDescriptor();
		
		/* [8]
		 * Recall the INVITE we stored in step [1]
		 */
		SipServletRequest inviteRequest = (SipServletRequest) 
			sipSession.getAttribute("inviteRequest");
		
		/* [9]
		 * Prepare the response we are about to send to the caller.
		 */
		SipServletResponse sipServletResponse = inviteRequest
				.createResponse(SipServletResponse.SC_OK);

		/* [10]
		 * Put our local SDP in the response and send the response.
		 */
		sipServletResponse.setContent(sdp, "application/sdp");
			sipServletResponse.send();
		
		/* [11]
		 * Set the initial mode of the application
		 */
	    mode = "1";
	    
	    /* [12]
	     * And connect the PacketRelay endpoint to an IVR endpoint. The IVR endpoint
	     * supports a variety of operations like playback, recording, DTMF. Basically,
	     * when this line is executed we have built the following topology:
	     * Caller <----MsConnection----> PacketRelay <----MsLink----> IVR
	     */
		mediaController.createLink(MsLinkMode.FULL_DUPLEX)
			.join("media/trunk/IVR/$",
					connection.getEndpoint().getLocalName());
	}
	
	/* [13]
	 * The Media Server notifies us that the link we created in step [12]
	 * was connected successfully. This method is also called from steps [20],
	 * [21], [22] where we perform a similar link connection operation. This
	 * method can handle all cases as long as the mode is set.
	 */
	@Observer("linkConnected")
	public void doLinkConnected(MsLinkEvent event) {
		if("1".equals(mode)) {
			/* [14]
			 * If we are in IVR mode just play some announcement and detect DTMF.
			 */
			ivrHelper.playAnnouncementWithDtmf(
					"http://mobicents.googlecode.com/svn/tags/servers/media/examples/" +
					"mobicents-media-server-examples-1.0.0.GA/mms-demo/web/src/main/" +
					"webapp/audio/welcome.wav");
		} else {
			/* [15]
			 * Otherwise just detect DTMF so we can keep reacting on DTMF events
			 */
			ivrHelper.detectDtmf();
		}
	}
	
	/* [16]
	 * This callback is called when the caller has pressed a button on her phone.
	 * Here, we switch between different endpoint to perform different functions.
	 * Initially, the caller is connected to IVR (through the PacketRelay), but
	 * we can connect the caller to a Conference endpoint for example as shown below.
	 */
	@Observer("DTMF")
	public void doDtmf(String digit) throws Exception{
		log.info("DTMF = " + digit + ", previous mode = " + mode);
		/* [17]
		 * Change the mode to the new digit pressed
		 */
		mode = digit;
		
		/* [18]
		 * Some cleanup to release the link that we don;t need anymore (as
		 * we create a new one). This operations is done automatically in
		 * newer versions of STF.
		 */
		mediaSessionStore.getMsLink().release();
		
		/* [19]
		 * Branch the logic depending on which button is pressed
		 */
		if("1".equals(digit)) {
			/* [20]
			 * If "1" is pressed build the following topology:
			 * Caller <----MsConnection----> PacketRelay <----MsLink----> IVR
			 */
			mediaController.createLink(MsLinkMode.FULL_DUPLEX)
			.join("media/trunk/IVR/$",
					mediaSessionStore.getMsConnection().getEndpoint().getLocalName());
		} else if("2".equals(digit)) {
			/* [21]
			 * If "1" is pressed build the following topology:
			 * Caller <----MsConnection----> PacketRelay <----MsLink----> Conference
			 */
			mediaController.createLink(MsLinkMode.FULL_DUPLEX)
			.join("media/trunk/Conference/$",
					mediaSessionStore.getMsConnection().getEndpoint().getLocalName());
		} else if("3".equals(digit)) {
			/* [22]
			 * If "1" is pressed build the following topology:
			 * Caller <----MsConnection----> PacketRelay <----MsLink----> Conference
			 * 
			 * The difference from the previous option is only that here we strictly
			 * connect to the Conference Endpoint 1, while in the previous option we
			 * connect to the first available (unused) Conference endpoint.
			 */
			mediaController.createLink(MsLinkMode.FULL_DUPLEX)
			.join("media/trunk/Conference/1",
					mediaSessionStore.getMsConnection().getEndpoint().getLocalName());
		} else if("0".equals(digit)) {
			/* [23]
			 * Here we just end the call if the user presses "0"
			 */
			sipSession.createRequest("BYE").send();
		}
	}
	
	@Observer("BYE")
	public void doBye(SipServletRequest request) throws Exception {
		request.createResponse(200).send();
	}
	
	@Observer("INFO")
	public void doInfo(SipServletRequest request) throws Exception {
		request.createResponse(200).send();
	}
	
	@Observer("REGISTER")
	public void doRegister(SipServletRequest request) throws Exception {
		request.createResponse(200).send();
	}
}

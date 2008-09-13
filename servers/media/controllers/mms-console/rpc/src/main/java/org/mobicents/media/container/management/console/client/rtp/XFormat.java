package org.mobicents.media.container.management.console.client.rtp;

import com.google.gwt.user.client.rpc.IsSerializable;

public class XFormat implements IsSerializable {

	protected String rtpMap = null;
	protected String format = null;

	public XFormat() {
		super();
		
	}
	public XFormat(String format, String rtpMap) {
		super();
		this.format = format;
		this.rtpMap = rtpMap;
	}

	public String getRtpMap() {
		return rtpMap;
	}

	public void setRtpMap(String rtpMap) {
		this.rtpMap = rtpMap;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String toString() {
		String result = "";

		// <payload>
		// <rtpmap>0</rtpmap>
		// <format>H261/90000</format>
		// </payload>
		result += "<payload>\n";
		result += "     <rtpmap>" + rtpMap + "</rtpmap>\n";
		result += "     <format>" + format + "</format>\n";
		result += "</payload>\n";

		return result;
	}

}

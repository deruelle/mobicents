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
		//String result = "";

		// <payload>
		// <rtpmap>0</rtpmap>
		// <format>H261/90000</format>
		// </payload>
		//result += "<payload>\n";
		//result += "     <rtpmap>" + rtpMap + "</rtpmap>\n";
		//result += "     <format>" + format + "</format>\n";
		//result += "</payload>\n";
		String result=" "+this.rtpMap+" = "+this.format;

		return result;
	}

	public static XFormat[] fromString(String formats)
	{

		if(formats==null)
			return new XFormat[0];
	
		//FIXME: its a hack....
		formats=formats.replaceAll(".0 Hz", "");
		formats=formats.replaceAll("Mono", "1");
		formats=formats.replaceAll("-bit", "");
		formats=formats.replaceAll("Stereo", "2");
		formats=formats.replaceAll(", Unknown Sample Rate", "");
		String[] _formats=formats.split(";");
		
		XFormat[] result=new XFormat[_formats.length];
		for(int i=0;i<_formats.length;i++)
		{
			String format=_formats[i];
			String[] parts=format.split("=");
			XFormat xf=new XFormat(parts[1].trim(),parts[0].trim());
			result[i]=xf;
			
		}
		
		return result;
		
		
	}
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((rtpMap == null) ? 0 : rtpMap.hashCode());
		return result;
	}
	public boolean equals(Object obj) {
		
		XFormat other = (XFormat) obj;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.trim().equals(other.format.trim()))
			return false;
		if (rtpMap == null) {
			if (other.rtpMap != null)
				return false;
		} else if (!rtpMap.trim().equals(other.rtpMap.trim()))
			return false;
		return true;
	}
	
	public Object clone()
	{
		return new XFormat(format,rtpMap);
	}
	
}

package org.mobicents.media.container.management.console.client.endpoint;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EndpointType implements IsSerializable{

	protected String type=null;
	protected String imageName=null;
	public static final EndpointType CONF=new EndpointType("CONF","endpoints.conf.gif");
	public static final EndpointType PCKT_RELAY=new EndpointType("PACKETRELAY","endpoints.pr.png");
	public static final EndpointType IVR=new EndpointType("IVR","endpoints.ivr.gif");
	public static final EndpointType ANNOUNCEMENT=new EndpointType("ANNOUNCEMENT","endpoints.ann.gif");
	public static final EndpointType LOOPBACK=new EndpointType("LOOPBACK","endpoints.loop.gif");
	
	public static final EndpointType[] defined;
	static
	{
		EndpointType[] tmp=new EndpointType[]{ANNOUNCEMENT,CONF,IVR,PCKT_RELAY,LOOPBACK};
		//EndpointType[] tmp=new EndpointType[]{ANNOUNCEMENT,CONF,IVR,PCKT_RELAY};
		defined=tmp;
		
	}
	
	
	public EndpointType() {
		super();
	}


	

	
	public EndpointType(String type, String imageName) {
		super();
		this.type = type;
		this.imageName = imageName;
	}





	public String toString() {
		
		return "EndpointType : "+type;
	}


	public String getType() {
		return type;
	}
	
	public String getImageName()
	{
		return this.imageName;
	}
	
	
}

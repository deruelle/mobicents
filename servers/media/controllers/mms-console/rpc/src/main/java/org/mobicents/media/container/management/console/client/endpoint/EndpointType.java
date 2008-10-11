package org.mobicents.media.container.management.console.client.endpoint;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EndpointType implements IsSerializable{

	protected String type=null;
	
	public static final EndpointType CONF=new EndpointType("CONF");
	public static final EndpointType PCKT_RELAY=new EndpointType("PACKET RELAY");
	public static final EndpointType IVR=new EndpointType("IVR");
	public static final EndpointType ANNOUNCEMENT=new EndpointType("ANNOUNCEMENT");
	//public static final EndpointType LOOPBACK=new EndpointType("LOOPBACK");
	
	public static final EndpointType[] defined;
	static
	{
		//EndpointType[] tmp=new EndpointType[]{ANNOUNCEMENT,CONF,IVR,PCKT_RELAY,LOOPBACK};
		EndpointType[] tmp=new EndpointType[]{ANNOUNCEMENT,CONF,IVR,PCKT_RELAY};
		defined=tmp;
		
	}
	
	
	public EndpointType() {
		super();
		// TODO Auto-generated constructor stub
	}


	public EndpointType(String type) {
		super();
		this.type = type;
	}

	
	public String toString() {
		
		return "EndpointType : "+type;
	}


	public String getType() {
		return type;
	}
	
	
	
}

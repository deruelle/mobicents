package org.mobicents.media.container.management.console.client.endpoint;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NoSuchEndpointException extends Exception implements IsSerializable {

	
	protected EndpointShortInfo info=null;
	
	public NoSuchEndpointException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NoSuchEndpointException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public NoSuchEndpointException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NoSuchEndpointException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public EndpointShortInfo getInfo() {
		return info;
	}

	public void setInfo(EndpointShortInfo info) {
		this.info = info;
	}

}

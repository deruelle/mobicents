package org.mobicents.media.container.management.console.client.endpoint;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NoSuchConnectionException extends Exception implements IsSerializable{

	public NoSuchConnectionException() {
		super();

	}

	public NoSuchConnectionException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public NoSuchConnectionException(String message) {
		super(message);
		
	}

	public NoSuchConnectionException(Throwable cause) {
		super(cause);
		
	}

}

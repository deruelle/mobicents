package org.mobicents.media.container.management.console.client.rtp;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RTPManagementMBeanDoesNotExistException extends Exception implements IsSerializable{

	public RTPManagementMBeanDoesNotExistException() {
		super();
	}

	public RTPManagementMBeanDoesNotExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public RTPManagementMBeanDoesNotExistException(String message) {
		super(message);
	}

	public RTPManagementMBeanDoesNotExistException(Throwable cause) {
		super(cause);
	}

}

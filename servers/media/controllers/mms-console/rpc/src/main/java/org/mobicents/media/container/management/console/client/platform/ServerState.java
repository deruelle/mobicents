package org.mobicents.media.container.management.console.client.platform;


import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerState implements IsSerializable {

	protected String type = null;
	protected String imageName=null;
	public final static ServerState RUNNING = new ServerState("RUNNING","server.state.running.gif");
	public final static ServerState STOPPING = new ServerState("STOPPING","server.state.stopping.gif");
	public final static ServerState STOPPED = new ServerState("STOPPED","server.state.stopped.gif");

	public static final ServerState[] defined;
	static {
		ServerState[] tmp = new ServerState[] { RUNNING, STOPPING, STOPPED };

		defined = tmp;

	}

	public ServerState() {
		super();
	}

	

	private ServerState(String type, String imageName) {
		super();
		this.type = type;
		this.imageName = imageName;
	}



	public String toString() {

		return type;
	}

	public String getState() {
		return type;
	}
	
	

	public String getImageName() {
		return imageName;
	}



	public static ServerState fromString(String name) {
		
		if (name.toUpperCase().equals(RUNNING.toString())) {
			return RUNNING;
		} else if (name.toUpperCase().equals(STOPPING.toString())) {
			return STOPPING;
		} else if (name.toUpperCase().equals(STOPPED.toString())) {
			return STOPPED;
		} else {
			return null;
		}
	}

}

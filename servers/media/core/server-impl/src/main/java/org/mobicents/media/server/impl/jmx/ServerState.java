package org.mobicents.media.server.impl.jmx;

import java.io.Serializable;

public enum ServerState implements Serializable {

	
	RUNNING("RUNNING"), STOPPING("STOPPING"), STOPPED("STOPPED");
	
	
	protected String stateRepresentation;

	private ServerState(String stateRepresentation) {
		this.stateRepresentation = stateRepresentation;
	}
	
	
	public static ServerState fromString(String name)
	{
		System.out.println("DOIGN FETCH: "+name);
		if(name.toUpperCase().equals(RUNNING.toString()))
		{
			return RUNNING;
		}else if(name.toUpperCase().equals(STOPPING.toString()))
		{
			return STOPPING;
		}else if(name.toUpperCase().equals(STOPPED.toString()))
		{
			return STOPPED;
		}else
		{
			return null;
		}
	}


	
	public String toString() {
		
		return stateRepresentation;
	}
	
	
	
}

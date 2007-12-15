package org.mobicents.ant.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mobicents.ant.SubTask;

import org.mobicents.slee.container.management.jmx.SleeCommandInterface;

public class CreateRaLinkTask implements SubTask {
	// Obtain a suitable logger.
    private static Logger logger = Logger.getLogger(org.mobicents.ant.tasks.CreateRaLinkTask.class.getName());
	
    public void run(SleeCommandInterface slee) {
		// TODO Auto-generated method stub
    	try {
    		// Invoke the operation
			Object result = slee.invokeOperation("-createRaLink", linkName, entityName, null);
			
    		if (result == null)
    		{
    			logger.info("No response");
    		}
    		else
    		{
    			logger.info(result.toString());
    		}
		}
    	
    	catch (Exception ex)
		{
    		// Log the error
            logger.log(Level.WARNING, "Bad result: " + slee.commandBean + "." + slee.commandString +
            		"\n" + ex.getCause().toString());
		}
	}
	
	// The setter for the "entityName" attribute	
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	// The setter for the "linkName" attribute
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	private String entityName = null;
	private String linkName = null;	
}
package org.mobicents.ant.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mobicents.ant.SubTask;

import org.mobicents.slee.container.management.jmx.SleeCommandInterface;

public class RemoveProfileTableTask implements SubTask {
	// Obtain a suitable logger.
    private static Logger logger = Logger.getLogger(org.mobicents.ant.tasks.RemoveProfileTableTask.class.getName());
	
    public void run(SleeCommandInterface slee) {
		// TODO Auto-generated method stub
    	try {
    		// Invoke the operation
			Object result = slee.invokeOperation("-removeProfileTable", profileTableName, null, null);
			
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
    
	// The setter for the "profileTableName" attribute
    public void setTableName(String profileTableName) {
        this.profileTableName = profileTableName;
    }
    
    private String profileTableName = null;
}
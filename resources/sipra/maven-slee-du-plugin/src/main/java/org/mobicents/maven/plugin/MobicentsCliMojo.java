/**
 * 
 */
package org.mobicents.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mobicents.slee.container.management.jmx.SleeCommandInterface;

/**
 * @author jean.deruelle@gmail.com
 * @goal mobicents-cli 
 *
 */
public class MobicentsCliMojo extends AbstractMojo {
	/**
	 * @parameter
	 * @required
	 */
	private String host;
	/**
	 * @parameter
	 * @required
	 */
	private String jnpPort;
	/**
	 * @parameter
	 * @required
	 */
	private CliCommand[] commands;
	
	
	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("host:"+host);
		getLog().info("jnpPort:"+jnpPort);
		for (int i = 0; i < commands.length; i++) {
			StringBuffer commandLine = new StringBuffer("-");
			commandLine.append(commands[i].getName());
			if(commands[i].getArg1() != null) {
				commandLine.append(" ");
				commandLine.append(commands[i].getArg1());
			}
			if(commands[i].getArg2() != null) {
				commandLine.append(" ");
				commandLine.append(commands[i].getArg2());
			}
			if(commands[i].getArg3() != null) {
				commandLine.append(" ");
				commandLine.append(commands[i].getArg3());
			}
			getLog().info("commandLine: "+commandLine);
			try {
				SleeCommandInterface sleeCommandInterface = new SleeCommandInterface("jnp://" + 
						host + ":" + jnpPort);
				
				Object result = sleeCommandInterface.invokeOperation("-"+commands[i].getName(), commands[i].getArg1(), commands[i].getArg2(), commands[i].getArg3());
				
				if (result == null)
	    		{
	    			getLog().info("No response");
	    		}
	    		else
	    		{
	    			getLog().info(result.toString());
	    		}
				
			} catch (Exception ex) {
				// Log the error
				getLog().info("Bad result: " + ex.getCause().toString());
				throw new MojoExecutionException("Bad result: " + ex.getCause().toString(),ex);
			} 	
		}
	}

}

package org.mobicents.maven.plugin;
/**
 * @author jean.deruelle@gmail.com
 *
 */
public class CliCommand {
	//Those are set by the configuration
	private String name;
	
	private String arg1;
	
	private String arg2;
	
	private String arg3;

	/**
	 * @return Returns the arg1.
	 */
	public String getArg1() {
		return arg1;
	}

	/**
	 * @param arg1 The arg1 to set.
	 */
	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}

	/**
	 * @return Returns the arg2.
	 */
	public String getArg2() {
		return arg2;
	}

	/**
	 * @param arg2 The arg2 to set.
	 */
	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}

	/**
	 * @return Returns the arg3.
	 */
	public String getArg3() {
		return arg3;
	}

	/**
	 * @param arg3 The arg3 to set.
	 */
	public void setArg3(String arg3) {
		this.arg3 = arg3;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}

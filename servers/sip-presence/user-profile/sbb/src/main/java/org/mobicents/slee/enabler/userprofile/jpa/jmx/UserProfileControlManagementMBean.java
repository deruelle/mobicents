package org.mobicents.slee.enabler.userprofile.jpa.jmx;

import javax.slee.management.ManagementException;

/**
 * JMX Configuration of the User Profile Control.
 * 
 * @author martins
 *
 */
public interface UserProfileControlManagementMBean {

	
	public static final String MBEAN_NAME="slee:userprofile=UserProfileControl";
	
	/**
	 * Adds a new user with the specified username and password.
	 * 
	 * @param username
	 * @param password
	 * @throws NullPointerException if the username is null
 	 * @throws IllegalStateException if the user already exists
	 * @throws ManagementException if an unexpected error occurred
	 */
	public void addUser(String username, String password) throws NullPointerException, IllegalStateException, ManagementException;
	
	/**
	 * 
	 * Removes the user with specified username.
	 * 
	 * @param username
	 * @return true if the user existed and was removed, false otherwise
	 * @throws NullPointerException if the username is null
	 * @throws ManagementException if an unexpected error occurred
	 */
	public boolean removeUser(String username) throws NullPointerException, ManagementException;
	
	/**
	 * Retrieves all users.
	 * 
	 * @return
	 * @throws ManagementException if an unexpected error occurred
	 */
	public String[] listUsers() throws ManagementException;

	/**
	 * Retrieves all users, separated by commas, in a single string.
	 * 
	 * @return
	 * @throws ManagementException if an unexpected error occurred
	 */
	public String listUsersAsString() throws ManagementException;
}

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
	 * Adds a new user with the specified username, realm and password.
	 * 
	 * @param username
	 * @param realm
	 * @param password
	 * @throws NullPointerException if the username or realm params are null
 	 * @throws IllegalStateException if the user already exists
	 * @throws ManagementException if an unexpected error occurred
	 */
	public void addUser(String username, String realm, String password) throws NullPointerException, IllegalStateException, ManagementException;
	
	/**
	 * 
	 * Removes the user with specified username and realm.
	 * 
	 * @param username
	 * @param realm
	 * @return true if the user existed and was removed, false otherwise
	 * @throws NullPointerException if the username or realm params are null
	 * @throws ManagementException if an unexpected error occurred
	 */
	public boolean removeUser(String username, String realm) throws NullPointerException, ManagementException;
	
	/**
	 * Retrieves all users, in the form of username@realm.
	 * 
	 * @return
	 * @throws ManagementException if an unexpected error occurred
	 */
	public String[] listUsers() throws ManagementException;

	/**
	 * Retrieves all users, in a string, each user is returned in form of username@realm.
	 * 
	 * @return
	 * @throws ManagementException if an unexpected error occurred
	 */
	public String listUsersAsString() throws ManagementException;
}

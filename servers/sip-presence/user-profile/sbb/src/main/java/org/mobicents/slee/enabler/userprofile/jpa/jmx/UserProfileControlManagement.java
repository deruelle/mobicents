package org.mobicents.slee.enabler.userprofile.jpa.jmx;

import java.util.ArrayList;
import java.util.Arrays;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.slee.management.ManagementException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.enabler.userprofile.jpa.UserProfile;
import org.mobicents.slee.enabler.userprofile.jpa.UserProfilePrimaryKey;

/**
 * Management interface for user profiles stored on JPA.
 * 
 * @author martins
 * 
 */
public class UserProfileControlManagement implements
		UserProfileControlManagementMBean {

	private static final Logger logger = Logger
			.getLogger(UserProfileControlManagement.class);

	private TransactionManager txMgr;
	
	/**
	 * the jpa entity manager factory to manage user profiles
	 */
	private EntityManagerFactory entityManagerFactory;

	/**
	 * 
	 * @throws Exception
	 */
	public void startService() throws Exception {

		txMgr = (TransactionManager) new InitialContext().lookup("java:/TransactionManager");
		
		Transaction tx = txMgr.suspend();
		entityManagerFactory = Persistence
				.createEntityManagerFactory("mobicents-slee-enabler-userprofile-pu");
		txMgr.resume(tx);

		MBeanServer mbs = SleeContainer.lookupFromJndi().getMBeanServer();
		ObjectName on = null;
		try {
			on = new ObjectName(MBEAN_NAME);

		} catch (MalformedObjectNameException e) {
			logger.error(e);
		}

		try {
			if (mbs.getObjectInstance(on) != null) {
				mbs.unregisterMBean(on);
			}
		} catch (InstanceNotFoundException e) {
			// ignore
		} catch (Exception e) {
			logger.error(e);
		}

		try {
			mbs.registerMBean(this, on);
		} catch (InstanceAlreadyExistsException e) {
			logger.error(e);
		}
				
		logger.info("Management MBean started.");
	}

	/**
	 * 
	 */
	public void stopService() {
		try {
			SleeContainer.lookupFromJndi().getMBeanServer().unregisterMBean(
					new ObjectName(MBEAN_NAME));
			logger.info("Management MBean stopped.");
			
			Transaction tx = txMgr.suspend();
			entityManagerFactory.close();
			txMgr.resume(tx);
			entityManagerFactory = null;
		} catch (Exception e) {
			logger.error("Failed to stop Management MBean.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.slee.xdm.server.userprofile.jmx.
	 * UserProfileControlManagementMBean#addUser(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void addUser(String username, String realm, String password)
			throws NullPointerException, IllegalStateException, ManagementException {

		if (logger.isDebugEnabled()) {
			logger.debug("addUser( username = "+username+" , realm = "+realm+" )");
		}
		
		if (username == null)
			throw new NullPointerException("null username");
		if (realm == null)
			throw new NullPointerException("null realm");

		if (getUser(username, realm) != null) {
			throw new IllegalStateException("user "+username+"@"+realm+" already exists");
		}
		
		EntityManager entityManager = null;
				
		try {
			
			txMgr.begin();
			
			entityManager = entityManagerFactory.createEntityManager();		
			
			UserProfile userProfile = new UserProfile(username, realm);
			userProfile.setPassword(password);

			entityManager.persist(userProfile);
			
			txMgr.commit();
			
			if (logger.isInfoEnabled()) {
				logger.info("Added user "+username+"@"+realm);
			}

		} catch (Throwable e) {
			try {
				txMgr.rollback();			
			} catch (Throwable f) {
				logger.error(e.getMessage(),e);
			}		
			throw new ManagementException(e.getMessage(), e);
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
				
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.slee.xdm.server.userprofile.jmx.
	 * UserProfileControlManagementMBean#getUsers()
	 */
	public String[] listUsers() throws ManagementException {

		if (logger.isDebugEnabled()) {
			logger.debug("listUsers()");
		}
		
		EntityManager entityManager = null;
		try {
			entityManager = entityManagerFactory.createEntityManager();

			ArrayList<String> resultList = new ArrayList<String>();
			for (Object result : entityManager.createNamedQuery(
					UserProfile.JPA_NAMED_QUERY_SELECT_ALL_USERPROFILES)
					.getResultList()) {
				UserProfile userProfile = (UserProfile) result;
				resultList.add(userProfile.getKey().getUsername() + '@'
						+ userProfile.getKey().getRealm());
			}

			String[] resultArray = new String[resultList.size()];
			return resultList.toArray(resultArray);
		} catch (Throwable e) {
			throw new ManagementException(e.getMessage(), e);
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.enabler.userprofile.jpa.jmx.UserProfileControlManagementMBean#listUsersAsString()
	 */
	public String listUsersAsString() throws ManagementException {
		return Arrays.asList(listUsers()).toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.slee.xdm.server.userprofile.jmx.
	 * UserProfileControlManagementMBean#removeUser(java.lang.String,
	 * java.lang.String)
	 */
	public boolean removeUser(String username, String realm)
			throws NullPointerException, ManagementException {

		if (logger.isDebugEnabled()) {
			logger.debug("removeUser( username = "+username+" , realm = "+realm+" )");
		}
		
		if (username == null)
			throw new NullPointerException("null username");
		if (realm == null)
			throw new NullPointerException("null realm");

		EntityManager entityManager = null;
		try {
			
			txMgr.begin();
			
			entityManager = entityManagerFactory.createEntityManager();

			UserProfile userProfile = entityManager.find(UserProfile.class,
					new UserProfilePrimaryKey(username, realm));

			boolean exists = userProfile != null;
			if (exists) {
				entityManager.remove(userProfile);
				if (logger.isInfoEnabled()) {
					logger.info("Removed user "+username+"@"+realm);
				}
			}

			txMgr.commit();
			
			return exists;
		} catch (Throwable e) {
			try {
				txMgr.rollback();			
			} catch (Throwable f) {
				logger.error(e.getMessage(),e);
			}
			throw new ManagementException(e.getMessage(), e);
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
	}

	/**
	 * Retrieves the user profile for the specified username and realm.
	 * 
	 * @param username
	 * @param realm
	 * @return
	 * @throws NullPointerException
	 */
	public UserProfile getUser(String username, String realm)
			throws NullPointerException {

		if (logger.isDebugEnabled()) {
			logger.debug("getUser( username = "+username+" , realm = "+realm+" )");
		}
		
		if (username == null)
			throw new NullPointerException("null username");
		if (realm == null)
			throw new NullPointerException("null realm");

		EntityManager entityManager = entityManagerFactory
				.createEntityManager();

		UserProfile userProfile = entityManager.find(UserProfile.class,
				new UserProfilePrimaryKey(username, realm));

		entityManager.close();

		return userProfile;
	}

}

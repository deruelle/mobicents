package org.mobicents.slee.runtime.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.mobicents.ftf.FTFListener;


/**
 * 
 * Proxy object for timer facility entity data management through JBoss Cache
 * 
 * @author martins
 * 
 */

public class TimerTasksCacheData extends CacheData implements FTFListener {
	
	private final static transient Logger log =Logger.getLogger(TimerTasksCacheData.class);
	/**
	 * the name of the cache node that holds all data
	 */
	private static final String CACHE_NODE_NAME = "timertasks";
	
	//This cant be like that, since all jvm will allocate different object
	//private static final Object CACHE_NODE_MAP_KEY = new Object();
	public static final String CACHE_NODE_MAP_KEY = "CACHE_NODE_MAP_KEY";
	public static final Fqn _TIMER_DATA_FQN = Fqn.fromElements(CACHE_NODE_NAME);
	private TimerTaskDataUser dataUser;
	private TransactionManager txManager;
	/**
	 * 
	 * @param txManager 
	 * @param txManager
	 */
	protected TimerTasksCacheData(Cache jBossCache, TransactionManager txManager) {
		super(_TIMER_DATA_FQN,
				jBossCache);
		this.txManager = txManager;
		
	}

	public void addTaskData(Serializable taskID, Object taskData) {
		getNode().addChild(Fqn.fromElements(taskID)).put(CACHE_NODE_MAP_KEY,
				taskData);
	}

	public boolean removeTaskData(Serializable taskID) {
		return getNode().removeChild(taskID);
	}

	public Object getTaskData(Serializable taskID) {
		final Node childNode = getNode().getChild(taskID);
		if (childNode == null) {
			return null;
		} else {
			return childNode.get(CACHE_NODE_MAP_KEY);
		}
	}

	public Set getTaskDatas() {
		final Node node = getNode();
		if (!node.isLeaf()) {
			Set result = new HashSet();
			Node childNode = null;
			for (Object obj : node.getChildren()) {
				childNode = (Node) obj;
				// add the task, not the timer id
				result.add(childNode.get(CACHE_NODE_MAP_KEY));
			}
			return result;
		}
		else {
			return Collections.EMPTY_SET;
		}
	}
	
	// remove methods for instance:
	/* (non-Javadoc)
	 * @see org.mobicents.ftf.FTFListener#onNodeOwnershipMoved(org.jboss.cache.Fqn, java.util.Map, boolean)
	 */
	public void onNodeOwnershipMoved(Fqn key, Map values, boolean toLocal) {

		if(log.isDebugEnabled())
		{
			log.debug("onNodeOwnershipMoved() : " + key + " - " + toLocal + " - " + values);
		}
		if(this.dataUser == null)
		{
			return;
		}
		boolean created = false;
		try{
		if (this.txManager != null && this.txManager.getTransaction()==null) {
			this.txManager.begin();
			created = true;
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		try {
			Object data = null;
			
			if (toLocal) {
				// we became owner of data
				if (values.containsKey(CACHE_NODE_MAP_KEY)) {
					data =  values.get(CACHE_NODE_MAP_KEY);
					if (data != null) {
						if (this.dataUser.containsLocalResource(data)) {
							// we do nothing?
						} else {
							this.dataUser.recoverLocalResource(data);
						}
					}
				} else {
					// we dont care?

				}

			} else {
				data =  values.get(CACHE_NODE_MAP_KEY);
				if (data != null) {
					if (this.dataUser.containsLocalResource(data)) {
						this.dataUser.removeLocalResource(data);
					} else {
						// we do nothing?
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (this.txManager != null)
				if (created) {
					try {
						this.txManager.commit();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RollbackException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (HeuristicMixedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (HeuristicRollbackException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}

	}

	/* (non-Javadoc)
	 * @see org.mobicents.ftf.FTFListener#onNodeRemoved(org.jboss.cache.Fqn, java.util.Map)
	 */
	public void onNodeRemoved(Fqn key, Map values) {
		if(log.isDebugEnabled())
		{
			log.debug("onNodeRemoved() : "+key+" - "+values);
		}
		
	}

	/**
	 * 
	 */
	public void clearDataUser() {
		this.dataUser = null;
		
	}
	public void setDataUser(TimerTaskDataUser dataUser)
	{
		if(this.dataUser != null)
		{
			throw new IllegalStateException("Cant not set data user again");
		}
		this.dataUser = dataUser;
	}
}

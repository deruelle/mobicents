/*
 * TreeCacheListenerImpl.java
 * 
 * Created on Jun 20, 2005
 * 
 * Created by: M. Ranganathan
 *
 * The Mobicents Open SLEE project
 * 
 * A SLEE for the people!
 *
 * The source code contained in this file is in in the public domain.          
 * It can be used in any project or product without prior permission, 	      
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
 * AND DATA ACCURACY.  We do not warrant or make any representations 
 * regarding the use of the software or the  results thereof, including 
 * but not limited to the correctness, accuracy, reliability or 
 * usefulness of the software.
 */

package org.mobicents.slee.runtime.transaction;

import java.util.HashSet;
import java.util.Hashtable;

import javax.transaction.Transaction;

import org.jboss.cache.Fqn;
import org.jboss.cache.TreeCache;
import org.jboss.cache.TreeCacheListener;
import org.jboss.logging.Logger;
import org.jgroups.View;

/**
 *
 */
public class TreeCacheListenerImpl implements TreeCacheListener {
    
    private static Logger logger = Logger.getLogger(TreeCacheListenerImpl.class);
    
    private static boolean isDebugEnabled = logger.isDebugEnabled();
    
    private TransactionManagerImpl txmgr;
   
    
    public TreeCacheListenerImpl (TransactionManagerImpl txMgr) {
        
        this.txmgr = txMgr;
    }
  
    
  
    
    
    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#nodeCreated(org.jboss.cache.Fqn)
     */
    public void nodeCreated(Fqn fqn) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#nodeRemoved(org.jboss.cache.Fqn)
     */
    public void nodeRemoved(Fqn arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#nodeLoaded(org.jboss.cache.Fqn)
     */
    public void nodeLoaded(Fqn arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#nodeEvicted(org.jboss.cache.Fqn)
     */
    public void nodeEvicted(Fqn arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#nodeModified(org.jboss.cache.Fqn)
     */
    public void nodeModified(Fqn fqn) {
        if(logger.isDebugEnabled())
        logger.debug("node modified " + fqn);
    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#nodeVisited(org.jboss.cache.Fqn)
     */
    public void nodeVisited(Fqn fqn) {
        if ( TreeCacheListenerImpl.isDebugEnabled) 
            logger.debug("node visited " + fqn);
        
        // DEBUG this.txmgr.owns(fqn.toString());

    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#cacheStarted(org.jboss.cache.TreeCache)
     */
    public void cacheStarted(TreeCache arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#cacheStopped(org.jboss.cache.TreeCache)
     */
    public void cacheStopped(TreeCache arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.jboss.cache.TreeCacheListener#viewChange(org.jgroups.View)
     */
    public void viewChange(View arg0) {
        // TODO Auto-generated method stub

    }

}


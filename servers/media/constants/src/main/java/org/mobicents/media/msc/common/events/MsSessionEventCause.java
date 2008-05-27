package org.mobicents.media.msc.common.events;

/**
 * This enum is for ({@link org.mobicents.mscontrol.MsSession} events firing
 * when STATE of MsSession changes 
 * 
 * <br/> <code>CONNECTION_CREATED</code> indicates the STATE of MsSession is changed because createNetworkConnection called 
 * <br/><code>CONNECTION_DROPPED</code> indicates the STATE of MsSession is changed because drop called 
 * <br/><code>LINK_CREATED</code> indicates the STATE of MsSession is changed because createLink called 
 * <br/><code>LINK_DROPPED</code> indicates the STATE of MsSession is changed because dropLink called
 * 
 * @author amit.bhayani
 * 
 */
public enum MsSessionEventCause {
	CONNECTION_CREATED, CONNECTION_DROPPED, LINK_CREATED, LINK_DROPPED;
}

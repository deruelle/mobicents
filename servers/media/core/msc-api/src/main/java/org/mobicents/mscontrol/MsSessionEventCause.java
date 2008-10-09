package org.mobicents.mscontrol;

/**
 * This enum is for {@link org.mobicents.mscontrol.MsSession} events firing when
 * STATE of MsSession changes <br/>
 * <ul>
 * <li><code>SESSION_CREATED</code> indicates the STATE of MsSession is IDLE
 * and new instance of MsSession is created <br/> </li><br/>
 * <li><code>CONNECTION_CREATED</code> indicates the STATE of MsSession is
 * changed to ACTIVE because createNetworkConnection called <br/></li><br/>
 * <li><code>CONNECTION_DROPPED</code> indicates the STATE of MsSession is
 * changed (state may change to INVALID if this was the only resource viz.,
 * Connection or Link hold by this session) because drop called <br/></li><br/>
 * <li><code>LINK_CREATED</code> indicates the STATE of MsSession is changed
 * to ACTIVE because createLink called <br/></li><br/>
 * <li><code>LINK_DROPPED</code> indicates the STATE of MsSession is changed
 * (state may change to INVALID if this was the only resource viz., Link or
 * Connection hold by this session) because dropLink called</li>
 * </ul>
 * 
 * @author amit.bhayani
 * 
 */
public enum MsSessionEventCause {
	SESSION_CREATED, CONNECTION_CREATED, CONNECTION_DROPPED, LINK_CREATED, LINK_DROPPED;
}

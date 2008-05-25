/*
 * MgcpSbb.java
 *
 * Mobicents Media Gateway
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

package org.mobicents.media.server.control.mgcp;

import jain.protocol.ip.mgcp.message.CreateConnection;
import jain.protocol.ip.mgcp.message.DeleteConnection;

import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;

import org.apache.log4j.Logger;

/**
 * @author amit bhayani
 * @author Oleg Kulikov
 */
public abstract class MgcpSbb implements Sbb {

	private SbbContext sbbContext;
	private Logger logger = Logger.getLogger(MgcpSbb.class);

	/** Creates a new instance of MgcpSbb */
	public MgcpSbb() {
	}

	public void onCreateConnection(CreateConnection event,
			ActivityContextInterface aci) {
		ChildRelation relation = getCreateConnectionSbbChild();
		forwardEvent(relation, aci);
	}

	private void forwardEvent(ChildRelation relation,
			ActivityContextInterface aci) {
		try {
			SbbLocalObject child = relation.create();
			aci.attach(child);
			aci.detach(sbbContext.getSbbLocalObject());
		} catch (Exception e) {
			logger.error("Unexpected error: ", e);
		}
	}

	public abstract ChildRelation getCreateConnectionSbbChild();

	

	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
	}

	public void unsetSbbContext() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbRemove() {
	}

	public void sbbExceptionThrown(Exception exception, Object object,
			ActivityContextInterface activityContextInterface) {
	}

	public void sbbRolledBack(RolledBackContext rolledBackContext) {
	}

}

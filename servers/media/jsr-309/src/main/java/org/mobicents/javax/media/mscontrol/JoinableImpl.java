package org.mobicents.javax.media.mscontrol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.media.mscontrol.Joinable;
import javax.media.mscontrol.MscontrolException;

/**
 * 
 * @author amit bhayani
 * 
 */
public class JoinableImpl implements Joinable {

	private List<Joinable> joinees = new ArrayList<Joinable>();
	private Direction direction = null;

	public Joinable[] getJoinees() throws MscontrolException {
		Joinable[] j = new Joinable[joinees.size()];
		return joinees.toArray(j);
	}

	public Joinable[] getJoinees(Direction direction) throws MscontrolException {
		List<Joinable> joineesTmp = new ArrayList<Joinable>();
		for (Joinable j : joinees) {
			Direction joineeDirection = ((JoinableImpl) j).getDirection();
			if (joineeDirection == direction
					|| joineeDirection == Direction.DUPLEX) {
				joineesTmp.add(j);
			}
		}
		Joinable[] j = new Joinable[joineesTmp.size()];
		return joinees.toArray(j);

	}

	public void join(Direction direction, Joinable other)
			throws MscontrolException {
		this.direction = direction;
		if (direction == Direction.RECV) {
			other.join(Direction.SEND, this);
		} else if (direction == Direction.SEND) {
			other.join(Direction.RECV, this);
		} else {
			other.join(Direction.DUPLEX, this);
		}

	}

	public void joinInitiate(Direction direction, Joinable other,
			Serializable context) throws MscontrolException {		

	}

	public void unjoin(Joinable other) throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public void unjoinInitiate(Joinable other, Serializable context)
			throws MscontrolException {
		// TODO Auto-generated method stub

	}

	public Direction getDirection() {
		return this.direction;
	}

}

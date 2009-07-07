package org.mobicents.slee.sippresence.server.subscription.rules;

import java.util.HashSet;
import java.util.Set;

public class OMAPresRule extends PresRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 865547623453835201L;
	
	private boolean provideBarringState;
	private GeoPrivTransformation provideGeopriv = GeoPrivTransformation.false_;
	private boolean provideNetworkAvailability;
	private boolean provideRegistrationState;
	private Set<String> serviceIDs;
	private boolean provideSessionParticipation;
	private boolean provideWillingness;
	
	/**
	 * combines this OMA pres rule with another.
	 * @param other
	 */
	public void combine(OMAPresRule other) {
		super.combine(other);
		this.provideBarringState = this.provideBarringState || other.provideBarringState;
		if (this.provideGeopriv.getValue() < other.provideGeopriv.getValue()) {
			this.provideGeopriv = other.provideGeopriv;
		}
		this.provideNetworkAvailability = this.provideNetworkAvailability || other.provideNetworkAvailability;
		this.provideRegistrationState = this.provideRegistrationState || other.provideRegistrationState;
		if (other.serviceIDs != null) {
			getServiceIDs().addAll(other.serviceIDs);
		}
		this.provideSessionParticipation = this.provideSessionParticipation || other.provideSessionParticipation;
		this.provideWillingness = this.provideWillingness || other.provideWillingness;
	}

	public boolean isProvideBarringState() {
		return provideBarringState;
	}

	public void setProvideBarringState(boolean provideBarringState) {
		this.provideBarringState = provideBarringState;
	}

	public GeoPrivTransformation getProvideGeopriv() {
		return provideGeopriv;
	}

	public void setProvideGeopriv(GeoPrivTransformation provideGeopriv) {
		this.provideGeopriv = provideGeopriv;
	}

	public boolean isProvideNetworkAvailability() {
		return provideNetworkAvailability;
	}

	public void setProvideNetworkAvailability(boolean provideNetworkAvailability) {
		this.provideNetworkAvailability = provideNetworkAvailability;
	}

	public boolean isProvideRegistrationState() {
		return provideRegistrationState;
	}

	public void setProvideRegistrationState(boolean provideRegistrationState) {
		this.provideRegistrationState = provideRegistrationState;
	}

	public boolean isProvideSessionParticipation() {
		return provideSessionParticipation;
	}

	public void setProvideSessionParticipation(boolean provideSessionParticipation) {
		this.provideSessionParticipation = provideSessionParticipation;
	}

	public boolean isProvideWillingness() {
		return provideWillingness;
	}

	public void setProvideWillingness(boolean provideWillingness) {
		this.provideWillingness = provideWillingness;
	}

	public Set<String> getServiceIDs() {
		if (serviceIDs == null) {
			serviceIDs = new HashSet<String>();
		}
		return serviceIDs;
	}
}

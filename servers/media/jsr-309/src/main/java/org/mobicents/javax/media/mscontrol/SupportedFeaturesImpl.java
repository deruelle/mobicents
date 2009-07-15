package org.mobicents.javax.media.mscontrol;

import java.util.Set;

import javax.media.mscontrol.EventType;
import javax.media.mscontrol.Parameter;
import javax.media.mscontrol.Qualifier;
import javax.media.mscontrol.SupportedFeatures;
import javax.media.mscontrol.Value;
import javax.media.mscontrol.resource.Action;
import javax.media.mscontrol.resource.Trigger;

public class SupportedFeaturesImpl implements SupportedFeatures {

	protected Set<Parameter> parameter = null;

	protected SupportedFeaturesImpl() {

	}

	public Set<Action> getSupportedActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<EventType> getSupportedEventTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Parameter> getSupportedParameters() {
		return parameter;
	}

	public Set<Qualifier> getSupportedQualifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Trigger> getSupportedTriggers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Value> getSupportedValues() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setParameter(Set<Parameter> parameter) {
		this.parameter = parameter;
	}

}

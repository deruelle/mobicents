package org.mobicents.slee.sippresence.server.subscription.rules;

import java.io.Serializable;

/**
 * 
 * @author emmartins
 *
 */
public class UnknownBooleanAttributeTransformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2742492842616195617L;
	private String name;
	private String namespace;
	
	public UnknownBooleanAttributeTransformation(String name, String namespace) {
		this.name = name;
		this.namespace = namespace;
	}
	
	public int hashCode() {
		return name.hashCode()*31+namespace.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			UnknownBooleanAttributeTransformation other = (UnknownBooleanAttributeTransformation)obj;
			return this.name.equals(other.name) && this.namespace.equals(other.namespace);
		}
		else {
			return false;
		}
	}
}

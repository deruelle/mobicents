package org.mobicents.slee.sippresence.server.subscription.rules;

/**
 * Interface to retrieve the published sphere for a resource.
 * @author emmartins
 *
 */
public interface PublishedSphereSource {
 
	public String getSphere(String notifier);
	
}

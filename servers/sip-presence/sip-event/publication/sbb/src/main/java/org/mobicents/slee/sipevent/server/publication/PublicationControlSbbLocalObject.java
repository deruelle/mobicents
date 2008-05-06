package org.mobicents.slee.sipevent.server.publication;

import javax.slee.SbbLocalObject;

import org.mobicents.slee.sipevent.server.publication.pojo.ComposedPublication;

public interface PublicationControlSbbLocalObject extends SbbLocalObject {

	/**
	 * Retreives the composed publication for the specified entity and event package.
	 * @param entity
	 * @param eventPackage
	 * @return
	 */
	public ComposedPublication getComposedPublication(String entity, String eventPackage);
	
}

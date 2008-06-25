package org.mobicents.slee.xdm.server.subscription;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.mobicents.slee.sipevent.server.subscription.pojo.SubscriptionKey;
import org.openxdm.xcap.common.uri.DocumentSelector;

public class Subscriptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SubscriptionKey key;
	private Set<String> appUsages;
	private Set<DocumentSelector> documentSelectors;
	
	public Subscriptions(SubscriptionKey key, Set<String> appUsages,
			Set<DocumentSelector> documentSelectors) {
		this.key = key;
		this.appUsages = appUsages;
		this.documentSelectors = documentSelectors;
		filter();
	}
	
	/*
	 * removes resources that are contained in other resources
	 */
	private void filter() {
		for (Iterator<DocumentSelector> i=documentSelectors.iterator();i.hasNext();) {
			DocumentSelector ds = (DocumentSelector) i.next();
			if(appUsages.contains(ds.getAUID())) {
				// we don't need this resource
				i.remove();
			}
		}
	}
	
	public SubscriptionKey getKey() {
		return key;
	}
	
	public Set<String> getAppUsages() {
		return appUsages;
	}
	
	public Set<DocumentSelector> getDocumentSelectors() {
		return documentSelectors;
	}
	
	
}

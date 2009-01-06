package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.eventlist.rlmi.Instance;
import org.mobicents.slee.sipevent.server.subscription.eventlist.rlmi.List;
import org.mobicents.slee.sipevent.server.subscription.eventlist.rlmi.Name;
import org.mobicents.slee.sipevent.server.subscription.eventlist.rlmi.Resource;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType.DisplayName;

public class NotificationData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * JAXB context is thread safe
	 */
	protected static final JAXBContext rlmiJaxbContext = initJAXBContext();

	private static final Logger logger = Logger.getLogger(NotificationData.class);
	
	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext
					.newInstance("org.mobicents.slee.sipevent.server.subscription.eventlist.rlmi");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
	}
	
	private final HashSet<String> urisLeft = new HashSet<String>();
	private final ArrayList<EntryType> entries = new ArrayList<EntryType>();
	private final int version;
	private final String notifier;
	private final HashMap<String,BodyPart> bodyParts = new HashMap<String, BodyPart>();
	private final HashMap<String,Instance> instances = new HashMap<String, Instance>();
	private final boolean fullState;
	private final String rlmiCid;
	private final String multiPartBoundary;
	private AtomicBoolean locked = new AtomicBoolean(false);
	
	public NotificationData(String notifier, int version, FlatList flatList, String rlmiCid, String multiPartBoundary) {
		this.version = version;
		this.notifier = notifier;
		this.fullState = true;
		this.rlmiCid = rlmiCid;
		this.multiPartBoundary = multiPartBoundary;
		for (EntryType entryType : flatList.getEntries().values()) {
			urisLeft.add(entryType.getUri());
			entries.add(entryType);
		}
	}
	
	public NotificationData(String notifier, int version, EntryType entryType, String rlmiCid, String multiPartBoundary) {
		this.version = version;
		this.notifier = notifier;
		this.fullState = false;
		this.rlmiCid = rlmiCid;
		this.multiPartBoundary = multiPartBoundary;
		urisLeft.add(entryType.getUri());
		entries.add(entryType);
	}
	
	/**
	 * Adds notification data for a resource
	 * @return if all required data is added a {@link MultiPart} will be returned, otherwise null
	 */
	public MultiPart addNotificationData(String uri, String cid, String instanceId, String content, String contentType, String contentSubType, String status, String reason) throws IllegalStateException {		
		if (cid != null) {
			bodyParts.put(uri, new BodyPart("binary",cid,contentType,contentSubType,"UTF-8",content));
		}
		Instance instance = new Instance();
		instance.setId(instanceId);
		instance.setCid(cid);
		instance.setState(status);
		instance.setReason(reason);
		instances.put(uri, instance);
		return notificationDataNotNeeded(uri);
	}
	
	/**
	 * Indicates no notification data for a resource, possibly due to an error, from this invocation this resource won't count on the verification for complete notification data & multipart building 
	 * @return if all required data is added a {@link MultiPart} will be returned, otherwise null
	 */
	public MultiPart notificationDataNotNeeded(String uri) throws IllegalStateException {
		urisLeft.remove(uri);		
		if (urisLeft.isEmpty()) {
			return buildMultipart();
		}
		else {
			return null;
		}
	}
	
	private MultiPart buildMultipart() throws IllegalStateException {
		
		// check lock
		if (!locked.compareAndSet(false, true)) {
			throw new IllegalStateException();
		}
		// create rlmi
		List rlmiList = new List();
		rlmiList.setFullState(fullState);
		rlmiList.setVersion(version);
		rlmiList.setUri(notifier);
		for (EntryType entryType : entries) {
			Resource resource = new Resource();
			resource.setUri(entryType.getUri());
			DisplayName displayName = entryType.getDisplayName();
			if (displayName != null) {
				Name name = new Name();
				name.setLang(displayName.getLang());
				name.setValue(displayName.getValue());
				resource.getName().add(name);
			}
			Instance instance = instances.get(entryType.getUri());
			if (instance != null) {
				resource.getInstance().add(instance);
			}
			rlmiList.getResource().add(resource);
		}
		// marshall it
		String rlmiString = marshallRlmi(rlmiList);
		// create multipart
		MultiPart multiPart = new MultiPart(multiPartBoundary,"application/rlmi+xml");
		// add rlmi body part
		multiPart.getBodyParts().add(new BodyPart("binary",rlmiCid,"application","rlmi+xml","UTF-8",rlmiString));
		// add remaining body parts
		for (BodyPart bodyPart : bodyParts.values()) {
			multiPart.getBodyParts().add(bodyPart);
		}
		return multiPart;
	}
	
	private String marshallRlmi(List rlmiList) {
		StringWriter stringWriter = new StringWriter();
		String result = null;
		try {
			rlmiJaxbContext.createMarshaller().marshal(rlmiList, stringWriter);
			result = stringWriter.toString();
		}
		catch (Exception e) {
			logger.error("failed to marshall rlmi content",e);
		}		
		try {
			stringWriter.close();
		} catch (Exception e) {
			logger.error("failed to close stringwriter used to marshall rlmi content",e);
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return notifier.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((NotificationData)obj).notifier.equals(this.notifier);
		}
		else {
			return false;
		}
	}
}
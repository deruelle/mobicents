package org.mobicents.slee.resource.mgcp.ra;

import java.util.concurrent.ConcurrentHashMap;

import javax.slee.facilities.EventLookupFacility;

import org.apache.log4j.Logger;

/**
 * 
 * @author amit bhayani
 * 
 */
public class EventIDCache {

	private static Logger logger = Logger.getLogger(EventIDCache.class);
	private ConcurrentHashMap<String, Integer> eventIds = new ConcurrentHashMap<String, Integer>();

	public int getEventId(EventLookupFacility eventLookupFacility, String eventName, String eventVendor,
			String eventVersion) {
		String key = eventName + eventVersion;
		Integer integer = eventIds.get(key);
		if (integer == null) {
			try {
				integer = Integer.valueOf(eventLookupFacility.getEventID(eventName, eventVendor, eventVersion));
			} catch (Exception e) {
				logger.error("Error while looking up event = " + eventName + " " + eventVendor + " " + eventVersion, e);
				integer = Integer.valueOf(-1);
			}
			eventIds.put(key, integer);
		}
		return integer.intValue();
	}
}

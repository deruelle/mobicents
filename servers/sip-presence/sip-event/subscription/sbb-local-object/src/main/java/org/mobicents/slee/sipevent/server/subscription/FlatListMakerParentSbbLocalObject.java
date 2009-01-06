package org.mobicents.slee.sipevent.server.subscription;

import javax.slee.SbbLocalObject;

import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ListType;


/**
 * Call back interface for the parent sbb of the
 * {@link FlatListMakerSbbLocalObject}. Provides the responses to
 * the requests sent by the parent sbb and event notifications.
 * 
 * @author Eduardo Martins
 * 
 */
public interface FlatListMakerParentSbbLocalObject extends
		SbbLocalObject {

	/**
	 * provides the {@link FlatList} of {@link EntryType}s, which is the result of flattening a {@link ListType}
	 * @param flatList
	 */
	public void flatListMade(FlatList flatList);

}

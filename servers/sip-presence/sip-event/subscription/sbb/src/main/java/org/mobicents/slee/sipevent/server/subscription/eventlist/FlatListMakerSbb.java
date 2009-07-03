package org.mobicents.slee.sipevent.server.subscription.eventlist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.ChildRelation;
import javax.slee.CreateException;
import javax.slee.RolledBackContext;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.mobicents.slee.sipevent.server.subscription.FlatListMakerParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.FlatListMakerSbbLocalObject;
import org.mobicents.slee.xdm.server.ServerConfiguration;
import org.mobicents.slee.xdm.server.XDMClientControlParentSbbLocalObject;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryRefType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.EntryType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ExternalType;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ListType;
import org.openxdm.xcap.client.appusage.rlsservices.jaxb.ServiceType;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.Parser;
import org.openxdm.xcap.common.uri.ResourceSelector;

/**
 * 
 * 
 * @author Eduardo Martins
 * 
 */
public abstract class FlatListMakerSbb implements Sbb,
		FlatListMakerSbbLocalObject {

	private static final Logger logger = Logger
			.getLogger(FlatListMakerSbb.class);
	
	// --- parent sbb
	
	public abstract void setParentSbbCMP(FlatListMakerParentSbbLocalObject sbbLocalObject);
	public abstract FlatListMakerParentSbbLocalObject getParentSbbCMP();
	
	public void setParentSbb(FlatListMakerParentSbbLocalObject sbbLocalObject) {
		setParentSbbCMP(sbbLocalObject);
	}
	
	// --- lists
	
	public abstract void setFlatList(FlatList value);
	public abstract FlatList getFlatList();
	
	public abstract void setLists(ArrayList value);
	public abstract ArrayList getLists();
	
	public abstract void setCurrentListType(ListType value);
	public abstract ListType getCurrentListType();
	
	// --- sbb logic
	
	/**
	 * flats a tree of {@link ListType}
	 */
	private ArrayList<ListType> addNestedLists(ArrayList<ListType> lists, ListType list) {
		for (Iterator i=list.getListOrExternalOrEntry().iterator(); i.hasNext();) {
			JAXBElement element = (JAXBElement) i.next();
			
			if (element.getValue() instanceof ListType) {
				addNestedLists(lists, (ListType)element.getValue());
				i.remove();
			}
		}
		lists.add(list);
		return lists;
	}
	
	private void processList(FlatList flatList, ArrayList<ListType> lists, ListType currentListType) {
		
		/*
		 * At this point, the RLS has a <list> element in its possession. The
		 * next step is to obtain a flat list of URIs from this element. To do
		 * that, it traverses the tree of elements rooted in the <list> element.
		 * Before traversal begins, the RLS initializes two lists: the "flat
		 * list", which will contain the flat list of the URI after traversal,
		 * and the "traversed list", which contains a list of HTTP URIs in
		 * <external> elements that have already been visited. Both lists are
		 * initially empty. Next, tree traversal begins. A server can use any
		 * tree-traversal ordering it likes, such as depth-first search or
		 * breadth-first search. The processing at each element in the tree
		 * depends on the name of the element:
		 */
		
		for (Iterator i=currentListType.getListOrExternalOrEntry().iterator(); i.hasNext();) {
			JAXBElement element = (JAXBElement) i.next();
			
			// we remove it before processing, so we never get it again
			i.remove();
			
			if (element.getValue() instanceof EntryType) {
				/* o If the element is <entry>, the URI in the "uri" attribute of the
				 * element is added to the flat list if it is not already present (based
				 * on case-sensitive string equality) in that list, and the URI scheme
				 * represents one that can be used to service subscriptions, such as SIP
				 * [4] and pres [15].
				 */
				flatList.putEntry((EntryType) element.getValue());
			}
			
			else if (element.getValue() instanceof EntryRefType) {
				/* o If the element is an <entry-ref>, the relative path reference
				 * making up the value of the "ref" attribute is resolved into an
				 * absolute URI. This is done using the procedures defined in Section
				 * 5.2 of RFC 3986 [7], using the XCAP root of the RLS services document
				 * as the base URI. This absolute URI is resolved. If the result is not
				 * a 200 OK containing a <entry> element, the SUBSCRIBE request SHOULD
				 * be rejected with a 502 (Bad Gateway). Otherwise, the <entry> element
				 * returned is processed as described in the previous step.
				 */
				
				// we need to derrefer the entry, which is async, so we need to store current list
				setCurrentListType(currentListType);
				setFlatList(flatList);
				setLists(lists);
				// now lets ask for it from the xdm
				EntryRefType entryRefType = (EntryRefType) element.getValue();
				try {
					ResourceSelector resourceSelector = null;
					int queryComponentSeparator = entryRefType.getRef().indexOf('?');
					if (queryComponentSeparator > 0) {
						resourceSelector = Parser
								.parseResourceSelector(
										null,
										entryRefType.getRef()
												.substring(0,
														queryComponentSeparator),
										entryRefType.getRef()
												.substring(
														queryComponentSeparator + 1));
					} else {
						resourceSelector = Parser
								.parseResourceSelector(
										null,
										entryRefType.getRef(), null);
					}
					getXDMClientControlSbb().get(new XcapUriKey(resourceSelector),null);
					return;
				}
				catch (Exception e) {
					logger.error("failed to parse entry ref "+entryRefType.getRef(),e);
					flatList.setStatus(Response.BAD_GATEWAY);					
				}				
			}
			
			else if (element.getValue() instanceof ExternalType) {
				
				 /* o If the element is an <external> element, the absolute URI making up
				 * the value of the "anchor" attribute of the element is examined. If
				 * the URI is on the traversed list, the server MUST cease traversing
				 * the tree, and SHOULD reject the SUBSCRIBE request with a 502 (Bad
				 * Gateway). If the URI is not on the traversed list, the server adds
				 * the URI to the traversed list, and dereferences the URI. If the
				 * result is not a 200 OK containing a <list> element, the SUBSCRIBE
				 * request SHOULD be rejected with a 502 (Bad Gateway). Otherwise, the
				 * RLS replaces the <external> element in its local copy of the tree
				 * with the <list> element that was returned, and tree traversal
				 * continues.
				 * 
				 * 
				 * Because the <external> element is used to dynamically construct the
				 * tree, there is a possibility of recursive evaluation of references.
				 * The traversed list is used to prevent this from happening.
				 * 
				 * Once the tree has been traversed, the RLS can create virtual
				 * subscriptions to each URI in the flat list, as defined in [14]. In
				 * the processing steps outlined above, when an <entry-ref> or
				 * <external> element contains a reference that cannot be resolved,
				 * failing the request is at SHOULD strength. In some cases, an RLS may
				 * provide better service by creating virtual subscriptions to the URIs
				 * in the flat list that could be obtained, omitting those that could
				 * not. Only in those cases should the SHOULD recommendation be ignored.
				 * 
				 * 
				 */
				
				// FIXME set 502 for now
				flatList.setStatus(Response.BAD_GATEWAY);
			}
		}
		
		// if we get here this list is fully processed, so move to the next one
		if (!lists.isEmpty()) {
			ListType nextListType = lists.remove(lists.size()-1);
			processList(flatList, lists, nextListType);
		}
		else {
			getParentSbbCMP().flatListMade(flatList);
		}
	}
	
	private void makeFlatList(FlatList flatList, ListType listType) {
		// the specs don't refer it but a list can contain inner lists, so lets
		// build a queue of lists to process
		ArrayList<ListType> lists = addNestedLists(new ArrayList<ListType>(), listType);
		ListType currentListType = lists.remove(lists.size()-1);
		// kick off the final process
		processList(flatList, lists, currentListType);
	}
	
	public void makeFlatList(ServiceType serviceType) {
		
		// create flat list and store it in cmp
		FlatList flatList = new FlatList(serviceType);
				
		/*
		 * 
		 * If the <service> element had a <list> element, it is extracted. If
		 * the <service> element had a <resource-list> element, its URI content
		 * is dereferenced. The result should be a <list> element. If it is not,
		 * the request SHOULD be rejected with a 502 (Bad Gateway). Otherwise,
		 * that <list> element is extracted.
		 * 
		 */
		ListType listType = serviceType.getList();
		if (listType != null) {
			makeFlatList(flatList, listType);
		}
		else {
			String resourceList = serviceType.getResourceList().trim();
			if (resourceList != null) {
				// get list, FIXME for now support only lists in local xdm
				// lets build the list xdm key
				XcapUriKey key = null;
				try {
					String shemeAndAuthorityURI = getSchemeAndAuthorityURI();
					if (resourceList.startsWith(shemeAndAuthorityURI)) {
						resourceList = resourceList.substring(shemeAndAuthorityURI.length());
					}
					else {
						if (logger.isDebugEnabled()) {
							logger.debug("The resource list uri "+resourceList+" does not starts with server scheme and authority uri "+shemeAndAuthorityURI);
						}
					}
					ResourceSelector resourceSelector = null;
					int queryComponentSeparator = resourceList.indexOf('?');
					if (queryComponentSeparator > 0) {
						resourceSelector = Parser
								.parseResourceSelector(
										getLocalXcapRoot(),
										resourceList
												.substring(0,
														queryComponentSeparator),
										resourceList
												.substring(
														queryComponentSeparator + 1));
					} else {
						resourceSelector = Parser
								.parseResourceSelector(
										getLocalXcapRoot(),
										resourceList, null);
					}
					if (!resourceSelector.getDocumentSelector().startsWith("/resource-lists")) {
						logger.error("Unable to make flat list, invalid or not supported resource list uri: "+serviceType.getResourceList());
						flatList.setStatus(Response.BAD_GATEWAY);
						getParentSbbCMP().flatListMade(flatList);
					}
					key = new XcapUriKey(resourceSelector);
				}
				catch (Exception e) {
					logger.error("Failed to parse resource list "+resourceList,e);
					flatList.setStatus(Response.BAD_GATEWAY);
					getParentSbbCMP().flatListMade(flatList);
					return;
				}				
				setFlatList(flatList);
				getXDMClientControlSbb().get(key,null);	
			}
			else {
				if (logger.isInfoEnabled()) {
					logger.info("service type to make flat list doesn't contains list or resource list");
				}				
				flatList.setStatus(Response.BAD_GATEWAY);
				getParentSbbCMP().flatListMade(flatList);
			}
		}
	}
	
	// --- XDM call backs
	
	public void getResponse(XcapUriKey key, int responseCode, String mimetype,
			String content, String tag) {

		FlatList flatList = getFlatList();
		ArrayList lists = getLists();
		ListType currentListType = getCurrentListType();
		
		if (responseCode == 200) {
			// unmarshall content
			Object o = null;
			StringReader stringReader = new StringReader(content);
			try {				
				o = jaxbContext.createUnmarshaller().unmarshal(stringReader);				
			} catch (JAXBException e) {
				logger.error("failed to unmarshall content for key "+key,e);
				// if it was deferring an entry ref continue
				flatList.setStatus(Response.BAD_GATEWAY);
				if (getCurrentListType() != null) {					
					processList(flatList, lists, currentListType);
				}
				else {
					getParentSbbCMP().flatListMade(flatList);					
				}				
				return;
			}
			finally {
				stringReader.close();
			}

			// check what type of object we got
			if (o instanceof ListType) {
				// we are deferring a resource list
				makeFlatList(flatList,(ListType) o);										
			}
			else if (o instanceof EntryType) {
				// we are deferring a entry ref
								
				// add entry 
				flatList.putEntry((EntryType) o);
				// restart procedure to make flat list
				processList(flatList, lists, currentListType);
			}
		}
		else {
			if (logger.isInfoEnabled()) {
				logger.info("xdm get request didn't returned sucess code. key: "+key);
			}
			flatList.setStatus(Response.BAD_GATEWAY);
			if (getCurrentListType() != null) {					
				processList(flatList, lists, currentListType);
			}
			else {
				getParentSbbCMP().flatListMade(flatList);					
			}				
		}
	}
	
	// --- aux public getter that reuires jboss running, publi so junit tests overwrite it
	
	public String getLocalXcapRoot() {
		return ServerConfiguration.XCAP_ROOT;
	}
	
	public String getSchemeAndAuthorityURI() {
		return ServerConfiguration.SCHEME_AND_AUTHORITY_URI;
	}
	
	// --- XDM CLIENT CHILD SBB
	
	public abstract ChildRelation getXDMClientControlChildRelation();

	public abstract XDMClientControlSbbLocalObject getXDMClientControlChildSbbCMP();

	public abstract void setXDMClientControlChildSbbCMP(
			XDMClientControlSbbLocalObject value);

	public XDMClientControlSbbLocalObject getXDMClientControlSbb() {
		XDMClientControlSbbLocalObject childSbb = getXDMClientControlChildSbbCMP();
		if (childSbb == null) {
			try {
				childSbb = (XDMClientControlSbbLocalObject) getXDMClientControlChildRelation()
						.create();
			} catch (Exception e) {
				logger.error("Failed to create child sbb", e);
				return null;
			}
			setXDMClientControlChildSbbCMP(childSbb);
			childSbb
					.setParentSbb((XDMClientControlParentSbbLocalObject) this.sbbContext
							.getSbbLocalObject());
		}
		return childSbb;
	}
	
	// ---- JAXB
	
	/*
	 * JAXB context is thread safe
	 */
	private static final JAXBContext jaxbContext = initJAXBContext();

	private static JAXBContext initJAXBContext() {
		try {
			return JAXBContext
					.newInstance("org.openxdm.xcap.client.appusage.rlsservices.jaxb"
							+ ":org.openxdm.xcap.client.appusage.resourcelists.jaxb");
		} catch (JAXBException e) {
			logger.error("failed to create jaxb context");
			return null;
		}
	}
	
	// ----------- SBB OBJECT's LIFE CYCLE

	private SbbContext sbbContext;
	
	public void setSbbContext(SbbContext sbbContext) {
		this.sbbContext = sbbContext;
	}
	
	public void sbbActivate() {
	}

	public void sbbCreate() throws CreateException {
	}

	public void sbbExceptionThrown(Exception arg0, Object arg1,
			ActivityContextInterface arg2) {
	}

	public void sbbLoad() {
	}

	public void sbbPassivate() {
	}

	public void sbbPostCreate() throws CreateException {
	}

	public void sbbRemove() {
	}

	public void sbbRolledBack(RolledBackContext arg0) {
	}

	public void sbbStore() {
	}

	public void unsetSbbContext() {
		this.sbbContext = null;
	}
}
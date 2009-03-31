package org.mobicents.slee.sipevent.server.subscription.eventlist.flatlist;

import java.util.ArrayList;
import java.util.Map;

import javax.slee.ChildRelation;
import javax.slee.NoSuchObjectLocalException;
import javax.slee.SLEEException;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;

import org.mobicents.slee.sipevent.server.subscription.FlatListMakerParentSbbLocalObject;
import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatList;
import org.mobicents.slee.sipevent.server.subscription.eventlist.FlatListMakerSbb;
import org.mobicents.slee.xdm.server.XDMClientControlSbbLocalObject;
import org.openxdm.xcap.client.appusage.resourcelists.jaxb.ListType;
import org.openxdm.xcap.common.key.XcapUriKey;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.NodeSelector;

public class FlatListMaker extends FlatListMakerSbb{

	private ListType currentListType;
	private FlatList flatList;
	private ArrayList lists;
	private FlatListMakerParentSbbLocalObject parentSbbCMP;
	private XDMClientControlSbbLocalObject xdm;
	private XDMClientChildRelation xdmChildRelation = new XDMClientChildRelation();
	
	@Override
	public String getLocalXcapRoot() {		
		return "/mobicents";
	}
	
	@Override
	public String getSchemeAndAuthorityURI() {
		return "http://127.0.0.1:8080";
	}
	
	@Override
	public ListType getCurrentListType() {
		return currentListType;
	}

	@Override
	public FlatList getFlatList() {
		return flatList;
	}

	@Override
	public ArrayList getLists() {
		return lists;
	}

	@Override
	public FlatListMakerParentSbbLocalObject getParentSbbCMP() {
		return parentSbbCMP;
	}

	@Override
	public ChildRelation getXDMClientControlChildRelation() {
		return xdmChildRelation;
	}

	@Override
	public XDMClientControlSbbLocalObject getXDMClientControlChildSbbCMP() {		
		return xdm;
	}

	@Override
	public void setCurrentListType(ListType value) {
		this.currentListType = value;
	}

	@Override
	public void setFlatList(FlatList value) {
		this.flatList = value;		
	}

	@Override
	public void setLists(ArrayList value) {
		this.lists = value;
		
	}

	@Override
	public void setParentSbbCMP(FlatListMakerParentSbbLocalObject sbbLocalObject) {
		this.parentSbbCMP = sbbLocalObject;
	}

	@Override
	public void setXDMClientControlChildSbbCMP(
			XDMClientControlSbbLocalObject value) {
		this.xdm = value;		
	}

	public void attributeUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, AttributeSelector attributeSelector,
			Map<String, String> namespaces, String oldETag, String newETag,
			String documentAsString, String attributeValue) {
		// TODO Auto-generated method stub
		
	}

	public void deleteResponse(XcapUriKey key, int responseCode, String responseContent, String tag) {
		// TODO Auto-generated method stub
		
	}

	public void documentUpdated(DocumentSelector documentSelector,
			String oldETag, String newETag, String documentAsString) {
		// TODO Auto-generated method stub
		
	}

	public void elementUpdated(DocumentSelector documentSelector,
			NodeSelector nodeSelector, Map<String, String> namespaces,
			String oldETag, String newETag, String documentAsString,
			String elementAsString) {
		// TODO Auto-generated method stub
		
	}

	public void putResponse(XcapUriKey key, int responseCode, String responseContent, String tag) {
		// TODO Auto-generated method stub
		
	}

	public byte getSbbPriority() throws TransactionRequiredLocalException,
			NoSuchObjectLocalException, SLEEException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isIdentical(SbbLocalObject arg0)
			throws TransactionRequiredLocalException, SLEEException {
		// TODO Auto-generated method stub
		return false;
	}

	public void remove() throws TransactionRequiredLocalException,
			NoSuchObjectLocalException, SLEEException {
		// TODO Auto-generated method stub
		
	}

	public void setSbbPriority(byte arg0)
			throws TransactionRequiredLocalException,
			NoSuchObjectLocalException, SLEEException {
		// TODO Auto-generated method stub
		
	}

}

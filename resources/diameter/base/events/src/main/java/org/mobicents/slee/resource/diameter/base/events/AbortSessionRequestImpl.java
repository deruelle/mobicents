package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AbortSessionRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

public class AbortSessionRequestImpl extends DiameterMessageImpl implements AbortSessionRequest
{

	@Override
	public String getLongName() {

		return "Abort-Session-Request";
	}

	@Override
	public String getShortName() {
		
		return "ASR";
	}

	public long getAuthApplicationId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public AvpList getExtensionAvps() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getOriginStateId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ProxyInfoAvp[] getProxyInfos() {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterIdentityAvp[] getRouteRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAuthApplicationId() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasDestinationHost() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasDestinationRealm() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasOriginHost() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasOriginRealm() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasOriginStateId() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasSessionId() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasUserName() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAuthApplicationId(long authApplicationId) {
		// TODO Auto-generated method stub
		
	}

	public void setExtensionAvps(AvpList avps) throws AvpNotAllowedException {
		// TODO Auto-generated method stub
		
	}

	public void setOriginStateId(long originStateId) {
		// TODO Auto-generated method stub
		
	}

	public void setProxyInfo(ProxyInfoAvp proxyInfo) {
		// TODO Auto-generated method stub
		
	}

	public void setProxyInfos(ProxyInfoAvp[] proxyInfos) {
		// TODO Auto-generated method stub
		
	}

	public void setRouteRecord(DiameterIdentityAvp routeRecord) {
		// TODO Auto-generated method stub
		
	}

	public void setRouteRecords(DiameterIdentityAvp[] routeRecords) {
		// TODO Auto-generated method stub
		
	}

	public void setUserName(String userName) {
		// TODO Auto-generated method stub
		
	}

  
}

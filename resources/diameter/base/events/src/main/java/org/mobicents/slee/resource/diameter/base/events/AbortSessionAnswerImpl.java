package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterURI;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.RedirectHostUsageType;

public class AbortSessionAnswerImpl extends DiameterMessageImpl implements
		AbortSessionAnswer {

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterIdentityAvp getErrorReportingHost() {
		// TODO Auto-generated method stub
		return null;
	}

	public AvpList getExtensionAvps() {
		// TODO Auto-generated method stub
		return null;
	}

	public FailedAvp[] getFailedAvps() {
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

	public RedirectHostUsageType getRedirectHostUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterURI[] getRedirectHosts() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getRedirectMaxCacheTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getResultCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasErrorMessage() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasErrorReportingHost() {
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

	public boolean hasRedirectHostUsage() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasRedirectMaxCacheTime() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasResultCode() {
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

	public void setErrorMessage(String errorMessage) {
		// TODO Auto-generated method stub

	}

	public void setErrorReportingHost(DiameterIdentityAvp errorReportingHost) {
		// TODO Auto-generated method stub

	}

	public void setExtensionAvps(AvpList avps) throws AvpNotAllowedException {
		// TODO Auto-generated method stub

	}

	public void setFailedAvp(FailedAvp failedAvp) {
		// TODO Auto-generated method stub

	}

	public void setFailedAvps(FailedAvp[] failedAvps) {
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

	public void setRedirectHost(DiameterURI redirectHost) {
		// TODO Auto-generated method stub

	}

	public void setRedirectHostUsage(RedirectHostUsageType redirectHostUsage) {
		// TODO Auto-generated method stub

	}

	public void setRedirectHosts(DiameterURI[] redirectHosts) {
		// TODO Auto-generated method stub

	}

	public void setRedirectMaxCacheTime(long redirectMaxCacheTime) {
		// TODO Auto-generated method stub

	}

	public void setResultCode(long resultCode) {
		// TODO Auto-generated method stub

	}

	public void setUserName(String userName) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLongName() {

		return "Abbort-Session-Answer";
	}

	@Override
	public String getShortName() {

		return "ASA";
	}
}

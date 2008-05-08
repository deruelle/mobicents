package org.mobicents.slee.resource.diameter.base.events;

import java.util.Date;

import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AccountingRealtimeRequiredType;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

public class AccountingAnswerImpl extends DiameterMessageImpl implements AccountingAnswer
{

	@Override
	public String getLongName() {
		
		return "Accounting-Answer";
	}

	@Override
	public String getShortName() {

		return "ACA";
	}

	public AccountingRealtimeRequiredType getAccountingRealtimeRequired() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getAccountingRecordNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public AccountingRecordType getAccountingRecordType() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getAccountingSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getAccountingSubSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getAcctApplicationId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getAcctInterimInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getAcctMultiSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterIdentityAvp getErrorReportingHost() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getEventTimestamp() {
		// TODO Auto-generated method stub
		return null;
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

	public long getResultCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	public VendorSpecificApplicationIdAvp getVendorSpecificApplicationId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAccountingRealtimeRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAccountingRecordNumber() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAccountingRecordType() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAccountingSessionId() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAccountingSubSessionId() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAcctApplicationId() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAcctInterimInterval() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasAcctMultiSessionId() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasErrorReportingHost() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasEventTimestamp() {
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

	public boolean hasVendorSpecificApplicationId() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAccountingRealtimeRequired(
			AccountingRealtimeRequiredType accountingRealtimeRequired) {
		// TODO Auto-generated method stub
		
	}

	public void setAccountingRecordNumber(long accountingRecordNumber) {
		// TODO Auto-generated method stub
		
	}

	public void setAccountingRecordType(
			AccountingRecordType accountingRecordType) {
		// TODO Auto-generated method stub
		
	}

	public void setAccountingSessionId(byte[] accountingSessionId) {
		// TODO Auto-generated method stub
		
	}

	public void setAccountingSubSessionId(long accountingSubSessionId) {
		// TODO Auto-generated method stub
		
	}

	public void setAcctApplicationId(long acctApplicationId) {
		// TODO Auto-generated method stub
		
	}

	public void setAcctInterimInterval(long acctInterimInterval) {
		// TODO Auto-generated method stub
		
	}

	public void setAcctMultiSessionId(String acctMultiSessionId) {
		// TODO Auto-generated method stub
		
	}

	public void setErrorReportingHost(DiameterIdentityAvp errorReportingHost) {
		// TODO Auto-generated method stub
		
	}

	public void setEventTimestamp(Date eventTimestamp) {
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

	public void setResultCode(long resultCode) {
		// TODO Auto-generated method stub
		
	}

	public void setUserName(String userName) {
		// TODO Auto-generated method stub
		
	}

	public void setVendorSpecificApplicationId(
			VendorSpecificApplicationIdAvp vendorSpecificApplicationId) {
		// TODO Auto-generated method stub
		
	}

  

}

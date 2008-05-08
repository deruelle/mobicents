package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.avp.AddressAvp;
import net.java.slee.resource.diameter.base.events.avp.AvpList;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

public class CapabilitiesExchangeRequestImpl extends DiameterMessageImpl implements CapabilitiesExchangeRequest
{

	@Override
	public String getLongName() {
		
		return "Capabilities-Exchange-Request";
	}

	@Override
	public String getShortName() {

		return "CER";
	}

	public long[] getAcctApplicationIds() {
		// TODO Auto-generated method stub
		return null;
	}

	public long[] getAuthApplicationIds() {
		// TODO Auto-generated method stub
		return null;
	}

	public AvpList getExtensionAvps() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getFirmwareRevision() {
		// TODO Auto-generated method stub
		return 0;
	}

	public AddressAvp[] getHostIpAddresses() {
		// TODO Auto-generated method stub
		return null;
	}

	public long[] getInbandSecurityIds() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getOriginStateId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getProductName() {
		// TODO Auto-generated method stub
		return null;
	}

	public long[] getSupportedVendorIds() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getVendorId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public VendorSpecificApplicationIdAvp[] getVendorSpecificApplicationIds() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasFirmwareRevision() {
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

	public boolean hasProductName() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasVendorId() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAcctApplicationId(long acctApplicationId) {
		// TODO Auto-generated method stub
		
	}

	public void setAcctApplicationIds(long[] acctApplicationIds) {
		// TODO Auto-generated method stub
		
	}

	public void setAuthApplicationId(long authApplicationId) {
		// TODO Auto-generated method stub
		
	}

	public void setAuthApplicationIds(long[] authApplicationIds) {
		// TODO Auto-generated method stub
		
	}

	public void setExtensionAvps(AvpList avps) throws AvpNotAllowedException {
		// TODO Auto-generated method stub
		
	}

	public void setFirmwareRevision(long firmwareRevision) {
		// TODO Auto-generated method stub
		
	}

	public void setHostIpAddress(AddressAvp hostIpAddress) {
		// TODO Auto-generated method stub
		
	}

	public void setHostIpAddresses(AddressAvp[] hostIpAddresses) {
		// TODO Auto-generated method stub
		
	}

	public void setInbandSecurityId(long inbandSecurityId) {
		// TODO Auto-generated method stub
		
	}

	public void setInbandSecurityIds(long[] inbandSecurityIds) {
		// TODO Auto-generated method stub
		
	}

	public void setOriginStateId(long originStateId) {
		// TODO Auto-generated method stub
		
	}

	public void setProductName(String productName) {
		// TODO Auto-generated method stub
		
	}

	public void setSupportedVendorId(long supportedVendorId) {
		// TODO Auto-generated method stub
		
	}

	public void setSupportedVendorIds(long[] supportedVendorIds) {
		// TODO Auto-generated method stub
		
	}

	public void setVendorId(long vendorId) {
		// TODO Auto-generated method stub
		
	}

	public void setVendorSpecificApplicationId(
			VendorSpecificApplicationIdAvp vendorSpecificApplicationId) {
		// TODO Auto-generated method stub
		
	}

	public void setVendorSpecificApplicationIds(
			VendorSpecificApplicationIdAvp[] vendorSpecificApplicationIds) {
		// TODO Auto-generated method stub
		
	}

 

}

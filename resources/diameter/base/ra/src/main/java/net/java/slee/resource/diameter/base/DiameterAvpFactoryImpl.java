package net.java.slee.resource.diameter.base;

import java.net.InetAddress;
import java.util.Date;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.Enumerated;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

public class DiameterAvpFactoryImpl implements DiameterAvpFactory {

	public DiameterAvp createAvp(int avpCode, DiameterAvp[] avps)
			throws NoSuchAvpException, AvpNotAllowedException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, DiameterAvp[] avps)
			throws NoSuchAvpException, AvpNotAllowedException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, byte[] value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, byte[] value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, int value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, int value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, long value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, long value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, float value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, float value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, double value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, double value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, InetAddress value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, InetAddress value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, Date value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, Date value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, String value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, String value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int avpCode, Enumerated value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterAvp createAvp(int vendorID, int avpCode, Enumerated value)
			throws NoSuchAvpException {
		// TODO Auto-generated method stub
		return null;
	}

	public DiameterCommand createCommand(int commandCode, int applicationId,
			String shortName, String longName, boolean isRequest,
			boolean isProxiable) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExperimentalResultAvp createExperimentalResult(long vendorId,
			long experimentalResultCode) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExperimentalResultAvp createExperimentalResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExperimentalResultAvp createExperimentalResult(DiameterAvp avp)
			throws AvpNotAllowedException {
		// TODO Auto-generated method stub
		return null;
	}

	public ExperimentalResultAvp createExperimentalResult(DiameterAvp[] avps)
			throws AvpNotAllowedException {
		// TODO Auto-generated method stub
		return null;
	}

	public FailedAvp createFailedAvp() {
		// TODO Auto-generated method stub
		return null;
	}

	public FailedAvp createFailedAvp(DiameterAvp avp) {
		// TODO Auto-generated method stub
		return null;
	}

	public FailedAvp createFailedAvp(DiameterAvp[] avps) {
		// TODO Auto-generated method stub
		return null;
	}

	public ProxyInfoAvp createProxyInfo(DiameterIdentityAvp proxyHost,
			byte[] proxyState) {
		// TODO Auto-generated method stub
		return null;
	}

	public ProxyInfoAvp createProxyInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public ProxyInfoAvp createProxyInfo(DiameterAvp avp) {
		// TODO Auto-generated method stub
		return null;
	}

	public ProxyInfoAvp createProxyInfo(DiameterAvp[] avps) {
		// TODO Auto-generated method stub
		return null;
	}

	public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId(
			long vendorId) {
		// TODO Auto-generated method stub
		return null;
	}

	public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId() {
		// TODO Auto-generated method stub
		return null;
	}

	public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId(
			DiameterAvp avp) throws AvpNotAllowedException {
		// TODO Auto-generated method stub
		return null;
	}

	public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId(
			DiameterAvp[] avps) throws AvpNotAllowedException {
		// TODO Auto-generated method stub
		return null;
	}

}

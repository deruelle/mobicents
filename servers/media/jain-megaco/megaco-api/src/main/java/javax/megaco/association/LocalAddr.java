package javax.megaco.association;

import java.io.Serializable;

import javax.megaco.ExceptionInfoCode;

import javax.megaco.ParameterNotSetException;

/**
 * This class represents the local transport address. This transport address is
 * used by the stack to send messages to peer. It can be used to store and
 * retrieve the local transport type and the corresponding address. Only one of
 * the transport addresses can be set for the local entity. If the transport is
 * SCTP, then multiple local IP addresses can be set. If transport is TCP or
 * UDP, then only one IPv4/IPv6 address or domain name can be set. In this case
 * optional port id can also be specified.
 * 
 * 
 */
public class LocalAddr implements Serializable {

	private String[] ipAddr = null;
	private TransportType tpt_type = null;
	private String addrString = null;
	private String aal5Addr = null;
	private String mtpAddr = null;

	private String domainName = null;
	private int portId = -1;
	private boolean isPortIdPresent = false;

	/**
	 * Constructs a empty Local Address Identifier reference object. The
	 * parameters would be set to this object using get methods defined for this
	 * class.
	 */
	public LocalAddr() {

	}

	public LocalAddr(java.lang.String[] ipAddr, TransportType tpt_type) throws IllegalArgumentException {
		if (ipAddr == null || tpt_type == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("IP Address or TransportType cannot be null for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO : check for IP valid form

		this.ipAddr = ipAddr;
		this.tpt_type = tpt_type;
	}

	public LocalAddr(java.lang.String addrString, TransportType tpt_type) throws IllegalArgumentException {

		if (addrString == null || tpt_type == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("AddressString or TransportType cannot be null for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO : check for validity

		this.addrString = addrString;
		this.tpt_type = tpt_type;
	}

	public LocalAddr(java.lang.String aal5Addr) throws IllegalArgumentException {

		if (addrString == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("aal5 AddressString cannot be null for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO : check for validity

		tpt_type = TransportType.ATM_TPT;
		this.aal5Addr = aal5Addr;
	}

	public void setDomainName(java.lang.String domainName, TransportType tpt_type) throws IllegalArgumentException {
		if (domainName == null || tpt_type == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("domainName  or TransportType cannot be null for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO: Set the domainName as per tpt_type
		this.domainName = domainName;
		this.tpt_type = tpt_type;
	}

	public void setIpAddr(java.lang.String[] ipAddr, TransportType tpt_type) throws IllegalArgumentException {
		if (domainName == null || tpt_type == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("ipAddr  or TransportType cannot be null for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO: Check for validity

		this.ipAddr = ipAddr;
		this.tpt_type = tpt_type;
	}

	public void setPortId(int portId) throws IllegalArgumentException {

		if (portId < 1) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("portId cannot be less than 1 for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}
		this.isPortIdPresent = true;
		this.portId = portId;
	}

	public void setMtp3Addr(java.lang.String mtpAddr) throws IllegalArgumentException {
		if (mtpAddr == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("mtpAddr cannot be null for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}
		this.tpt_type = TransportType.MTP3B_TPT;
		this.mtpAddr = mtpAddr;
	}

	public void setAAL5Addr(java.lang.String aal5Addr) throws IllegalArgumentException {
		if (aal5Addr == null) {
			IllegalArgumentException invalidArgumentException = new IllegalArgumentException("aal5Addr cannot be null for LocalAddr");
			//invalidArgumentException.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}
		// TODO : Validity check?
		this.aal5Addr = aal5Addr;
		this.tpt_type = TransportType.ATM_TPT;
	}

	public java.lang.String getDomainName() {
		return this.domainName;
	}

	public java.lang.String[] getIpAddr() {
		return this.ipAddr;
	}

	public int getPortId() throws ParameterNotSetException {
		if (!this.isPortIdPresent) {
			ParameterNotSetException parameterNotSetException = new ParameterNotSetException("PortId not yet set for LocalAddr");
			throw parameterNotSetException;
		}
		return this.portId;
	}

	public boolean isPortIdPresent() {
		return this.isPortIdPresent;
	}

	public java.lang.String getMtp3Addr() {
		return this.mtpAddr;
	}

	public java.lang.String getAAL5Addr() {
		return this.aal5Addr;
	}

	public int getTransportType() {
		return this.tpt_type.getTransportType();
	}

	@Override
	public String toString() {
		// TODO : need to recreate
		return super.toString();
	}

}

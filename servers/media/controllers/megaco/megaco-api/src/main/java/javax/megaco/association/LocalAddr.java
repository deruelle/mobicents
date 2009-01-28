package javax.megaco.association;

import java.io.Serializable;

import javax.megaco.ExceptionInfoCode;
import javax.megaco.InvalidArgumentException;

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

	private String domainName = null;
	private int portId;

	/**
	 * Constructs a empty Local Address Identifier reference object. The
	 * parameters would be set to this object using get methods defined for this
	 * class.
	 */
	public LocalAddr() {

	}

	public LocalAddr(java.lang.String[] ipAddr, TransportType tpt_type)
			throws javax.megaco.InvalidArgumentException {
		if (ipAddr == null || tpt_type == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"IP Address or TransportType cannot be null for LocalAddr");
			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO : check for IP valid form

		this.ipAddr = ipAddr;
		this.tpt_type = tpt_type;
	}

	public LocalAddr(java.lang.String addrString, TransportType tpt_type)
			throws javax.megaco.InvalidArgumentException {

		if (addrString == null || tpt_type == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"AddressString or TransportType cannot be null for LocalAddr");
			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO : check for validity

		this.addrString = addrString;
		this.tpt_type = tpt_type;
	}

	public LocalAddr(java.lang.String aal5Addr)
			throws javax.megaco.InvalidArgumentException {

		if (addrString == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"aal5 AddressString cannot be null for LocalAddr");
			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO : check for validity

		tpt_type = TransportType.ATM_TPT;
		this.aal5Addr = aal5Addr;
	}

	public void setDomainName(java.lang.String domainName,
			TransportType tpt_type)
			throws javax.megaco.InvalidArgumentException {
		if (domainName == null || tpt_type == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"domainName  or TransportType cannot be null for LocalAddr");
			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO: Set the domainName as per tpt_type
		this.domainName = domainName;
		this.tpt_type = tpt_type;
	}

	public void setIpAddr(java.lang.String[] ipAddr, TransportType tpt_type)
			throws InvalidArgumentException {
		if (domainName == null || tpt_type == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"ipAddr  or TransportType cannot be null for LocalAddr");
			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}

		// TODO: Check for validity

		this.ipAddr = ipAddr;
		this.tpt_type = tpt_type;
	}

	public void setPortId(int portId) throws InvalidArgumentException {

		if (portId < 1) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"portId cannot be less than 1 for LocalAddr");
			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}
		this.portId = portId;
	}
	
	public void setMtp3Addr(java.lang.String mtpAddr)   throws InvalidArgumentException{
		if (mtpAddr == null) {
			InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
					"mtpAddr cannot be null for LocalAddr");
			invalidArgumentException
					.setInfoCode(ExceptionInfoCode.INV_LOCAL_ADDR);
			throw invalidArgumentException;
		}
		this.tpt_type = TransportType.MTP3B_TPT;
		this.addrString = mtpAddr;
	}
	
	public void setAAL5Addr(java.lang.String aal5Addr)   throws InvalidArgumentException{
		
	}
}
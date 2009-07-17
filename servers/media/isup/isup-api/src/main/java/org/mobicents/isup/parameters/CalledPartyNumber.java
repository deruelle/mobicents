/**
 * Start time:15:59:02 2009-03-29<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Start time:15:59:02 2009-03-29<br>
 * Project: mobicents-isup-stack<br>
 * This represent called party number - Q.763 - 3.9
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author Oleg Kulikoff
 */
public class CalledPartyNumber extends AbstractNAINumber {
	public static final int _PARAMETER_CODE = 0x04;
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _NPI_ISDN = 1;
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _NPI_DATA = 3;
	/**
	 * numbering plan indicator indicator value. See Q.763 - 3.9d
	 */
	public final static int _NPI_TELEX = 4;

	/**
	 * internal network number indicator indicator value. See Q.763 - 3.9c
	 */
	public final static int _INN_ROUTING_ALLOWED = 0;
	/**
	 * internal network number indicator indicator value. See Q.763 - 3.9c
	 * 
	 */
	public final static int _INN_ROUTING_NOT_ALLOWED = 1;

	// Extension to NAI

	/**
	 * nature of address indicator value. See Q.763 - 3.46b network routing
	 * number in national (significant) number format (national use)
	 */
	public final static int _NAI_NRNINNF = 6;

	/**
	 * nature of address indicator value. See Q.763 - 3.46b network routing
	 * number in network-specific number format (national use)
	 */
	public final static int _NAI_NRNINSNF = 7;
	/**
	 * nature of address indicator value. See Q.763 - 3.46b reserved for network
	 * routing number concatenated with Called Directory Number (national use)
	 */
	public final static int _NAI_RNRNCWCDN = 8;
	protected int numberingPlanIndicator;

	protected int internalNetworkNumberIndicator;

	/**
	 * 
	 * 
	 * @param representation
	 */
	public CalledPartyNumber(byte[] representation) {
		super(representation);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * 
	 * @param bis
	 */
	public CalledPartyNumber(ByteArrayInputStream bis) {
		super(bis);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param natureOfAddresIndicator
	 * @param address
	 */
	public CalledPartyNumber(int natureOfAddresIndicator, String address, int numberingPlanIndicator, int internalNetworkNumberIndicator) {
		super(natureOfAddresIndicator, address);
		this.numberingPlanIndicator = numberingPlanIndicator;
		this.internalNetworkNumberIndicator = internalNetworkNumberIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#decodeBody(java.io.
	 * ByteArrayInputStream)
	 */
	@Override
	public int decodeBody(ByteArrayInputStream bis) throws IllegalArgumentException {
		int b = bis.read() & 0xff;

		this.internalNetworkNumberIndicator = (b & 0x80) >> 7;
		this.numberingPlanIndicator = (b & 0x70) >> 4;
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.mobicents.isup.parameters.AbstractNumber#encodeBody(java.io.
	 * ByteArrayOutputStream)
	 */
	@Override
	public int encodeBody(ByteArrayOutputStream bos) {
		int c = (this.numberingPlanIndicator & 0x07) << 4;
		c |= ((this.internalNetworkNumberIndicator & 0x01) << 7);
		bos.write(c);
		return 1;
	}

	public int getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}

	public void setNumberingPlanIndicator(int numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator;
	}

	public int getInternalNetworkNumberIndicator() {
		return internalNetworkNumberIndicator;
	}

	public void setInternalNetworkNumberIndicator(int internalNetworkNumberIndicator) {
		this.internalNetworkNumberIndicator = internalNetworkNumberIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}

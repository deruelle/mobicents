/**
 * Start time:13:49:00 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:13:49:00 2009-03-30<br>
 * Project: mobicents-jain-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 */
public class BackwardCallIndicators extends AbstractParameter {
	/**
	 * See q.763 3.5
	 */
	public static final int _CHARGE_INDICATOR_NOINDICATION = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _CHARGE_INDICATOR_NOCHARGE = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _CHARGE_INDICATOR_CHARGE = 2;

	private int chargeIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _CALLED_PARTYS_STATUS_INDICATOR_NOINDICATION = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _CALLED_PARTYS_STATUS_INDICATOR_SUBSCRIBERFREE = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _CALLED_PARTYS_STATUS_INDICATOR_CONNECTWHENFREE = 2;
	private int calledPartysStatusIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _CALLED_PARTYS_CATEGORY_INDICATOR_NOINDICATION = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _CALLED_PARTYS_CATEGORY_INDICATOR_ORDINARYSUBSCRIBER = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _CALLED_PARTYS_CATEGORY_INDICATOR_PAYPHONE = 2;
	private int calledPartysCategoryIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_NOMETHODAVAILABLE = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_PASSALONG = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_SCCP = 2;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_METHOD_INDICATOR_SCCP_AND_PASSALONG = 3;
	private int endToEndMethodIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _INTERWORKING_INDICATOR_NOT_ENCOUTNERED = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _INTERWORKING_INDICATOR_ENCOUTNERED = 1;
	private int interworkingIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_INFORMATION_INDICATOR_NOT_AVAILABLE = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _END_TO_END_INFORMATION_INDICATOR_AVAILABLE = 1;
	private int endToEndInformationIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _ISDN_USER_PART_INDICATOR_NOTUSED = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _ISDN_USER_PART_INDICATOR_USED = 1;
	private int isdnUserPartIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _ISDN_ACCESS_INDICATOR_TERMINATING_ACCESS_NOT_ISDN = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _ISDN_ACCESS_INDICATOR_TERMINATING_ACCESS_ISDN = 1;
	private int isdnAccessIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _ECHO_CONTROL_DEVICE_NOT_INCLUDED = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _ECHO_CONTROL_DEVICE_INCLUDED = 1;
	private int echoControlDeviceIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _HOLD_INDICATOR_NOT_REQUESTED = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _HOLD_INDICATOR_REQUESTED = 1;
	private int holdingIndicator = 0;

	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_NOINDICATION = 0;
	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_CONNECTIONLESS = 1;
	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_CONNECTION_ORIENTED = 2;
	/**
	 * See q.763 3.5
	 */
	public static final int _SCCP_METHOD_INDICATOR_CONNECTIONLESS_AND_CONNECTION_ORIENTED = 3;
	private int sccpMethodIndicator = 0;

	public BackwardCallIndicators(int chargeIndicator, int calledPartysStatusIndicator, int calledPartysCategoryIndicator, int endToEndMethodIndicator, int interworkingIndicator,
			int endToEndInformationIndicator, int isdnUserPartIndicator, int isdnAccessIndicator, int echoControlDeviceIndicator, int holdingIndicator, int sccpMethodIndicator) {
		super();
		this.chargeIndicator = chargeIndicator;
		this.calledPartysStatusIndicator = calledPartysStatusIndicator;
		this.calledPartysCategoryIndicator = calledPartysCategoryIndicator;
		this.endToEndMethodIndicator = endToEndMethodIndicator;
		this.interworkingIndicator = interworkingIndicator;
		this.endToEndInformationIndicator = endToEndInformationIndicator;
		this.isdnUserPartIndicator = isdnUserPartIndicator;
		this.isdnAccessIndicator = isdnAccessIndicator;
		this.echoControlDeviceIndicator = echoControlDeviceIndicator;
		this.holdingIndicator = holdingIndicator;
		this.sccpMethodIndicator = sccpMethodIndicator;
	}

	/**
	 * requres length 2 array.
	 * 
	 * @param b
	 */
	public BackwardCallIndicators(byte[] b) {
		this.decodeElement(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws IllegalArgumentException {
		if (b == null || b.length != 2) {
			throw new IllegalArgumentException("byte[] must not be null or have different size than 2");
		}

		int v = b[0];
		this.chargeIndicator = v & 0x03;
		this.calledPartysStatusIndicator = (v >> 2) & 0x03;
		this.calledPartysCategoryIndicator = (v >> 4) & 0x03;
		this.endToEndMethodIndicator = (v >> 6) & 0x03;

		v = b[1];
		this.interworkingIndicator = v & 0x01;
		this.endToEndInformationIndicator = (v >> 1) & 0x01;
		this.isdnUserPartIndicator = (v >> 2) & 0x01;
		this.holdingIndicator = (v >> 3) & 0x01;
		this.isdnAccessIndicator = (v >> 4) & 0x01;
		this.echoControlDeviceIndicator = (v >> 5) & 0x01;
		this.sccpMethodIndicator = (v >> 6) & 0x03;
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {

		byte[] b = new byte[2];
		int v = 0;
		v |= this.chargeIndicator & 0x03;
		v |= (this.calledPartysStatusIndicator & 0x03) << 2;
		v |= (this.calledPartysCategoryIndicator & 0x03) << 4;
		v |= (this.endToEndMethodIndicator & 0x03) << 6;
		b[0] = (byte) v;
		v = 0;

		v |= this.interworkingIndicator & 0x01;
		v |= (this.endToEndInformationIndicator & 0x01) << 1;
		v |= (this.isdnUserPartIndicator & 0x01) << 2;
		v |= (this.holdingIndicator & 0x01) << 3;
		v |= (this.isdnAccessIndicator & 0x01) << 4;
		v |= (this.echoControlDeviceIndicator & 0x01) << 5;
		v |= (this.sccpMethodIndicator & 0x03) << 6;

		b[1] = (byte) v;
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.isup.ISUPComponent#encodeElement(java.io.ByteArrayOutputStream
	 * )
	 */
	public int encodeElement(ByteArrayOutputStream bos) throws IOException {
		byte[] b = encodeElement();
		bos.write(b);
		return b.length;
	}

	public int getChargeIndicator() {
		return chargeIndicator;
	}

	public void setChargeIndicator(int chargeIndicator) {
		this.chargeIndicator = chargeIndicator;
	}

	public int getCalledPartysStatusIndicator() {
		return calledPartysStatusIndicator;
	}

	public void setCalledPartysStatusIndicator(int calledPartysStatusIndicator) {
		this.calledPartysStatusIndicator = calledPartysStatusIndicator;
	}

	public int getCalledPartysCategoryIndicator() {
		return calledPartysCategoryIndicator;
	}

	public void setCalledPartysCategoryIndicator(int calledPartysCategoryIndicator) {
		this.calledPartysCategoryIndicator = calledPartysCategoryIndicator;
	}

	public int getEndToEndMethodIndicator() {
		return endToEndMethodIndicator;
	}

	public void setEndToEndMethodIndicator(int endToEndMethodIndicator) {
		this.endToEndMethodIndicator = endToEndMethodIndicator;
	}

	public int getInterworkingIndicator() {
		return interworkingIndicator;
	}

	public void setInterworkingIndicator(int interworkingIndicator) {
		this.interworkingIndicator = interworkingIndicator;
	}

	public int getEndToEndInformationIndicator() {
		return endToEndInformationIndicator;
	}

	public void setEndToEndInformationIndicator(int endToEndInformationIndicator) {
		this.endToEndInformationIndicator = endToEndInformationIndicator;
	}

	public int getIsdnUserPartIndicator() {
		return isdnUserPartIndicator;
	}

	public void setIsdnUserPartIndicator(int isdnUserpartIndicator) {
		this.isdnUserPartIndicator = isdnUserpartIndicator;
	}

	public int getIsdnAccessIndicator() {
		return isdnAccessIndicator;
	}

	public void setIsdnAccessIndicator(int isdnAccessIndicator) {
		this.isdnAccessIndicator = isdnAccessIndicator;
	}

	public int getEchoControlDeviceIndicator() {
		return echoControlDeviceIndicator;
	}

	public void setEchoControlDeviceIndicator(int echoControlDeviceIndicator) {
		this.echoControlDeviceIndicator = echoControlDeviceIndicator;
	}

	public int getHoldingIndicator() {
		return holdingIndicator;
	}

	public void setHoldingIndicator(int holdingIndicator) {
		this.holdingIndicator = holdingIndicator;
	}

	public int getSccpMethodIndicator() {
		return sccpMethodIndicator;
	}

	public void setSccpMethodIndicator(int sccpMethodIndicator) {
		this.sccpMethodIndicator = sccpMethodIndicator;
	}

}

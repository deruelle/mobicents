/**
 * Start time:13:49:00 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Start time:13:49:00 2009-03-30<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public class BackwardCallIndicators extends AbstractParameter {
	public static final int _PARAMETER_CODE = 0x11;

	private final static int _TURN_ON = 1;
	private final static int _TURN_OFF = 0;

	/**
	 * See q.763 3.5 Charge indicator no indication
	 */
	public static final int _CHARGE_INDICATOR_NOINDICATION = 0;
	/**
	 * See q.763 3.5 Charge indicator no charge
	 */
	public static final int _CHARGE_INDICATOR_NOCHARGE = 1;
	/**
	 * See q.763 3.5 Charge indicator charge
	 */
	public static final int _CHARGE_INDICATOR_CHARGE = 2;

	private int chargeIndicator = 0;

	/**
	 * See q.763 3.5 Called party's status indicator no indication
	 */
	public static final int _CPSI_NO_INDICATION = 0;
	/**
	 * See q.763 3.5 Called party's status indicator subscriber free
	 */
	public static final int _CPSI_SUBSCRIBER_FREE = 1;
	/**
	 * See q.763 3.5 Called party's status indicator connect when free (national
	 * use)
	 */
	public static final int _CPSI_CONNECT_WHEN_FREE = 2;
	private int calledPartysStatusIndicator = 0;

	/**
	 * See q.763 3.5 Called party's category indicator
	 */
	public static final int _CPCI_NOINDICATION = 0;
	/**
	 * See q.763 3.5 Called party's category indicator
	 */
	public static final int _CPCI_ORDINARYSUBSCRIBER = 1;
	/**
	 * See q.763 3.5 Called party's category indicator
	 */
	public static final int _CPCI_PAYPHONE = 2;
	private int calledPartysCategoryIndicator = 0;

	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _ETEMI_NOMETHODAVAILABLE = 0;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _ETEMI_PASSALONG = 1;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _ETEMI_SCCP = 2;
	/**
	 * See q.763 3.5 End-to-end method indicator (Note 2)
	 */
	public static final int _ETEMI_SCCP_AND_PASSALONG = 3;
	private int endToEndMethodIndicator = 0;

	/**
	 * See q.763 3.5 Interworking indicator (Note 2) no interworking encountered
	 * (Signalling System No. 7 all the way)
	 */
	public static final boolean _II_NO_IE = false;
	/**
	 * See q.763 3.5 Interworking indicator (Note 2) interworking encountered
	 */
	public static final boolean _II_IE = true;
	private boolean interworkingIndicator = false;

	/**
	 * See q.763 3.5 End-to-end information indicator (national use) (Note 2) no
	 * end-to-end information available
	 */
	public static final boolean _ETEII_NO_IA = false;
	/**
	 * See q.763 3.5 End-to-end information indicator (national use) (Note 2)
	 * end-to-end information available
	 */
	public static final boolean _ETEII_IA = true;
	private boolean endToEndInformationIndicator = false;

	/**
	 * See q.763 3.5 ISDN user part indicator (Note 2) ISDN user part not used
	 * all the way
	 */
	public static final boolean _ISDN_UPI_NOT_UATW = false;
	/**
	 * See q.763 3.5 ISDN user part indicator (Note 2) ISDN user part used all
	 * the way
	 */
	public static final boolean _ISDN_UPI_UATW = true;
	private boolean isdnUserPartIndicator = false;

	/**
	 * See q.763 3.5 ISDN access indicator terminating access non-ISDN
	 */
	public static final boolean _ISDN_AI_TA_NOT_ISDN = false;
	/**
	 * See q.763 3.5 ISDN access indicator terminating access ISDN
	 */
	public static final boolean _ISDN_AI_TA_ISDN = true;
	private boolean isdnAccessIndicator = false;

	/**
	 * See q.763 3.5 Echo control device indicator incoming echo control device
	 * not included
	 */
	public static final boolean _ECDI_IECD_NOT_INCLUDED = false;
	/**
	 * See q.763 3.5 Echo control device indicator incoming echo control device
	 * included
	 */
	public static final boolean _ECDI_IECD_INCLUDED = true;
	private boolean echoControlDeviceIndicator = false;

	/**
	 * See q.763 3.5 Holding indicator (national use)
	 */
	public static final boolean _HI_NOT_REQUESTED = false;
	/**
	 * See q.763 3.5 Holding indicator (national use)
	 */
	public static final boolean _HI_REQUESTED = true;
	private boolean holdingIndicator = false;

	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) no indication
	 */
	public static final int _SCCP_MI_NO_INDICATION = 0;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) connectionless method
	 * available (national use)
	 */
	public static final int _SCCP_MI_CONNECTIONLESS = 1;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) connection oriented method
	 * available
	 */
	public static final int _SCCP_MI_CONNECTION_ORIENTED = 2;
	/**
	 * See q.763 3.5 SCCP method indicator (Note 2) connectionless and
	 * connection oriented methods available (national use)
	 */
	public static final int _SCCP_MI_CONNLESS_AND_CONN_ORIENTED = 3;
	private int sccpMethodIndicator = 0;

	public BackwardCallIndicators(int chargeIndicator, int calledPartysStatusIndicator, int calledPartysCategoryIndicator, int endToEndMethodIndicator, boolean interworkingIndicator,
			boolean endToEndInformationIndicator, boolean isdnUserPartIndicator, boolean isdnAccessIndicator, boolean echoControlDeviceIndicator, boolean holdingIndicator, int sccpMethodIndicator) {
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
		this.interworkingIndicator = (v & 0x01) == _TURN_ON;
		this.endToEndInformationIndicator = ((v >> 1) & 0x01) == _TURN_ON;
		this.isdnUserPartIndicator = ((v >> 2) & 0x01) == _TURN_ON;
		this.holdingIndicator = ((v >> 3) & 0x01) == _TURN_ON;
		this.isdnAccessIndicator = ((v >> 4) & 0x01) == _TURN_ON;
		this.echoControlDeviceIndicator = ((v >> 5) & 0x01) == _TURN_ON;
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

		v |= (this.interworkingIndicator ? _TURN_ON : _TURN_OFF);
		v |= (this.endToEndInformationIndicator ? _TURN_ON : _TURN_OFF) << 1;
		v |= (this.isdnUserPartIndicator ? _TURN_ON : _TURN_OFF) << 2;
		v |= (this.holdingIndicator ? _TURN_ON : _TURN_OFF) << 3;
		v |= (this.isdnAccessIndicator ? _TURN_ON : _TURN_OFF) << 4;
		v |= (this.echoControlDeviceIndicator ? _TURN_ON : _TURN_OFF) << 5;
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

	public boolean isInterworkingIndicator() {
		return interworkingIndicator;
	}

	public void setInterworkingIndicator(boolean interworkingIndicator) {
		this.interworkingIndicator = interworkingIndicator;
	}

	public boolean isEndToEndInformationIndicator() {
		return endToEndInformationIndicator;
	}

	public void setEndToEndInformationIndicator(boolean endToEndInformationIndicator) {
		this.endToEndInformationIndicator = endToEndInformationIndicator;
	}

	public boolean isIsdnUserPartIndicator() {
		return isdnUserPartIndicator;
	}

	public void setIsdnUserPartIndicator(boolean isdnUserPartIndicator) {
		this.isdnUserPartIndicator = isdnUserPartIndicator;
	}

	public boolean isIsdnAccessIndicator() {
		return isdnAccessIndicator;
	}

	public void setIsdnAccessIndicator(boolean isdnAccessIndicator) {
		this.isdnAccessIndicator = isdnAccessIndicator;
	}

	public boolean isEchoControlDeviceIndicator() {
		return echoControlDeviceIndicator;
	}

	public void setEchoControlDeviceIndicator(boolean echoControlDeviceIndicator) {
		this.echoControlDeviceIndicator = echoControlDeviceIndicator;
	}

	public boolean isHoldingIndicator() {
		return holdingIndicator;
	}

	public void setHoldingIndicator(boolean holdingIndicator) {
		this.holdingIndicator = holdingIndicator;
	}

	public int getSccpMethodIndicator() {
		return sccpMethodIndicator;
	}

	public void setSccpMethodIndicator(int sccpMethodIndicator) {
		this.sccpMethodIndicator = sccpMethodIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}

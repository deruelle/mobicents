/**
 * Start time:12:44:04 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:12:44:04 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class UserToUserIndicators extends AbstractParameter {
	private static final int _TURN_ON = 1;
	private static final int _TURN_OFF = 0;

	/**
	 * Service 1,2,3 request : no info
	 */
	public static final int _REQ_Sx_NO_INFO = 0;
	/**
	 * Service 1,2,3 request : not essential
	 */
	public static final int _REQ_Sx_RNE = 2;
	/**
	 * Service 1,2,3 request : essential
	 */
	public static final int _REQ_Sx_RE = 3;

	/**
	 * Service 1,2,3 request : no info
	 */
	public static final int _RESP_Sx_NO_INFO = 0;
	/**
	 * Service 1,2,3 request : not provided
	 */
	public static final int _RESP_Sx_NOT_PROVIDED = 1;

	/**
	 * Service 1,2,3 request : provided
	 */
	public static final int _RESP_Sx_PROVIDED = 2;

	/**
	 * See Q.763 3.60 Network discard indicator : no information
	 */
	public static final boolean _NDI_NO_INFO = false;

	/**
	 * See Q.763 3.60 Network discard indicator : user-to-user information
	 * discarded by the network
	 */
	public static final boolean _NDI_UTUIDBTN = true;
	public static final int _PARAMETER_CODE = 0x2A;

	private boolean response;
	private int serviceOne;
	private int serviceTwo;
	private int serviceThree;
	private boolean networkDiscardIndicator;

	public UserToUserIndicators(byte[] b) throws ParameterRangeInvalidException {
		super();
		decodeElement(b);
	}

	public UserToUserIndicators(boolean response, int serviceOne, int serviceTwo, int serviceThree, boolean networkDiscardIndicator) {
		super();
		this.response = response;
		this.serviceOne = serviceOne;
		this.serviceTwo = serviceTwo;
		this.serviceThree = serviceThree;
		this.networkDiscardIndicator = networkDiscardIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#decodeElement(byte[])
	 */
	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		if (b == null || b.length != 1) {
			throw new ParameterRangeInvalidException("byte[] must  not be null and length must  be 1");
		}
		try {
			this.setResponse((b[0] & 0x01) == _TURN_ON);
			this.setServiceOne((b[0] >> 1));
			this.setServiceTwo((b[0] >> 3));
			this.setServiceThree((b[0] >> 5));
			this.setNetworkDiscardIndicator(((b[0] >> 7) & 0x01) == _TURN_ON);
		} catch (Exception e) {
			throw new ParameterRangeInvalidException(e);
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mobicents.isup.ISUPComponent#encodeElement()
	 */
	public byte[] encodeElement() throws IOException {
		int v = this.response ? _TURN_ON : _TURN_OFF;
		v |= (this.serviceOne & 0x03) << 1;
		v |= (this.serviceTwo & 0x03) << 3;
		v |= (this.serviceThree & 0x03) << 5;
		v |= (this.networkDiscardIndicator ? _TURN_ON : _TURN_OFF) << 7;
		return new byte[] { (byte) v };
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public int getServiceOne() {
		return serviceOne;
	}

	public void setServiceOne(int serviceOne) {
		this.serviceOne = serviceOne & 0x03;
	}

	public int getServiceTwo() {
		return serviceTwo;
	}

	public void setServiceTwo(int serviceTwo) {
		this.serviceTwo = serviceTwo & 0x03;
	}

	public int getServiceThree() {
		return serviceThree;
	}

	public void setServiceThree(int serviceThree) {
		this.serviceThree = serviceThree & 0x03;
	}

	public boolean isNetworkDiscardIndicator() {
		return networkDiscardIndicator;
	}

	public void setNetworkDiscardIndicator(boolean networkDiscardIndicator) {
		this.networkDiscardIndicator = networkDiscardIndicator;
	}

	public int getCode() {

		return _PARAMETER_CODE;
	}
}

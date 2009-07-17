/**
 * Start time:14:02:37 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.isup.parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.mobicents.isup.ParameterRangeInvalidException;

/**
 * Start time:14:02:37 2009-04-04<br>
 * Project: mobicents-isup-stack<br>
 * This is number representation that has NAI field
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski
 *         </a>
 */
public abstract class AbstractNAINumber extends AbstractNumber {
	/**
	 * nature of address indicator value. See Q.763 - 3.9b
	 */
	public final static int _NAI_SPARE = 0;
	/**
	 * nature of address indicator value. See Q.763 - 3.9b
	 */
	public final static int _NAI_SUBSCRIBER = 1;
	/**
	 * nature of address indicator value. See Q.763 - 3.9b
	 */
	public final static int _NAI_UNKNOWN = 2;
	/**
	 * nature of address indicator value. See Q.763 - 3.9b
	 */
	public final static int _NAI_NATIONAL = 3;
	/**
	 * nature of address indicator value. See Q.763 - 3.9b
	 */
	public final static int _NAI_INTERNATIONAL = 4;
	/**
	 * nature of address indicator value. See Q.763 - 3.9b
	 */
	public final static int _NAI_NETWORK_SPECIFIC = 5;

	/**
	 * Holds nature of address indicator bits - those are 7 first bits from
	 * ususaly top byte (first bit is even/odd flag.)
	 */
	protected int natureOfAddresIndicator;

	public AbstractNAINumber(byte[] representation) throws ParameterRangeInvalidException{
		super(representation);

	}

	public AbstractNAINumber(ByteArrayInputStream bis) throws ParameterRangeInvalidException{
		super(bis);

	}

	public AbstractNAINumber( int natureOfAddresIndicator,String address) {
		super(address);
		this.natureOfAddresIndicator = natureOfAddresIndicator;
	}

	public int decodeElement(byte[] b) throws org.mobicents.isup.ParameterRangeInvalidException {
		ByteArrayInputStream bis = new ByteArrayInputStream(b);

		return this.decodeElement(bis);
	}

	protected int decodeElement(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Decoding header");
		}

		int count = decodeHeader(bis);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Decoding header, read count: " + count);
			logger.debug("[" + this.getClass().getSimpleName() + "]Decoding body");
		}
		count += decodeBody(bis);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Decoding body, read count: " + count);
			logger.debug("[" + this.getClass().getSimpleName() + "]Decoding digits");
		}
		count += decodeDigits(bis);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Decoding digits, read count: " + count);
		}
		return count;
	}

	public byte[] encodeElement() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding header");
		}
		int count = encodeHeader(bos);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding header, write count: " + count);
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding body");
		}
		count += encodeBody(bos);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding body, write count: " + count);
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding digits");
		}
		count += encodeDigits(bos);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding digits, write count: " + count);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// out.write(tag);
		// Util.encodeLength(count, out);
		out.write(bos.toByteArray());
		return out.toByteArray();
	}

	public int encodeElement(ByteArrayOutputStream out) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding header");
		}
		int count = encodeHeader(bos);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding header, write count: " + count);
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding body");
		}
		count += encodeBody(bos);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding body, write count: " + count);
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding digits");
		}
		count += encodeDigits(bos);
		if (logger.isDebugEnabled()) {
			logger.debug("[" + this.getClass().getSimpleName() + "]Encoding digits, write count: " + count);
		}

		// count += tag.length;
		// out.write(tag);
		// count += Util.encodeLength(count, out);
		out.write(bos.toByteArray());
		return count;
	}

	public int getNatureOfAddressIndicator() {
		return natureOfAddresIndicator;
	}

	public void setNatureOfAddresIndicator(int natureOfAddresIndicator) {
		this.natureOfAddresIndicator = natureOfAddresIndicator;
	}

	/**
	 * This method is used in encodeElement method. It encodes header part (1 or
	 * 2 bytes usually.)
	 * 
	 * @param bis
	 * @return - number of bytes encoded.
	 */
	public int encodeHeader(ByteArrayOutputStream bos) {
		int b = this.natureOfAddresIndicator & 0x7f;
		// Even is 000000000 == 0
		boolean isOdd = this.oddFlag == _FLAG_ODD;
		
		if (isOdd)
			b |= 0x80;
		
		bos.write(b);

		return 1;
	}

	/**
	 * This method is used in constructor that takes byte[] or
	 * ByteArrayInputStream as parameter. Decodes header part (its 1 or 2 bytes
	 * usually.) Default implemetnation decodes header of one byte - where most
	 * significant bit is O/E indicator and bits 7-1 are NAI. This method should
	 * be over
	 * 
	 * @param bis
	 * @return - number of bytes reads
	 * @throws IllegalArgumentException
	 *             - thrown if read error is encountered.
	 */
	public int decodeHeader(ByteArrayInputStream bis) throws ParameterRangeInvalidException {
		if(bis.available()==0)
		{
			throw new ParameterRangeInvalidException("No more data to read.");
		}
		int b = bis.read() & 0xff;
		
		this.oddFlag = (b & 0x80) >> 7;
		this.natureOfAddresIndicator = b & 0x7f;
		
		return 1;
	}


}

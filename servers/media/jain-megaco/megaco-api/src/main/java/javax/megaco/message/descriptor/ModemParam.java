package javax.megaco.message.descriptor;

import java.io.Serializable;


import javax.megaco.MethodInvocationException;
import javax.megaco.message.Descriptor;

/**
 * The class ModemParam shall be used to set the Modem Type in the Modem
 * Descriptor. This also takes the extension string if the modem type is set to
 * extension parameter.
 */
public class ModemParam implements Serializable {

	private ModemType modemType = null;
	private String extModem;

	/**
	 * Constructs a object of class ModemParam with ModemType. This constructor
	 * can take all values of modem type except {@link ModemType.EXT}.
	 * 
	 * @param modemType
	 * @throws IllegalArgumentException
	 *             - If the object of class ModemType is set to NULL or the
	 *             modem type is set to {@link ModemType.EXT}.
	 */
	public ModemParam(ModemType modemType)

	throws IllegalArgumentException {
		if (modemType == null) {
			throw new IllegalArgumentException("ModemType must not be null");
		}

		this.modemType = modemType;
	}

	/**
	 * 
	 * Constructs a object of class ModemParam with extension string. This
	 * implicitly sets the modem type to {@link ModemType.EXT}.
	 * 
	 * @param extModem
	 *            - Sets the string for the extension of the modem type. The
	 *            extension string should be prefixed with "X-" or "X+". The
	 *            extension characters following the prefix should be at most of
	 *            6 characters. The extension string would be set only when the
	 *            modem type specifies {@link ModemType.EXT}.
	 * @throws IllegalArgumentException
	 *             - If the extension string passed to this method is NULL or if
	 *             the extension string is not in proper format. It should be
	 *             prefixed with either "X+" or "X-" followed by at most 6
	 *             characters.
	 */
	public ModemParam(java.lang.String extModem)

	throws IllegalArgumentException {
		modemType = ModemType.EXT;

		if (extModem == null) {
			throw new IllegalArgumentException("ExtModem must not be null");
		}
		// FIXME??
		DescriptorUtils.checkMethodExtensionRules(extModem);
		if (extModem.length() > 8) {
			throw new IllegalArgumentException("ExtModem length must nto exceed 8 characters");
		}
		this.extModem = extModem;

	}

	/**
	 * This method returns the identity of the modem type. The constants for the
	 * modem type are defined in ModemType.
	 * 
	 * @return Returns an integer value that identifies Modem type. It returns
	 *         one of the values defined in, ModemType.
	 */
	public ModemType getModemType() {
		return this.modemType;
	}

	/**
	 * This method returns the extension string of the modem type. The extension
	 * string should be prefixed with "X-" or "X+". The extension characters
	 * following the prefix should be at most of 6 characters. The extension
	 * string would be set only when the modem type specifies
	 * MODEM_TYPE_EXTENSION.
	 * 
	 * @return Gets the string for the extension of the modem type. The
	 *         extension string would be set only when the modem type specifies
	 *         MODEM_TYPE_EXTENSION.
	 * @throws IllegalStateException
	 *             if the method has been called when the modem type denotes
	 *             anything other than MODEM_TYPE_EXT
	 */
	public java.lang.String getExtensionString() throws IllegalStateException {
		if (this.modemType.getModemType() != ModemType.M_EXT) {
			throw new IllegalStateException("ModemType must be: EXT");
		}
		return this.extModem;
	}

}

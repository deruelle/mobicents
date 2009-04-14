package org.mobicents.jain.protocol.ip.mgcp.pkg;

public class PosKeyValue extends Value {
	private String keyPadKey = null;
	private String posKeyAction = null;
	private Parameter parameter = null;

	public PosKeyValue(Parameter parameter, String keyPadKey, String posKeyAction) {
		super();
		this.parameter = parameter;
		this.keyPadKey = keyPadKey;
		this.posKeyAction = posKeyAction;
	}

	public String getKeyPadKey() {
		return keyPadKey;
	}

	public String getPosKeyAction() {
		return posKeyAction;
	}

	public Parameter getParameter() {
		return this.parameter;
	}

	@Override
	public String toString() {
		String s = this.parameter + "=" + this.keyPadKey + "," + this.posKeyAction;
		return s;
	}

}

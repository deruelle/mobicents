package org.mobicents.jain.protocol.ip.mgcp.pkg;

public class StringValue extends Value {
	private String string = null;
	private Parameter parameter = null;

	public StringValue(Parameter parameter, String str) {
		super();
		this.string = str;
		this.parameter = parameter;

	}

	public String getString() {
		return this.string;
	}

	public Parameter getParameter() {
		return this.parameter;
	}

	@Override
	public String toString() {
		String s = this.parameter + "=" + this.string;
		return s;
	}
}

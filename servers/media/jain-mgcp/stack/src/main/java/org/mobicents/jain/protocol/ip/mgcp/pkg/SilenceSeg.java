package org.mobicents.jain.protocol.ip.mgcp.pkg;

public class SilenceSeg {
	private String silenceSeg = null;

	public SilenceSeg(String silenceSeg) {
		this.silenceSeg = silenceSeg;
	}

	@Override
	public String toString() {
		String s = ParameterEnum.si + "(" + this.silenceSeg + ")";
		return s;
	}

}

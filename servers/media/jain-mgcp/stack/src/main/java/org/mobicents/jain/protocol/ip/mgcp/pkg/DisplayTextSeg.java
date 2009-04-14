package org.mobicents.jain.protocol.ip.mgcp.pkg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DisplayTextSeg {
	private Map<String, String> segSelectorMap = new HashMap<String, String>();
	private String displayText = null;

	public DisplayTextSeg(String displayText) {
		this.displayText = displayText;
	}

	public Map<String, String> getSegSelectorMap() {
		return segSelectorMap;
	}

	public void setSegSelectorMap(Map<String, String> segSelectorMap) {
		this.segSelectorMap = segSelectorMap;
	}

	public String getTextToSpeech() {
		return displayText;
	}

	@Override
	public String toString() {
		String s = ParameterEnum.dt + "(" + this.displayText + ")";

		if (segSelectorMap.size() > 0) {
			s = s + "[";
			boolean first = true;
			Set<String> keys = segSelectorMap.keySet();
			for (String str : keys) {
				if (first) {
					s = s + str + "=" + segSelectorMap.get(str);
					first = false;
				} else {
					s = s + "," + str + "=" + segSelectorMap.get(str);
				}

			}
			s = s + "]";
		}
		return s;
	}
}

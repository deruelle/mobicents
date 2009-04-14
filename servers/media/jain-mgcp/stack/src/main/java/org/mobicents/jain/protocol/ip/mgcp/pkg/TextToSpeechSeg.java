package org.mobicents.jain.protocol.ip.mgcp.pkg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TextToSpeechSeg {
	private Map<String, String> segSelectorMap = new HashMap<String, String>();
	private String textToSpeech = null;

	public TextToSpeechSeg(String textToSpeech) {
		this.textToSpeech = textToSpeech;
	}

	public Map<String, String> getSegSelectorMap() {
		return segSelectorMap;
	}

	public void setSegSelectorMap(Map<String, String> segSelectorMap) {
		this.segSelectorMap = segSelectorMap;
	}

	public String getTextToSpeech() {
		return textToSpeech;
	}

	@Override
	public String toString() {
		String s = ParameterEnum.ts + "(" + this.textToSpeech + ")";

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

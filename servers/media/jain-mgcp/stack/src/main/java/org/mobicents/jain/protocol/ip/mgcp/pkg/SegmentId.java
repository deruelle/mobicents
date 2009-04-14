package org.mobicents.jain.protocol.ip.mgcp.pkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SegmentId {
	private List<String> embedVarList = new ArrayList<String>();
	private Map<String, String> segSelectorMap = new HashMap<String, String>();
	private String segmentId = null;
	private String alias = null;

	public SegmentId(String segmentId, String alias) {
		this.segmentId = segmentId;
		this.alias = alias;
	}

	public List<String> getEmbedVarList() {
		return embedVarList;
	}

	public void setEmbedVarList(List<String> embedVarList) {
		this.embedVarList = embedVarList;
	}

	public Map<String, String> getSegSelectorMap() {
		return segSelectorMap;
	}

	public void setSegSelectorMap(Map<String, String> segSelectorMap) {
		this.segSelectorMap = segSelectorMap;
	}

	public String getSegmentId() {
		return this.segmentId;
	}

	public String getAlias() {
		return this.alias;
	}

	@Override
	public String toString() {
		String s = "";
		if (segmentId != null) {
			s = s + segmentId;
		} else {
			s = s + "/" + alias + "/";
		}

		if (embedVarList.size() > 0) {
			s = s + "<";
			boolean first = true;
			for (String str : embedVarList) {
				if (first) {
					s = s + str;
					first = false;
				} else {
					s = s + "," + str;
				}

			}
			s = s + ">";
		}

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

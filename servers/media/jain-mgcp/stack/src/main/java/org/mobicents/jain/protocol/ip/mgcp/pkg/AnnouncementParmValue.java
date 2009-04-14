package org.mobicents.jain.protocol.ip.mgcp.pkg;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementParmValue extends Value {

	List<SegmentId> segmentIds = new ArrayList<SegmentId>();
	List<TextToSpeechSeg> textToSpeechSegs = new ArrayList<TextToSpeechSeg>();
	List<DisplayTextSeg> displayTextSegs = new ArrayList<DisplayTextSeg>();
	List<SilenceSeg> silenceSegs = new ArrayList<SilenceSeg>();
	Parameter parameter = null;

	public AnnouncementParmValue(Parameter parameter) {
		this.parameter = parameter;
	}

	public List<SegmentId> getSegmentIds() {
		return segmentIds;
	}

	public void addSegmentId(SegmentId s) {
		segmentIds.add(s);
	}

	public List<TextToSpeechSeg> getTextToSpeechSegs() {
		return textToSpeechSegs;
	}

	public void addTextToSpeechSeg(TextToSpeechSeg textToSpeechSeg) {
		textToSpeechSegs.add(textToSpeechSeg);
	}

	public List<DisplayTextSeg> getDisplayTextSegs() {
		return displayTextSegs;
	}

	public void addDisplayTextSeg(DisplayTextSeg displayTextSeg) {
		displayTextSegs.add(displayTextSeg);
	}

	public List<SilenceSeg> getSilenceSegs() {
		return silenceSegs;
	}

	public void addSilenceSeg(SilenceSeg silenceSeg) {
		silenceSegs.add(silenceSeg);
	}

	@Override
	public String toString() {

		String s = this.parameter + "=";
		boolean first = true;
		if (segmentIds.size() > 0) {

			for (SegmentId sId : segmentIds) {
				if (first) {
					s = s + sId.toString();
					first = false;
				} else {
					s = s + "," + sId.toString();
				}
			}
		}

		if (textToSpeechSegs.size() > 0) {
			for (TextToSpeechSeg ttsSeg : textToSpeechSegs) {
				if (first) {
					s = s + ttsSeg.toString();
					first = false;
				} else {
					s = s + "," + ttsSeg.toString();
				}
			}
		}

		if (displayTextSegs.size() > 0) {
			for (DisplayTextSeg dtSeg : displayTextSegs) {
				if (first) {
					s = s + dtSeg.toString();
					first = false;
				} else {
					s = s + "," + dtSeg.toString();
				}
			}
		}

		if (silenceSegs.size() > 0) {
			for (SilenceSeg siSeg : silenceSegs) {
				if (first) {
					s = s + siSeg.toString();
					first = false;
				} else {
					s = s + "," + siSeg.toString();
				}
			}
		}

		return s;
	}

}

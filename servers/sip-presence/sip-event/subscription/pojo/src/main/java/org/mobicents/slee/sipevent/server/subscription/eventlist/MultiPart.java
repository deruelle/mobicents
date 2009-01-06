package org.mobicents.slee.sipevent.server.subscription.eventlist;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple multipart/related string constructor. Doesn't validate references among body part contents.
 * @author martins
 *
 */
public class MultiPart {

	public static final String MULTIPART_CONTENT_TYPE = "multipart";
	public static final String MULTIPART_CONTENT_SUBTYPE = "related";
	
	private final String boundary;
	private final String type;
	private final List<BodyPart> bodyParts; 
	
	public MultiPart(String boundary, String type) {
		this.boundary = boundary;
		this.type = type;
		this.bodyParts = new ArrayList<BodyPart>();
	}
	
	public List<BodyPart> getBodyParts() {
		return bodyParts;
	}
	
	public String getBoundary() {
		return boundary;
	}
	
	public String getType() {
		return type;
	}
	
	public int hashCode() {
		return this.boundary.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			MultiPart other = (MultiPart) obj;
			return other.boundary.equals(this.boundary);
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		
		if (!bodyParts.isEmpty()) {
			final String b = "--"+boundary;
			String result = b;
			for (BodyPart bodyPart : bodyParts) {
				result += "\n"+bodyPart+b;
			}
			result +="--";
			return result;
		}
		else {
			return null;
		}
	}
}

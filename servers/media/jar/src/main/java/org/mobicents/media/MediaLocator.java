package org.mobicents.media;

import java.io.Serializable;

public class MediaLocator implements Serializable {
	private static final long serialVersionUID = -6747425113475481405L;

	private String locatorString;

	public MediaLocator(java.net.URL url) {
		this.locatorString = url.toExternalForm(); // TODO: is this correct?
	}

	public MediaLocator(String locatorString) {
		if (locatorString == null)
			throw new NullPointerException();
		this.locatorString = locatorString;
	}

	public java.net.URL getURL() throws java.net.MalformedURLException {
		return new java.net.URL(locatorString);
	}

	public String getProtocol() {
		int colonIndex = locatorString.indexOf(':');
		if (colonIndex < 0)
			return "";
		return locatorString.substring(0, colonIndex);
	}

	public String getRemainder() {
		int colonIndex = locatorString.indexOf(':');
		if (colonIndex < 0)
			return "";
		return locatorString.substring(colonIndex + 1, locatorString.length());
	}

	public String toString() {
		return locatorString;
	}

	public String toExternalForm() {
		return locatorString;
	}
}

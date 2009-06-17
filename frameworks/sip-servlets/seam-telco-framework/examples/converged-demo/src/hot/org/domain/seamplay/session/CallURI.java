package org.domain.seamplay.session;

import org.jboss.seam.annotations.Name;

@Name("callURI")
public class CallURI {
	private String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}

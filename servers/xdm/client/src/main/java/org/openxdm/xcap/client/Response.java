package org.openxdm.xcap.client;

import java.io.Serializable;

import org.apache.commons.httpclient.Header;

public class Response implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int code;
	private String eTag;
	private Header[] headers;
	private Object content;
	private boolean unmarshalled = false;
	
	public Response(int code,String eTag, Header[] headers, Object content, boolean unmarshalled) {				
		this.code = code;
		this.eTag = eTag;
		this.headers = headers;
		this.content = content;
		this.unmarshalled = unmarshalled;
	}
			
	public int getCode() {
		return code;
	}
	
	public Object getContent() {
		return content;
	}
	
	public boolean isContentUnmarshalled() {
		return unmarshalled;
	}
	
	public String getETag() {
		return eTag;
	}
	
	public Header[] getHeaders() {
		return headers;
	}
			
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Response {").append("\nCode: ").append(code).append("\nHeaders: ");
		boolean first=true;
		for(int i=0;i<headers.length;i++) {
			Header header = headers[i];
			if (!first) {
				sb.append(", ");
				
			} else {
				first=false;
			}
			sb.append(header.getName()).append("=").append(header.getValue());
		}
		
		sb.append("\nContent: ").append(content).append("\n}");
		return sb.toString();
	}
}

package org.mobicents.slee.sipevent.server.subscription;

import javax.sip.header.ContentTypeHeader;

/**
 * Container of content for a SIP notify request
 *
 * @author eduardomartins
 *
 */
public class NotifyContent {

	private Object content;
	private ContentTypeHeader contentTypeHeader;
	
	public NotifyContent(Object content, ContentTypeHeader contentTypeHeader) {
		super();
		this.content = content;
		this.contentTypeHeader = contentTypeHeader;
	}
	
	public Object getContent() {
		return content;
	}
	
	public ContentTypeHeader getContentTypeHeader() {
		return contentTypeHeader;
	}
	
}

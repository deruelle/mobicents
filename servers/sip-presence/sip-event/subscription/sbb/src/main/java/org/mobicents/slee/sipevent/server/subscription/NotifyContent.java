package org.mobicents.slee.sipevent.server.subscription;

import javax.sip.header.ContentTypeHeader;
import javax.xml.bind.JAXBElement;

/**
 * Container of content for a SIP notify request
 *
 * @author eduardomartins
 *
 */
public class NotifyContent {

	private JAXBElement content;
	private ContentTypeHeader contentTypeHeader;
	
	public NotifyContent(JAXBElement content, ContentTypeHeader contentTypeHeader) {
		super();
		this.content = content;
		this.contentTypeHeader = contentTypeHeader;
	}
	
	public JAXBElement getContent() {
		return content;
	}
	
	public ContentTypeHeader getContentTypeHeader() {
		return contentTypeHeader;
	}
	
}

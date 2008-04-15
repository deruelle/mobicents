package org.mobicents.media.protocol;

import org.mobicents.media.Controls;

public interface SourceStream extends Controls {
	public static final long LENGTH_UNKNOWN = -1L;
	public ContentDescriptor getContentDescriptor();
	public long getContentLength();
	public boolean endOfStream();
}

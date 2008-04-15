package org.mobicents.media.protocol;

import java.io.IOException;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;

public interface PushBufferStream extends SourceStream {
	public Format getFormat();

	public void read(Buffer buffer) throws IOException;

	public void setTransferHandler(BufferTransferHandler transferHandler);
}

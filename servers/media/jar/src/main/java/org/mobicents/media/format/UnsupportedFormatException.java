package org.mobicents.media.format;

import org.mobicents.media.Format;
import org.mobicents.media.MediaException;

public class UnsupportedFormatException extends MediaException {
	private final Format unsupportedFormat;

	public UnsupportedFormatException(Format unsupportedFormat) {
		super();
		this.unsupportedFormat = unsupportedFormat;
	}

	public UnsupportedFormatException(String message, Format unsupportedFormat) {
		super(message);
		this.unsupportedFormat = unsupportedFormat;

	}

	public Format getFailedFormat() {
		return unsupportedFormat;
	}
}

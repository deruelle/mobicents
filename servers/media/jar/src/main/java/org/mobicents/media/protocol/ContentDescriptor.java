package org.mobicents.media.protocol;

import org.mobicents.media.Format;

public class ContentDescriptor extends Format {
	public static final String RAW = "raw";

	public static final String RAW_RTP = "raw.rtp";

	// TODO: content types need to have special characters replaced
	// according to mimeTypeToPackageName.  If a web server ever returned this content type as application/mixed-data
	// it would be converted to application.mixed_data, and it would never match any demux for MIXED.
	// However, this is how it is defined in JMF.
	public static final String MIXED = "application.mixed-data";

	public static final String CONTENT_UNKNOWN = "UnknownContent";

	public String getContentType()
	{	return super.getEncoding();
	}
	
	public ContentDescriptor(String cdName)
	{
		super(cdName);
		dataType = byteArray;
	}

	public String toString()
	{	return getContentType();
	}

	private static final boolean isAlpha(char c)
	{	return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	private static final boolean isUpperAlpha(char c)
	{	return c >= 'A' && c <= 'Z';
	}
	private static final boolean isNumeric(char c)
	{	return c >= '0' && c <= '9';
	}
	
	/** Convert to package name. 
	 * a-z -> a-z
	 * 0-9 -> 0->9
	 * A-Z -> a-z
	 * / -> .
	 * . -> .
	 * everything else -> _
	 */
	public static final String mimeTypeToPackageName(String mimeType)
	{
		final StringBuffer b = new StringBuffer();
		for (int i = 0; i < mimeType.length(); ++i)
		{
			char c = mimeType.charAt(i);
			if (c == '/' || c == '.')
				b.append('.');
			else if (isAlpha(c) || isNumeric(c))
			{	if (isUpperAlpha(c))
					b.append(Character.toLowerCase(c));
				else
					b.append(c);
			}
			else
				b.append('_');
		}
		return b.toString();
	}

}

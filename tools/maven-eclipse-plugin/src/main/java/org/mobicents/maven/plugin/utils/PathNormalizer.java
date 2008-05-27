package org.mobicents.maven.plugin.utils;

public class PathNormalizer {
		
	/**
	 * Forward slash character
	 */
			private static final String FORWARD_SLASH = "/";

	/**
	        * The pattern used for normalizing paths paths with more than one back slash.
	        */
	       private static final String BACK_SLASH_NORMALIZATION_PATTERN = "\\\\+";
	   
	       /**
	        * The pattern used for normalizing paths with more than one forward slash.
	        */
	       private static final String FORWARD_SLASH_NORMALIZATION_PATTERN = FORWARD_SLASH + "+";
	   
	       /**
	        * Removes any extra path separators and converts all from back slashes
	        * to forward slashes.
	        *
	        * @param path the path to normalize.
	        * @return the normalizd path
	        */
	       public static String normalizePath(final String path)
	       {
	           return path != null
	           ? path.replaceAll(
	               BACK_SLASH_NORMALIZATION_PATTERN,
	               FORWARD_SLASH).replaceAll(
	               FORWARD_SLASH_NORMALIZATION_PATTERN,
	               FORWARD_SLASH) : null;
	       }
	  
}

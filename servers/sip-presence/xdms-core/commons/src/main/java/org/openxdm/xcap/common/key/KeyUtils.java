package org.openxdm.xcap.common.key;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.openxdm.xcap.common.uri.ElementSelector;

public class KeyUtils {

	protected static String getPercentEncodedDocumentParent(String user) {
		try {
			user = URLEncoder.encode(user,"UTF-8");
		}
		catch(UnsupportedEncodingException e) {
			// must not happen
		}
		return new StringBuilder("users/").append(user).toString();
	}
	
	protected static String getPercentEncodedString(String s) {
		try {
			return URLEncoder.encode(s,"UTF-8");
		}
		catch(UnsupportedEncodingException e) {
			// must not happen
			return null;
		}		
	}
	
	protected static String getPercentEncondedElementSelector(ElementSelector elementSelector) {
		StringBuilder percentEncodedElementSelector = new StringBuilder("/");		
		try {
			boolean first = true;
			for(int i=0;i<elementSelector.getStepsSize();i++) {
				if(!first) {
					percentEncodedElementSelector.append('/');
				}
				else {
					first = false;
				}
				percentEncodedElementSelector.append(URLEncoder.encode(elementSelector.getStep(i).toString(),"UTF-8"));
			}			
		} catch (UnsupportedEncodingException e) {
			// MUST NOT COME HERE
		}
		return percentEncodedElementSelector.toString();
	}
	
}

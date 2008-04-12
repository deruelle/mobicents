package org.openxdm.xcap.common.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.util.XML11Char;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.NotUTF8ConflictException;
import org.openxdm.xcap.common.error.NotValidXMLFragmentConflictException;
import org.openxdm.xcap.common.error.NotWellFormedConflictException;
import org.openxdm.xcap.common.error.NotXMLAttributeValueConflictException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.syndication.io.XmlReader;

public class XMLValidator {

	/**
	 * 
	 * 
	 */
	public static boolean isQName(String name) {
		String[] qName = name.split(":");
		if (qName.length == 1) {
			return XML11Char.isXML11ValidNCName(name);
		} else if (qName.length == 2) {
			return XML11Char.isXML11ValidNCName(qName[0])
					&& XML11Char.isXML11ValidNCName(qName[1]);
		}
		return false;
	}

	/** 
	 * Validates if the specifiedc string is a valid xml attribute value.
	 * Specs say that an attr value is validated by the following regex:
	 * 
	 * AttValue	   	::=	'"' ([^<&"] | Reference)* '"' |  "'" ([^<&'] | Reference)* "'"
	 * Reference 	::= EntityRef | CharRef
	 * EntityRef 	::= '&' Name ';' 
	 * CharRef		::= '&#' [0-9]+ ';' | '&#x' [0-9a-fA-F]+ ';'
	 * 
	 *  
	 * NOTE: The specified string doesn't come with surroundings " or ' so we can't accept both chars!!!!
	 * 
	 * @param value
	 * @return
	 */
	public static void checkAttValue(String value)
			throws NotXMLAttributeValueConflictException {

		try {

			StringBuilder sb = new StringBuilder(value);

			// check and remove char refs 

			// &#x [0-9a-fA-F]+ ;

			Set<String> set = new HashSet<String>();
			while (true) {
				int begin = sb.indexOf("&#x");
				if (begin > -1) {
					// found begin
					int end = sb.indexOf(";", begin + 3);
					if (end > -1) {
						// found an end
						set.add(sb.substring(begin + 3, end));
						sb = new StringBuilder(sb.substring(0, begin))
								.append(sb.substring(end + 1));
					} else {
						break;
					}
				} else {
					break;
				}
			}

			Pattern p = Pattern.compile("[0-9a-fA-F]+");
			for (Iterator<String> i = set.iterator(); i.hasNext();) {
				String t = i.next();
				Matcher m = p.matcher(t);
				if (!m.matches()) {
					throw new NotXMLAttributeValueConflictException();
				}
			}

			// &# [0-9]+ ;

			set = new HashSet<String>();
			while (true) {
				int begin = sb.indexOf("&#");
				if (begin > -1) {
					// found begin
					int end = sb.indexOf(";", begin + 2);
					if (end > -1) {
						// found an end
						set.add(sb.substring(begin + 2, end));
						sb = new StringBuilder(sb.substring(0, begin))
								.append(sb.substring(end + 1));
					} else {
						break;
					}
				} else {
					break;
				}
			}

			p = Pattern.compile("[0-9]+");
			for (Iterator<String> i = set.iterator(); i.hasNext();) {
				String t = i.next();
				Matcher m = p.matcher(t);
				if (!m.matches()) {
					throw new NotXMLAttributeValueConflictException();
				}
			}

			// check and remove entity refs 			
			// & name ;

			set = new HashSet<String>();
			while (true) {
				int begin = sb.indexOf("&");
				if (begin > -1) {
					// found begin
					int end = sb.indexOf(";", begin + 1);
					if (end > -1) {
						// found an end
						set.add(sb.substring(begin + 1, end));
						sb = new StringBuilder(sb.substring(0, begin))
								.append(sb.substring(end + 1));
					} else {
						break;
					}
				} else {
					break;
				}
			}

			// check all names found
			for (Iterator<String> i = set.iterator(); i.hasNext();) {
				String name = i.next();
				if (!XML11Char.isXML11ValidName(name)) {
					throw new NotXMLAttributeValueConflictException();
				}
			}

			// check remaining chars

			for (int i = 0; i < sb.length(); i++) {
				if (sb.charAt(i) == '&' || sb.charAt(i) == '\''
						|| sb.charAt(i) == '"' || sb.charAt(i) == '<') {
					throw new NotXMLAttributeValueConflictException();
				}
			}

		} catch (Exception e) {
			// parsing error
			throw new NotXMLAttributeValueConflictException();
		}

	}

	public static String getUTF8String(InputStream is)
			throws NotUTF8ConflictException, InternalServerErrorException {
		// lets get the byte array in stream
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		try {
			while ((len = is.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
		} catch (IOException e) {
			throw new InternalServerErrorException(e.getMessage());
		}
		byte[] data = bos.toByteArray();
		// now decode the bytes
		CharsetDecoder dec = Charset.forName("UTF8").newDecoder();
		try {
			return dec.decode(ByteBuffer.wrap(data)).toString();
		} catch (Exception e) {
			throw new NotUTF8ConflictException();
		}
	}

	public static Document getWellFormedDocument(Reader reader)
			throws NotWellFormedConflictException, InternalServerErrorException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder parser = factory.newDocumentBuilder();
			return parser.parse(new InputSource(reader));
		} catch (SAXException e) {
			throw new NotWellFormedConflictException();
		} catch (IOException e) {
			throw new InternalServerErrorException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	public static Element getWellFormedDocumentFragment(Reader reader)
			throws NotValidXMLFragmentConflictException,
			InternalServerErrorException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document dummyDocument = parser.parse(new InputSource(reader));
			return dummyDocument.getDocumentElement();
		} catch (SAXException e) {
			throw new NotValidXMLFragmentConflictException();
		} catch (IOException e) {
			throw new InternalServerErrorException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new InternalServerErrorException(e.getMessage());
		}
	}

	public static Reader getUTF8Reader(InputStream is)
			throws NotUTF8ConflictException, InternalServerErrorException {

		try {
			XmlReader reader = new XmlReader(is);
			if (reader.getEncoding().equals("UTF-8")) {
				// encoding ok, return reader
				return reader;
			}
		} catch (Exception e) {
			// no comments, this is to not change XMLReader so we can update it if needed
			if (e.getMessage().startsWith("Invalid encoding")) {
				throw new NotUTF8ConflictException();
			} else {
				throw new InternalServerErrorException(e.getMessage());
			}
		}

		// encoding not ok, throw exception
		throw new NotUTF8ConflictException();
	}

	public static boolean weaklyEquals(String xml1, String xml2) {

		// clean xml1 string
		xml1 = xml1.trim().replaceAll("\n", "").replaceAll("\t", "")
				.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\f", "");

		// clean xml2 string
		xml2 = xml2.trim().replaceAll("\n", "").replaceAll("\t", "")
				.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\f", "");

		return xml1.compareTo(xml2) == 0;

	}
}

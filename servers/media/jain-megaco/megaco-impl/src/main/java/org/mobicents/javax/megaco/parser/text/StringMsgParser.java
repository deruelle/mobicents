package org.mobicents.javax.megaco.parser.text;

import java.text.ParseException;
import java.util.regex.Pattern;

import javax.megaco.CommandEvent;



public class StringMsgParser {

	private String rawStringMessage;
	int index = 0;

	public StringMsgParser() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String msg = " asalnlnd MEGACO/1 [192.168.1.101]:2944";
		StringMsgParser parser = new StringMsgParser();
		try {
			parser.parseMegacoMessage(msg);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public CommandEvent parseMegacoMessage(String msgString) throws ParseException {
		if (msgString == null || msgString.length() == 0)
			return null;

		rawStringMessage = msgString;

		int linestart = index;
		index = msgString.indexOf(TokenNames.MegacopToken);
		if (index == -1) {
			index = msgString.indexOf(TokenNames.a_MegacopToken);
		}

		if (index == -1) {
			throw new ParseException("Message has no MEGACO / ! header", index);
		}

		// As per ABNF definition
		// megacoMessage = LWSP [authenticationHeader SEP ] message;
		// we are interested in message.
		// Cut off LWSP [authenticationHeader SEP ]
		rawStringMessage = rawStringMessage.substring(index, rawStringMessage.length());
		System.out.println(rawStringMessage);
		index = 0;
		decode_MegacopToken();
		decode_SLASH();

		decode_Version();

		decode_SEP();

		decode_domainName();

		System.out.println("index = " + index);

		return null;

	}

	private boolean decode_MegacopToken() {
		// MegacopToken = ("MEGACO" / "!");
		boolean decoded = false;
		decoded = decode_StringValue("MEGACO");
		if (!decoded) {
			decoded = decode_StringValue("!");
		}
		return decoded;
	}

	private boolean decode_SLASH() {
		// SLASH = %x2F ;
		boolean decoded = false;
		decoded = decode_NumericValue("[\\x2F]", 1);
		return decoded;
	}

	private boolean decode_Version() {
		// Version = 1*2(DIGIT);
		boolean decoded = false;
		decoded = decode_DIGIT();
		if (decoded) {
			decode_DIGIT();
		}
		return decoded;
	}

	private boolean decode_mId() {
		// mId = (( domainAddress / domainName )
		// [":" portNumber]) / mtpAddress / deviceName;
		int s = index;
		boolean decoded = false;
		decoded = decode_domainAddress();
		if (!decoded) {
			decoded = decode_domainName();
		}
		if (decoded) {
			char ch = rawStringMessage.charAt(index);
			if (ch == ':') {
				index += 1;
				decoded = decode_portNumber();
			}
		}

		if (!decoded) {
			index = s;

			decoded = decode_mtpAddress();
		}
		return decoded;
	}

	private boolean decode_deviceName() {
		// deviceName = pathNAME;
		boolean decoded = false;
		return decoded;
	}

	private boolean decode_pathNAME() {
		// pathNAME = ["*"] NAME *("/" / "*"/ ALPHA / DIGIT /"_" / "$" )
		// ["@" pathDomainName ];
		boolean decoded = false;
		int s = index;
		char ch = rawStringMessage.charAt(index);
		if (ch == '*') {
			index += 1;
		}

		decoded = decode_NAME();
		if (decoded) {
			boolean f1 = true;
			while (f1) {
				char ch1 = rawStringMessage.charAt(index);
				if (ch1 == '/' || ch1 == '*' || ch1 == '_' || ch1 == '$') {
					index += 1;
					f1 = true;
				}
				if (!f1) {
					f1 = decode_ALPHA();
				}
				if (!f1) {
					f1 = decode_DIGIT();
				}
			}

			f1 = true;
			char ch1 = rawStringMessage.charAt(index);
			if (ch1 == '@') {

			}
		}

		return decoded;
	}

	private boolean decode_pathDomainName() {
		// pathDomainName = (ALPHA / DIGIT / "*" )
		// *63(ALPHA / DIGIT / "-" / "*" / ".");
		boolean decoded = false;
		int s = index;
		decoded = decode_ALPHA();
		if(!decoded){
			decoded = decode_DIGIT();
		}
		if(!decoded){
			//decoded
		}
		return decoded;
	}

	private boolean decode_NAME() {
		// NAME = ALPHA *63(ALPHA / DIGIT / "_" );
		boolean decoded = false;
		int s = index;
		decoded = decode_ALPHA();
		if (decoded) {
			boolean f1 = true;
			for (int i = 0; i < 63 && f1; i++) {
				f1 = decode_ALPHA();
				if (!f1) {
					f1 = decode_DIGIT();
				}
				if (!f1) {
					char ch = rawStringMessage.charAt(index);
					if (ch == '-') {
						index += 1;
						f1 = true;
					}
				}

			}// for loop
		}
		if (!decoded) {
			index = s;
		}
		return decoded;
	}

	private boolean decode_mtpAddress() {
		// mtpAddress = MTPToken LBRKT 4*8 (HEXDIG) RBRKT;

		boolean decoded = false;
		int s = index;
		decoded = decode_StringValue(TokenNames.MTPToken);
		if (decoded) {
			decoded = decode_LBRKT();
			for (int i = 0; i < 4 && decoded; i++) {
				decoded = decode_HEXDIG();
			}
			boolean f1 = decoded;
			for (int i = 4; i < 8 && f1; i++) {
				f1 = decode_HEXDIG();
			}
			if (decoded) {
				decoded = decode_RBRKT();
			}
		}
		if (!decoded) {
			index = s;
		}

		return decoded;
	}

	private boolean decode_HEXDIG() {
		// HEXDIG = ( DIGIT / "A" / "B" / "C" / "D" / "E" / "F" );
		boolean decoded = false;
		decoded = decode_DIGIT();
		if (!decoded) {
			char c = rawStringMessage.charAt(index);
			switch (c) {
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				index += 1;
				decoded = true;
				break;
			default:
				decoded = false;
				break;

			}
		}
		return decoded;
	}

	private boolean decode_LBRKT() {
		// LBRKT = LWSP %x7B LWSP ;
		boolean decoded = false;
		int s = index;
		decoded = decode_LWSP();
		if (decoded) {
			char ch = rawStringMessage.charAt(index);
			if (ch == '{') {
				index += 1;
				decoded = true;
				decoded = decode_LWSP();
			}
		}

		if (!decoded) {
			index = s;
		}
		return decoded;
	}

	private boolean decode_RBRKT() {
		// RBRKT = LWSP %x7D LWSP ;
		boolean decoded = false;
		int s = index;
		decoded = decode_LWSP();
		if (decoded) {
			char ch = rawStringMessage.charAt(index);
			if (ch == '{') {
				index += 1;
				decoded = true;
				decoded = decode_LWSP();
			}
		}

		if (!decoded) {
			index = s;
		}
		return decoded;
	}

	private boolean decode_portNumber() {
		// portNumber = UINT16;
		return decode_UINT16();
	}

	private boolean decode_UINT16() {
		// UINT16 = 1*5(DIGIT) ;
		boolean decoded = false;
		decoded = decode_DIGIT();
		if (decoded) {
			boolean f1 = true;
			for (int i = 1; i < 5 && f1; i++) {
				f1 = decode_DIGIT();
			}
		}
		return decoded;
	}

	private boolean decode_domainAddress() {
		// domainAddress = "[" (IPv4address / IPv6address) "]";
		// TODO : Add decode for IPV6

		boolean decoded = false;
		int startIndex = index;
		int addressStart = 0;
		int addressEnd = 0;
		int count = 0;
		String ipAddress = null;
		if (rawStringMessage.charAt(index) == '[') {
			index += 1;
			addressStart = index;

			while (rawStringMessage.charAt(index) != ']') {
				if (count == 15) {
					index = startIndex;
					decoded = false;
					return decoded;
				}
				index += 1;
				count += 1;
				addressEnd = index;
			}
			index += 1;
			ipAddress = rawStringMessage.substring(addressStart, addressEnd);
			System.out.println(ipAddress);
		}

		return decoded;

	}

	private boolean decode_domainName() {
		// domainName = "<" (ALPHA / DIGIT) *63(ALPHA / DIGIT / "-" /
		// ".") ">";
		int s = index;
		boolean decoded = false;
		int domainStart = 0;
		int domainEnd = 0;
		String domainName = null;

		if (rawStringMessage.charAt(index) == '<') {
			index += 1;
			domainStart = index;
			decoded = decode_ALPHA();
			if (!decoded) {
				decoded = decode_DIGIT();
			}

			if (decoded) {
				boolean f1 = true;
				for (int i = 0; i < 63 && f1; i++) {
					f1 = decode_ALPHA();
					if (!f1) {
						f1 = decode_DIGIT();
					}
					if (!f1) {
						char ch = rawStringMessage.charAt(index);
						if (ch == '-') {
							index += 1;
							f1 = true;
						}
					}
					if (!f1) {
						char ch = rawStringMessage.charAt(index);
						if (ch == '.') {
							index += 1;
							f1 = true;
						}
					}

				}// for loop

				char ch = rawStringMessage.charAt(index);
				if (ch == '>') {
					domainEnd = index;
					index += 1;
					decoded = true;
					domainName = rawStringMessage.substring(domainStart, domainEnd);
					System.out.println(domainName);
				} else {
					decoded = false;
					index = s;
				}
			}
		}
		return decoded;
	}

	private boolean decode_IPv4address() {
		// IPv4address = V4hex DOT V4hex DOT V4hex DOT V4hex;
		// V4hex = 1*3(DIGIT) ;
		boolean decoded = false;
		String IPv4 = null;
		char c = ' ';

		while (c != '.') {
			c = rawStringMessage.charAt(index);
			IPv4 += c;
			index += 1;
		}
		index += 1;

		return decoded;
	}

	private boolean decode_WSP() {
		// WSP = SP / HTAB ;
		boolean decoded = false;
		decoded = decode_SP();
		if (!decoded) {
			decoded = decode_HTAB();
		}
		return decoded;
	}

	private boolean decode_SP() {
		// ABNF definition
		// SP = %x20 ;
		boolean decoded = false;
		decoded = decode_NumericValue("[\\x20]", 1); // Decode Space
		return decoded;
	}

	private boolean decode_HTAB() {
		// ABNF definition
		// HTAB = %x09 ;
		boolean decoded = false;
		decoded = decode_NumericValue("[\\x09]", 1);
		return decoded;
	}

	private boolean decode_EOL() {
		// EOL = (CR [LF] / LF );;
		boolean decoded = false;

		decoded = decode_CR();
		if (decoded) {
			decoded = decode_LF();
			return decoded;
		}

		decoded = decode_LF();
		return decoded;
	}

	private boolean decode_CR() {
		// CR = %x0D ;
		boolean decoded = false;
		decoded = decode_NumericValue("[\\x0D]", 1);
		return decoded;
	}

	private boolean decode_LF() {
		// LF = %x0A ;
		boolean decoded = false;
		decoded = decode_NumericValue("[\\x0A]", 1);
		return decoded;
	}

	private boolean decode_SEP() {
		// SEP = ( WSP / EOL / COMMENT) LWSP;
		boolean decoded = false;

		decoded = decode_WSP();
		if (!decoded) {
			decoded = decode_EOL();
		}
		if (!decoded) {
			decoded = decode_COMMENT();
		}

		if (decoded) {
			decoded = decode_LWSP();
		}

		return decoded;
	}

	private boolean decode_LWSP() {
		// LWSP = *( WSP / COMMENT / EOL );
		boolean decoded = false;
		boolean f1 = true;
		while (f1) {
			decoded = decode_WSP();
			if (!decoded) {
				decoded = decode_COMMENT();
			}
			if (!decoded) {
				decoded = decode_EOL();
			}
			f1 = decoded;
		}
		return true;
	}

	private boolean decode_COMMENT() {
		// COMMENT = ";" *(SafeChar/ RestChar / WSP / %x22) EOL;
		boolean decoded = false;
		int s = index;
		char c = rawStringMessage.charAt(index);
		if (c == ';') {
			decoded = true;
			index += 1;
			boolean f1 = true;
			while (f1) {
				decoded = decode_SafeChar();
				if (!decoded) {
					decoded = decode_RestChar();
				}
				if (!decoded) {
					decoded = decode_WSP();
				}
				if (!decoded) {
					decoded = decode_NumericValue("[\\x22]", 1);
				}
				f1 = decoded;
			}
			decoded = decode_EOL();
		}
		if (!decoded) {
			index = s;
		}

		return decoded;
	}

	private boolean decode_SafeChar() {
		// SafeChar = DIGIT / ALPHA / "+" / "-" / "&" /
		// "!" / "_" / "/" / "\'" / "?" / "@" /
		// "^" / "`" / "~" / "*" / "$" / "\" /
		// "(" / ")" / "%" / "|" / ".";
		boolean decoded = false;
		decoded = decode_DIGIT();
		if (!decoded) {
			decoded = decode_ALPHA();
		}
		if (!decoded) {
			char c = rawStringMessage.charAt(index);
			if (c == '+' || c == '-' || c == '&' || c == '!' || c == '_' || c == '/' || c == '\'' || c == '?'
					|| c == '@' || c == '^' || c == '`' || c == '~' || c == '*' || c == '$' || c == '\\' || c == '('
					|| c == ')' || c == '%' || c == '|' || c == '.') {
				index += 1;
				decoded = true;
			}
		}
		return decoded;
	}

	private boolean decode_RestChar() {
		// RestChar = ";" / "[" / "]" / "{" / "}" / ":" / "," / "#" /
		// "<" / ">" / "=";
		boolean decoded = false;
		char c = rawStringMessage.charAt(index);
		if (c == ';' || c == '[' || c == ']' || c == '{' || c == '}' || c == ':' || c == ',' || c == '#' || c == '<'
				|| c == '>' || c == '=') {
			index += 1;
			decoded = true;
		}
		return decoded;
	}

	private boolean decode_DIGIT() {
		// DIGIT = %x30-39 ;;
		boolean decoded = false;
		char ch = rawStringMessage.charAt(index);
		if (ch <= 127) {
			if (ch >= '0' && ch <= '9') {
				index += 1;
				decoded = true;
			}
		}
		return decoded;
	}

	private boolean decode_ALPHA() {
		// ALPHA = %x41-5A / %x61-7A ;
		boolean decoded = false;
		char ch = rawStringMessage.charAt(index);
		if (ch <= 127) {
			if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
				index += 1;
				decoded = true;
			}
		}
		// TODO : Handle Basic Multilingual Plane ?
		return decoded;
	}

	private boolean decode_StringValue(String regex) {

		boolean decoded = false;
		int start = index;

		try {
			String value = rawStringMessage.substring(index, index + regex.length());
			if ((decoded = value.equalsIgnoreCase(regex))) {
				index += regex.length();
				decoded = true;
			}
		} catch (IndexOutOfBoundsException e) {
			decoded = false;
		}

		return decoded;
	}

	private boolean decode_NumericValue(String regex, int length) {

		boolean decoded = false;

		try {
			String value = rawStringMessage.substring(index, index + length);
			if ((decoded = Pattern.matches(regex, value))) {
				index += length;
				decoded = true;
			}
		} catch (IndexOutOfBoundsException e) {
			decoded = false;
		}

		return decoded;

	}

}

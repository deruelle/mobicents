/*
 * File Name     : Utils.java
 *
 * The JAIN MGCP API implementaion.
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */
package org.mobicents.mgcp.stack.parser;

import jain.protocol.ip.mgcp.message.parms.Bandwidth;
import jain.protocol.ip.mgcp.message.parms.BearerInformation;
import jain.protocol.ip.mgcp.message.parms.CapabilityValue;
import jain.protocol.ip.mgcp.message.parms.CompressionAlgorithm;
import jain.protocol.ip.mgcp.message.parms.ConnectionIdentifier;
import jain.protocol.ip.mgcp.message.parms.ConnectionMode;
import jain.protocol.ip.mgcp.message.parms.ConnectionParm;
import jain.protocol.ip.mgcp.message.parms.DigitMap;
import jain.protocol.ip.mgcp.message.parms.EchoCancellation;
import jain.protocol.ip.mgcp.message.parms.EmbeddedRequest;
import jain.protocol.ip.mgcp.message.parms.EncryptionMethod;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.ExtendedConnectionParm;
import jain.protocol.ip.mgcp.message.parms.GainControl;
import jain.protocol.ip.mgcp.message.parms.InfoCode;
import jain.protocol.ip.mgcp.message.parms.LocalOptVal;
import jain.protocol.ip.mgcp.message.parms.LocalOptionExtension;
import jain.protocol.ip.mgcp.message.parms.LocalOptionValue;
import jain.protocol.ip.mgcp.message.parms.NotificationRequestParms;
import jain.protocol.ip.mgcp.message.parms.NotifiedEntity;
import jain.protocol.ip.mgcp.message.parms.PacketizationPeriod;
import jain.protocol.ip.mgcp.message.parms.ReasonCode;
import jain.protocol.ip.mgcp.message.parms.RegularConnectionParm;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ResourceReservation;
import jain.protocol.ip.mgcp.message.parms.RestartMethod;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.message.parms.SilenceSuppression;
import jain.protocol.ip.mgcp.message.parms.SupportedModes;
import jain.protocol.ip.mgcp.message.parms.SupportedPackages;
import jain.protocol.ip.mgcp.message.parms.TypeOfNetwork;
import jain.protocol.ip.mgcp.message.parms.TypeOfService;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Oleg Kulikov
 * @author Pavel Mitrenko
 * @author Amit Bhayani
 */
public class Utils {

	/** Creates a new instance of Utils */
	public Utils() {
	}

	/**
	 * Create ConnectionMode object from given text value.
	 * 
	 * @param mode
	 *            the text value of the connection mode.
	 * @return the ConnectionMode object.
	 */
	public ConnectionMode decodeConnectionMode(String mode) {
		if (mode.equals("sendrecv")) {
			return ConnectionMode.SendRecv;
		} else if (mode.equalsIgnoreCase("sendonly")) {
			return ConnectionMode.SendOnly;
		} else if (mode.equalsIgnoreCase("recvonly")) {
			return ConnectionMode.RecvOnly;
		} else if (mode.equalsIgnoreCase("confrnce")) {
			return ConnectionMode.Confrnce;
		} else if (mode.equalsIgnoreCase("conttest")) {
			return ConnectionMode.Conttest;
		} else if (mode.equalsIgnoreCase("data")) {
			return ConnectionMode.Data;
		} else if (mode.equalsIgnoreCase("loopback")) {
			return ConnectionMode.Loopback;
		} else if (mode.equalsIgnoreCase("netwloop")) {
			return ConnectionMode.Netwloop;
		} else if (mode.equalsIgnoreCase("netwtest")) {
			return ConnectionMode.Netwtest;
		}
		return ConnectionMode.Inactive;
	}

	public String encodeBearerInformation(BearerInformation bearerInformation) {
		String s = "e:";

		if (BearerInformation.ENC_METHOD_A_LAW == bearerInformation.getEncodingMethod()) {
			s += "A";
		} else if (BearerInformation.ENC_METHOD_MU_LAW == bearerInformation.getEncodingMethod()) {
			s += "mu";
		}
		return s;
	}

	/**
	 * Create BearerInformation object from given text.
	 * 
	 * @param text
	 *            the text value of the object.
	 * @return BearerInformation object.
	 */
	public BearerInformation decodeBearerInformation(String text) throws ParseException {
		// BearerInformation =BearerAttribute 0*(","0*(WSP)BearerAttribute)
		// BearerAttribute =("e"":"BearerEncoding)
		// (BearerExtensionName [":"BearerExtensionValue ])
		// BearerExtensionName =PackageLCOExtensionName
		// BearerExtensionValue =LocalOptionExtensionValue
		// BearerEncoding ="A"/"mu"

		text = text.toLowerCase();
		if (!text.startsWith("e:")) {
			throw new ParseException("Bearer extensions not supported", 0);
		}

		text = text.substring(text.indexOf(":") + 1).trim();

		if (text.equals("a")) {
			return BearerInformation.EncMethod_A_Law;
		} else if (text.equals("mu")) {
			return BearerInformation.EncMethod_mu_Law;
		} else {
			throw new ParseException("Unknown value for BearerInformation: " + text, 0);
		}
	}

	/**
	 * Create local connection options from given text.
	 * 
	 * @param text
	 *            the text value.
	 * @return array of LocalOptionValue objects.
	 */
	public LocalOptionValue[] decodeLocalOptionValueList(String text) throws ParseException {
		// LocalConnectionOptions =LocalOptionValue 0*(WSP)
		// 0*(","0*(WSP)LocalOptionValue 0*(WSP))

		String[] tokens = text.split(",");
		LocalOptionValue[] options = new LocalOptionValue[tokens.length];

		for (int i = 0; i < tokens.length; i++) {
			options[i] = decodeLocalOptionValue(tokens[i]);
		}

		return options;
	}

	/**
	 * Create local connection object from given text.
	 * 
	 * @param text
	 *            the text value of the LocalOptionValue object.
	 * @return LocalOption object.
	 */
	public LocalOptionValue decodeLocalOptionValue(String text) throws ParseException {
		// LocalOptionValue =("p"":"packetizationPeriod)
		// /("a"":"compressionAlgorithm)
		// /("b"":"bandwidth)
		// /("e"":"echoCancellation)
		// /("gc"":"gainControl)
		// /("s"":"silenceSuppression)
		// /("t"":"typeOfService)
		// /("r"":"resourceReservation)
		// /("k"":"encryptiondata)
		// /("nt"":"(typeOfNetwork /
		// supportedTypeOfNetwork))
		// /(LocalOptionExtensionName
		// [":"LocalOptionExtensionValue ])

		int pos = text.indexOf(':');
		if (pos < 0) {
			throw new ParseException("Could not parse local connection option: " + text, 0);
		}

		String name = text.substring(0, pos).trim();
		String value = text.substring(pos + 1).trim();

		if (name.equalsIgnoreCase("a")) {
			return decodeCompressionAlgorithm(value);
		} else if (name.equalsIgnoreCase("p")) {
			return decodePacketizationPeriod(value);
		} else if (name.equalsIgnoreCase("b")) {
			return decodeBandwidth(value);
		} else if (name.equalsIgnoreCase("e")) {
			return decodeEchoCancellation(value);
		} else if (name.equalsIgnoreCase("gc")) {
			return decodeGainControl(value);
		} else if (name.equalsIgnoreCase("s")) {
			return decodeSilenceSuppression(value);
		} else if (name.equalsIgnoreCase("t")) {
			return decodeTypeOfService(value);
		} else if (name.equalsIgnoreCase("r")) {
			return decodeResourceReservation(value);
		} else if (name.equalsIgnoreCase("k")) {
			return decodeEncryptionMethod(value);
		} else if (name.equalsIgnoreCase("nt")) {
			return decodeTypeOfNetwork(value);
		} else {
			return new LocalOptionExtension(name, value);
		}

	}

	public String encodeLocalOptionVale(LocalOptionValue localOptionValue) {
		String s = "";
		switch (localOptionValue.getLocalOptionValueType()) {
		case LocalOptionValue.BANDWIDTH:
			Bandwidth b = (Bandwidth) localOptionValue;
			s += "b:" + encodeBandwidth(b);
			break;

		case LocalOptionValue.COMPRESSION_ALGORITHM:
			CompressionAlgorithm compressionAlgorithm = (CompressionAlgorithm) localOptionValue;
			s += "a:" + encodeCompressionAlgorithm(compressionAlgorithm);
			break;

		case LocalOptionValue.ECHO_CANCELLATION:
			EchoCancellation echoCancellation = (EchoCancellation) localOptionValue;
			s += "e:" + encodeEchoCancellation(echoCancellation);
			break;

		case LocalOptionValue.ENCRYPTION_METHOD:
			EncryptionMethod encryptionMethod = (EncryptionMethod) localOptionValue;
			s += "k:" + encodeEncryptionMethod(encryptionMethod);
			break;

		case LocalOptionValue.GAIN_CONTROL:
			GainControl gainControl = (GainControl) localOptionValue;
			s += "gc:" + encodeGainControl(gainControl);
			break;

		case LocalOptionValue.LOCAL_OPTION_EXTENSION:
			LocalOptionExtension localOptionExtension = (LocalOptionExtension) localOptionValue;
			s += localOptionExtension.getLocalOptionExtensionName() + ":"
					+ localOptionExtension.getLocalOptionExtensionValue();
			break;

		case LocalOptionValue.PACKETIZATION_PERIOD:
			PacketizationPeriod packetizationPeriod = (PacketizationPeriod) localOptionValue;
			s += "p:" + encodePacketizationPeriod(packetizationPeriod);
			break;

		case LocalOptionValue.RESOURCE_RESERVATION:
			s += "r:";
			ResourceReservation resourceReservation = (ResourceReservation) localOptionValue;
			s += encodeResourceReservation(resourceReservation);
			break;

		case LocalOptionValue.SILENCE_SUPPRESSION:
			s += "s:";
			SilenceSuppression silenceSuppression = (SilenceSuppression) localOptionValue;
			s += encodeSilenceSuppression(silenceSuppression);
			break;

		case LocalOptionValue.TYPE_OF_NETWORK:
			s += "nt:";
			TypeOfNetwork typeOfNetwork = (TypeOfNetwork) localOptionValue;
			s += encodeTypeOfNetwork(typeOfNetwork);
			break;

		case LocalOptionValue.TYPE_OF_SERVICE:
			TypeOfService typeOfService = (TypeOfService) localOptionValue;
			s += "t:" + encodeTypeOfService(typeOfService);
			break;

		default:
			System.out.println("LocalOptionValue " + localOptionValue + " not identified");
			break;
		}

		return s;
	}

	/**
	 * Create CompressionAlgorithm object from given text.
	 * 
	 * @param text
	 *            the text value of the compression algoritm.
	 * @return CompressionAlgorithm object.
	 */
	public CompressionAlgorithm decodeCompressionAlgorithm(String value) throws ParseException {
		// compressionAlgorithm =algorithmName 0*(";"algorithmName)
		return new CompressionAlgorithm(value.split(";"));
	}

	public String encodeCompressionAlgorithm(CompressionAlgorithm compressionAlgorithm) {
		String s = "";
		String[] names = compressionAlgorithm.getCompressionAlgorithmNames();
		boolean first = true;
		for (int i = 0; i < names.length; i++) {
			if (first) {
				first = false;
			} else {
				s += ";";
			}
			s += names[i];
		}
		return s;
	}

	/**
	 * Create PacketizationPeriod object from given text.
	 * 
	 * @param text
	 *            the text view of the PacketizationPeriod object.
	 * @return PacketizationPeriod object.
	 */
	public PacketizationPeriod decodePacketizationPeriod(String value) throws ParseException {
		// packetizationPeriod =1*4(DIGIT)["-"1*4(DIGIT)]
		int pos = value.indexOf('-');
		if (pos < 0) {
			try {
				return new PacketizationPeriod(Integer.parseInt(value));
			} catch (Exception e) {
				throw new ParseException("Invalid packetization period:" + value, 0);
			}
		} else {
			String low = value.substring(0, pos).trim();
			String hight = value.substring(pos + 1).trim();

			try {
				return new PacketizationPeriod(Integer.parseInt(low), Integer.parseInt(hight));
			} catch (Exception e) {
				throw new ParseException("Invalid packetization period:" + value, 0);
			}
		}
	}

	public String encodePacketizationPeriod(PacketizationPeriod packetizationPeriod) {
		String s = "";
		if (packetizationPeriod.getPacketizationPeriodLowerBound() != packetizationPeriod
				.getPacketizationPeriodUpperBound()) {
			s += packetizationPeriod.getPacketizationPeriodLowerBound() + "-"
					+ packetizationPeriod.getPacketizationPeriodUpperBound();
		} else {
			s += packetizationPeriod.getPacketizationPeriodLowerBound();
		}
		return s;
	}

	/**
	 * Create Bandwidth object from given text.
	 * 
	 * @param text
	 *            the text view of the Bandwidth object.
	 * @return Bandwidth object.
	 */
	public Bandwidth decodeBandwidth(String value) throws ParseException {
		// bandwidth =1*4(DIGIT)["-"1*4(DIGIT)]
		int pos = value.indexOf('-');
		if (pos < 0) {
			try {
				return new Bandwidth(Integer.parseInt(value));
			} catch (Exception e) {
				throw new ParseException("Invalid packetization period:" + value, 0);
			}
		} else {
			String low = value.substring(0, pos).trim();
			String hight = value.substring(pos + 1).trim();

			try {
				return new Bandwidth(Integer.parseInt(low), Integer.parseInt(hight));
			} catch (Exception e) {
				throw new ParseException("Invalid packetization period:" + value, 0);
			}
		}
	}

	public String encodeBandwidth(Bandwidth bandwidth) {
		String s = "";
		if (bandwidth.getBandwidthLowerBound() != bandwidth.getBandwidthUpperBound()) {
			s += bandwidth.getBandwidthLowerBound() + "-" + bandwidth.getBandwidthUpperBound();
		} else {
			s += bandwidth.getBandwidthLowerBound();
		}
		return s;
	}

	/**
	 * Decode EchoCancellation object from given text.
	 * 
	 * @param text
	 *            the text value of the EchoCancellation.
	 * @return EchoCancellation object.
	 */
	public EchoCancellation decodeEchoCancellation(String value) throws ParseException {
		// echoCancellation ="on"/"off"
		if (value.equalsIgnoreCase("on")) {
			return EchoCancellation.EchoCancellationOn;
		} else if (value.equalsIgnoreCase("of")) {
			return EchoCancellation.EchoCancellationOff;
		} else {
			throw new ParseException("Invalid value for EchoCancellation :" + value, 0);
		}
	}

	public String encodeEchoCancellation(EchoCancellation echoCancellation) {
		String s = "";
		if (echoCancellation.getEchoCancellation()) {
			s += "on";
		} else {
			s += "off";
		}
		return s;
	}

	/**
	 * Decode GainControl object from given text.
	 * 
	 * @param text
	 *            the text value of the GainControl.
	 * @return GainControl object.
	 */
	public GainControl decodeGainControl(String value) throws ParseException {
		// gainControl ="auto"/["-"] 1**4(DIGIT)
		if (value.equalsIgnoreCase("auto")) {
			return new GainControl();
		} else {
			try {
				return new GainControl(Integer.parseInt(value));
			} catch (Exception e) {
				throw new ParseException("Invalid value for EchoCancellation :" + value, 0);
			}
		}
	}

	public String encodeGainControl(GainControl gainControl) {
		String s = "";
		if (gainControl.getGainControl() == 0) {
			s += "auto";
		} else {
			s += gainControl.getGainControl();
		}
		return s;
	}

	/**
	 * Decode SilenceSuppression object from given text.
	 * 
	 * @param text
	 *            the text value of the SilenceSuppression.
	 * @return SilenceSuppression object.
	 */
	public SilenceSuppression decodeSilenceSuppression(String value) throws ParseException {
		// silenceSuppression ="on"/"off"
		if (value.equalsIgnoreCase("on")) {
			return SilenceSuppression.SilenceSuppressionOn;
		} else if (value.equalsIgnoreCase("off")) {
			return SilenceSuppression.SilenceSuppressionOff;
		} else {
			throw new ParseException("Invalid value for SilenceSuppression :" + value, 0);
		}
	}

	public String encodeSilenceSuppression(SilenceSuppression silenceSuppression) {
		String s = "";
		if (silenceSuppression.getSilenceSuppression()) {
			s += "on";
		} else {
			s += "off";
		}
		return s;
	}

	/**
	 * Decode TypeOfService object from given text.
	 * 
	 * @param text
	 *            the text value of the TypeOfService.
	 * @return TypeOfService object.
	 */
	public TypeOfService decodeTypeOfService(String value) throws ParseException {
		// typeOfService =1*2(HEXDIG);1 hex only for capabilities
		try {
			return new TypeOfService((byte) Integer.parseInt(value));
		} catch (Exception e) {
			throw new ParseException("Invalid value for TypeOfService :" + value, 0);
		}
	}

	public String encodeTypeOfService(TypeOfService typeOfService) {
		String s = "";
		s += Integer.toString(typeOfService.getTypeOfService(), 16).toUpperCase();
		return s;
	}

	/**
	 * Decode ResourceReservation object from given text.
	 * 
	 * @param text
	 *            the text value of the ResourceReservation.
	 * @return ResourceReservation object.
	 */
	public ResourceReservation decodeResourceReservation(String value) throws ParseException {
		// resourceReservation ="g"/"cl"/"be"
		if (value.equalsIgnoreCase("g")) {
			return ResourceReservation.Guaranteed;
		} else if (value.equalsIgnoreCase("cl")) {
			return ResourceReservation.ControlledLoad;
		} else if (value.equalsIgnoreCase("be")) {
			return ResourceReservation.BestEffort;
		} else {
			throw new ParseException("Invalid value for EchoCancellation :" + value, 0);
		}
	}

	public String encodeResourceReservation(ResourceReservation resourceReservation) {
		String s = "";
		switch (resourceReservation.getResourceReservation()) {
		case ResourceReservation.BEST_EFFORT:
			s += "be";
			break;

		case ResourceReservation.CONTROLLED_LOAD:
			s += "cl";
			break;

		case ResourceReservation.GUARANTEED:
			s += "g";
			break;
		}
		return s;
	}

	/**
	 * Decode EncryptionMethod object from given text.
	 * 
	 * @param text
	 *            the text value of the EncryptionMethod.
	 * @return EncryptionMethod object.
	 */
	public EncryptionMethod decodeEncryptionMethod(String value) throws ParseException {
		// encryptiondata =("clear"":"encryptionKey )
		// /("base64"":"encodedEncryptionKey )
		// /("uri"":"URItoObtainKey )
		// /("prompt");defined in SDP,not usable in MGCP!
		int pos = value.indexOf(':');
		if (pos < 0) {
			throw new ParseException("Invalid value for EncryptionData: " + value, 0);
		}

		String method = value.substring(0, pos).trim();
		String key = value.substring(pos + 1).trim();

		if (method.equalsIgnoreCase("clear")) {
			return new EncryptionMethod(EncryptionMethod.CLEAR, key);
		} else if (method.equalsIgnoreCase("base64")) {
			return new EncryptionMethod(EncryptionMethod.BASE64, key);
		} else if (method.equalsIgnoreCase("uri")) {
			return new EncryptionMethod(EncryptionMethod.URI, key);
		} else {
			throw new ParseException("Invalid value for EncryptionData: " + method, 0);
		}
	}

	public String encodeEncryptionMethod(EncryptionMethod encryptionMethod) {
		String s = "";
		switch (encryptionMethod.getEncryptionMethod()) {
		case EncryptionMethod.BASE64:
			s += "base64:" + encryptionMethod.getEncryptionKey();
			break;

		case EncryptionMethod.CLEAR:
			s += "clear:" + encryptionMethod.getEncryptionKey();
			break;

		case EncryptionMethod.URI:
			s += "uri:" + encryptionMethod.getEncryptionKey();
			break;
		}
		return s;
	}

	/**
	 * Decode TypeOfNetwork object from given text.
	 * 
	 * @param text
	 *            the text value of the TypeOfNetwork.
	 * @return TypeOfNetwork object.
	 */
	public TypeOfNetwork decodeTypeOfNetwork(String value) throws ParseException {
		// typeOfNetwork ="IN"/"ATM"/"LOCAL"/OtherTypeOfNetwork
		if (value.equalsIgnoreCase("in")) {
			return TypeOfNetwork.In;
		} else if (value.equalsIgnoreCase("atm")) {
			return TypeOfNetwork.Atm;
		} else if (value.equalsIgnoreCase("local")) {
			return TypeOfNetwork.Local;
		} else {
			throw new ParseException("Invalid value for TypeOfNetwork :" + value, 0);
		}
	}

	public String encodeTypeOfNetwork(TypeOfNetwork typeOfNetwork) {
		String s = "";
		switch (typeOfNetwork.getTypeOfNetwork()) {
		case TypeOfNetwork.IN:
			s += "in";
			break;
		case TypeOfNetwork.ATM:
			s += "atm";
			break;
		case TypeOfNetwork.LOCAL:
			s += "local";
			break;
		}
		return s;
	}

	public RestartMethod decodeRestartMethod(String value) throws ParseException {
		// RestartMethod = "graceful" / "forced" / "restart" / "disconnected"
		// / "cancel-graceful" / extensionRestartMethod
		// extensionRestartMethod = PackageExtensionRM
		// PackageExtensionRM = packageName "/" 1*32(ALPHA / DIGIT / HYPHEN)

		if (value.equalsIgnoreCase("graceful")) {
			return RestartMethod.Graceful;
		} else if (value.equalsIgnoreCase("forced")) {
			return RestartMethod.Forced;
		} else if (value.equalsIgnoreCase("restart")) {
			return RestartMethod.Restart;
		} else if (value.equalsIgnoreCase("disconnected")) {
			return RestartMethod.Disconnected;
		} else if (value.equalsIgnoreCase("cancel-grateful")) {
			return RestartMethod.CancelGraceful;
		} else {
			// TODO: Add support for extension restart method.
			throw new ParseException("Extension restarts not (yet) supported:" + value, 0);
		}
	}

	public RequestedEvent[] decodeRequestedEventList(String value) throws ParseException {
		// RequestedEvents =requestedEvent 0*(","0*(WSP)requestedEvent)
		String[] tokens = value.split(",");
		RequestedEvent[] events = new RequestedEvent[tokens.length];

		for (int i = 0; i < tokens.length; i++) {
			events[i] = decodeRequestedEvent(tokens[i]);
		}

		return events;
	}

	public RequestedEvent decodeRequestedEvent(String value) throws ParseException {
		// requestedEvent =(eventName ["("requestedActions ")"])
		// /(eventName "("requestedActions ")" "("eventParameters ")")

		// Replace all space, tabs
		value = value.replaceAll("\\s", "");
		int pos1 = value.indexOf('(');
		// int pos2 = value.indexOf(')', pos1);
		// int pos3 = value.indexOf('(', pos2);
		// int pos4 = value.indexOf(')', pos3);

		if (pos1 == -1) { // no actions, no parameters
			// TODO: RFC3435 : 3.2.2.16 :When no action is specified, the
			// default action is to notify the event. This should be taken care
			// by MGW or N action should be added here by default?
			return new RequestedEvent(decodeEventName(value, null), null);
		}

		String evtName = value.substring(0, pos1);
		value = value.substring(pos1, value.length());

		String REGEX_EMBEDDED_COMMAND = "\\(E\\(.*\\)\\)";
		Pattern pattern = Pattern.compile(REGEX_EMBEDDED_COMMAND);

		Matcher matcher = pattern.matcher(value);

		if ((matcher.find())) {
			// This is Embedded Notification Request

			// TODO : We assumed that Embedded Notification Request E will not
			// be
			// combined with any other Action N, A, D, S, I or K
			String embeddedNotificatonRequest = value.substring(3, value.length() - 2);
			EmbeddedRequest embeddedRequest = decodeEmbeddedRequest(embeddedNotificatonRequest);
			RequestedAction[] actions = new RequestedAction[] { new RequestedAction(embeddedRequest) };
			return new RequestedEvent(decodeEventName(evtName, null), actions);
		}

		int pos2 = value.indexOf(')');
		int pos3 = value.indexOf('(', pos2);

		if (pos3 == -1) {
			value = value.substring(1, pos2);
			return new RequestedEvent(decodeEventName(evtName, null), decodeRequestedActions(value));
		}

		int pos4 = value.indexOf(')', pos3);
		String parms = value.substring(pos3 + 1, pos4);
		value = value.substring(1, pos2);

		return new RequestedEvent(decodeEventName(evtName, parms), decodeRequestedActions(value));

	}

	public EventName decodeEventName(String value, String param) throws ParseException {
		// eventName =[(packageName /"*")"/"]
		// (eventId /"all"/eventRange
		// /"*"/"#");for DTMF
		// ["@"(ConnectionId /"$"/"*")]
		String tokens[] = value.split("/");
		if (tokens.length == 1) {
			return new EventName(PackageName.AllPackages, MgcpEvent.factory((tokens[0]).trim()).withParm(param));
		} else if (tokens.length == 2) {
			int pos = tokens[1].indexOf('@');
			if (pos > 0) {
				String cid = (tokens[1].substring(pos + 1)).trim();
				ConnectionIdentifier connectionIdentifier = null;
				if ((ConnectionIdentifier.AnyConnection).toString().equals(cid)) {
					connectionIdentifier = ConnectionIdentifier.AnyConnection;
				} else if ((ConnectionIdentifier.AllConnections).toString().equals(cid)) {
					connectionIdentifier = ConnectionIdentifier.AllConnections;
				} else {
					connectionIdentifier = new ConnectionIdentifier(cid);
				}
				return new EventName(PackageName.factory((tokens[0]).trim()), MgcpEvent.factory(
						((tokens[1]).trim()).substring(0, pos)).withParm(param), connectionIdentifier);
			} else {
				return new EventName(PackageName.factory(tokens[0].trim()), MgcpEvent.factory(tokens[1].trim())
						.withParm(param));
			}
		} else if (tokens.length == 3) {
			int pos = tokens[2].indexOf('@');
			if (pos < 0) {
				throw new ParseException("Invalid token " + tokens[2], 0);
			}

			String cid = (tokens[1].trim()).substring(pos + 1);
			return new EventName(PackageName.factory(tokens[0].trim()), MgcpEvent.factory(
					(tokens[1].trim()).substring(0, pos)).withParm(param), new ConnectionIdentifier(cid));
		} else {
			throw new ParseException("Unexpected event name " + value, 0);
		}
	}

	public String encodeInfoCodeList(InfoCode[] infoCodes) {
		String msg = "";
		boolean first = true;
		for (int i = 0; i < infoCodes.length; i++) {

			if (first) {
				first = false;
			} else {
				msg += ',';
			}

			InfoCode infoCode = infoCodes[i];
			switch (infoCode.getInfoCode()) {
			case (InfoCode.BEARER_INFORMATION):
				msg += "B";
				break;

			case (InfoCode.CALL_IDENTIFIER):
				msg += "C";
				break;

			case (InfoCode.CONNECTION_IDENTIFIER):
				msg += "I";
				break;

			case (InfoCode.NOTIFIED_ENTITY):
				msg += "N";
				break;

			case (InfoCode.REQUEST_IDENTIFIER):
				msg += "X";
				break;

			case (InfoCode.LOCAL_CONNECTION_OPTIONS):
				msg += "L";
				break;

			case (InfoCode.CONNECTION_MODE):
				msg += "M";
				break;

			case (InfoCode.REQUESTED_EVENTS):
				msg += "R";
				break;

			case (InfoCode.SIGNAL_REQUESTS):
				msg += "S";
				break;

			case (InfoCode.DIGIT_MAP):
				msg += "D";
				break;

			case (InfoCode.OBSERVED_EVENTS):
				msg += "O";
				break;

			case (InfoCode.CONNECTION_PARAMETERS):
				msg += "P";
				break;

			case (InfoCode.REASON_CODE):
				msg += "E";
				break;

			case (InfoCode.SPECIFIC_ENDPOINT_ID):
				msg += "Z";
				break;

			case (InfoCode.QUARANTINE_HANDLING):
				msg += "Q";
				break;

			case (InfoCode.DETECT_EVENTS):
				msg += "T";
				break;

			case (InfoCode.REMOTE_CONNECTION_DESCRIPTOR):
				msg += "RC";
				break;

			case (InfoCode.LOCAL_CONNECTION_DESCRIPTOR):
				msg += "LC";
				break;

			case (InfoCode.CAPABILITIES):
				msg += "A";
				break;

			case (InfoCode.EVENT_STATES):
				msg += "ES";
				break;

			case (InfoCode.RESTART_METHOD):
				msg += "RM";
				break;

			case (InfoCode.RESTART_DELAY):
				msg += "RD";
				break;

			default:
				System.out.println("The InfoCode " + infoCode + " is not supported");
				break;
			}
		}
		return msg;
	}

	public InfoCode[] decodeInfoCodeList(String value) throws ParseException {
		String tokens[] = (value.trim()).split(",");
		InfoCode[] infoCodes = new InfoCode[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			infoCodes[i] = decodeInfoCode(tokens[i]);
		}
		return infoCodes;
	}

	private InfoCode decodeInfoCode(String value) throws ParseException {
		value = value.trim();
		if (value.equalsIgnoreCase("B")) {
			return InfoCode.BearerInformation;
		}

		else if (value.equalsIgnoreCase("C")) {
			return InfoCode.CallIdentifier;
		}

		else if (value.equalsIgnoreCase("I")) {
			return InfoCode.ConnectionIdentifier;
		}

		else if (value.equalsIgnoreCase("N")) {
			return InfoCode.NotifiedEntity;
		}

		else if (value.equalsIgnoreCase("X")) {
			return InfoCode.RequestIdentifier;
		}

		else if (value.equalsIgnoreCase("L")) {
			return InfoCode.LocalConnectionOptions;
		}

		else if (value.equalsIgnoreCase("M")) {
			return InfoCode.ConnectionMode;
		}

		else if (value.equalsIgnoreCase("R")) {
			return InfoCode.RequestedEvents;
		}

		else if (value.equalsIgnoreCase("S")) {
			return InfoCode.SignalRequests;
		}

		else if (value.equalsIgnoreCase("D")) {
			return InfoCode.DigitMap;
		}

		else if (value.equalsIgnoreCase("O")) {
			return InfoCode.ObservedEvents;
		}

		else if (value.equalsIgnoreCase("P")) {
			return InfoCode.ConnectionParameters;
		}

		else if (value.equalsIgnoreCase("E")) {
			return InfoCode.ReasonCode;
		}

		else if (value.equalsIgnoreCase("Z")) {
			return InfoCode.SpecificEndpointID;
		}

		else if (value.equalsIgnoreCase("Q")) {
			return InfoCode.QuarantineHandling;
		}

		else if (value.equalsIgnoreCase("T")) {
			return InfoCode.DetectEvents;
		}

		else if (value.equalsIgnoreCase("RC")) {
			return InfoCode.RemoteConnectionDescriptor;
		}

		else if (value.equalsIgnoreCase("LC")) {
			return InfoCode.LocalConnectionDescriptor;
		}

		else if (value.equalsIgnoreCase("A")) {
			return InfoCode.Capabilities;
		}

		else if (value.equalsIgnoreCase("ES")) {
			return InfoCode.EventStates;
		}

		else if (value.equalsIgnoreCase("RM")) {
			return InfoCode.RestartMethod;
		}

		else if (value.equalsIgnoreCase("RD")) {
			return InfoCode.RestartDelay;
		}

		else {
			throw new ParseException("Extension action not suported", 0);
		}
	}

	public CapabilityValue[] decodeCapabilityList(String value) throws ParseException {
		String tokens[] = (value.trim()).split(",");
		CapabilityValue[] capabilityValues = new CapabilityValue[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			capabilityValues[i] = decodeCapability(tokens[i]);
		}
		return capabilityValues;

	}

	public String encodeCapabilityList(CapabilityValue[] c) {
		String s = "";
		boolean first = true;
		for (int i = 0; i < c.length; i++) {
			if (first) {
				first = false;
			} else {
				s += ",";
			}
			s += encodeCapability(c[i]);
		}
		return s;
	}

	public String encodeCapability(CapabilityValue c) {
		String s = "";

		switch (c.getCapabilityValueType()) {
		case CapabilityValue.LOCAL_OPTION_VALUE:
			LocalOptVal localOptVal = (LocalOptVal) c;
			LocalOptionValue localOptionValue = localOptVal.getLocalOptionValue();
			s += encodeLocalOptionVale(localOptionValue);
			break;

		case CapabilityValue.SUPPORTED_PACKAGES:
			s += "v:";
			SupportedPackages supportedPackages = (SupportedPackages) c;
			PackageName[] packageNameList = supportedPackages.getSupportedPackageNames();
			s += encodePackageNameList(packageNameList);
			break;

		case CapabilityValue.SUPPORTED_MODES:
			s += "m:";
			SupportedModes supportedModes = (SupportedModes) c;
			ConnectionMode[] connectionModeList = supportedModes.getSupportedModes();
			s += encodeConnectionModeList(connectionModeList);
			break;
		}

		return s;
	}

	public String encodeConnectionModeList(ConnectionMode[] connectionModeList) {
		String s = "";
		boolean first = true;
		for (int i = 0; i < connectionModeList.length; i++) {
			if (first) {
				first = false;
			} else {
				s += ";";
			}
			s += connectionModeList[i].toString();

		}
		return s;
	}

	public String encodePackageNameList(PackageName[] packageNameList) {
		String s = "";
		boolean first = true;
		for (int i = 0; i < packageNameList.length; i++) {
			if (first) {
				first = false;
			} else {
				s += ";";
			}
			s += packageNameList[i].toString();

		}
		return s;
	}

	public PackageName[] decodePackageNameList(String value) {
		String[] packages = value.split(";");
		PackageName[] supportedPackageNames = new PackageName[packages.length];
		for (int i = 0; i < packages.length; i++) {
			PackageName p = PackageName.factory(packages[i]);
			supportedPackageNames[i] = p;
		}

		return supportedPackageNames;
	}

	public CapabilityValue decodeCapability(String value) throws ParseException {

		CapabilityValue capabilityValue = null;
		int pos = value.indexOf(':');
		if (pos < 0) {
			throw new ParseException("Invalid value for EncryptionData: " + value, 0);
		}

		String key = value.substring(0, pos).trim();
		String capability = value.substring(pos + 1).trim();

		if ("a".equals(key)) {
			String[] codecs = capability.split(";");
			CompressionAlgorithm compressionAlgorithm = new CompressionAlgorithm(codecs);
			capabilityValue = new LocalOptVal(compressionAlgorithm);

		} else if ("b".equals(key)) {
			String[] bandwidthRange = capability.split("-");
			int lower = Integer.parseInt(bandwidthRange[0]);
			int upper = lower;
			if (bandwidthRange.length == 2) {
				upper = Integer.parseInt(bandwidthRange[1]);
			}

			Bandwidth bandwidth = new Bandwidth(lower, upper);
			capabilityValue = new LocalOptVal(bandwidth);

		} else if ("p".equals(key)) {
			String[] packetizationRange = capability.split("-");
			int lower = Integer.parseInt(packetizationRange[0]);
			int upper = lower;
			if (packetizationRange.length == 2) {
				upper = Integer.parseInt(packetizationRange[1]);
			}

			PacketizationPeriod packetizationPeriod = new PacketizationPeriod(lower, upper);
			capabilityValue = new LocalOptVal(packetizationPeriod);
		} else if ("e".equals(key)) {
			if ("on".equals(capability)) {
				capabilityValue = new LocalOptVal(EchoCancellation.EchoCancellationOn);
			} else {
				capabilityValue = new LocalOptVal(EchoCancellation.EchoCancellationOff);
			}
		} else if ("s".equals(key)) {
			if ("on".equals(capability)) {
				capabilityValue = new LocalOptVal(SilenceSuppression.SilenceSuppressionOn);
			} else {
				capabilityValue = new LocalOptVal(SilenceSuppression.SilenceSuppressionOff);
			}
		} else if ("gc".equals(key)) {
			int gainControl = 0;

			try {
				gainControl = Integer.parseInt(capability);
			} catch (NumberFormatException ne) {
				// Ignore
			}
			capabilityValue = new LocalOptVal(new GainControl(gainControl));

		} else if ("t".equals(key)) {
			byte typeOfService = 0;

			try {
				typeOfService = Byte.parseByte(capability);
			} catch (NumberFormatException ne) {
				// Ignore
			}
			capabilityValue = new LocalOptVal(new TypeOfService(typeOfService));
		} else if ("r".equals(key)) {
			if ("g".equals(capability)) {
				capabilityValue = new LocalOptVal(ResourceReservation.Guaranteed);
			} else if ("cl".equals(capability)) {
				capabilityValue = new LocalOptVal(ResourceReservation.ControlledLoad);
			} else if ("be".equals(capability)) {
				capabilityValue = new LocalOptVal(ResourceReservation.BestEffort);
			}
		} else if ("k".equals(key)) {
			EncryptionMethod encryptionMethod = decodeEncryptionMethod(capability);
			capabilityValue = new LocalOptVal(encryptionMethod);
		} else if ("nt".equals(key)) {
			TypeOfNetwork typeOfNetwork = decodeTypeOfNetwork(capability);
			capabilityValue = new LocalOptVal(typeOfNetwork);
		} else if ("v".equals(key)) {
			PackageName[] supportedPackageNames = decodePackageNameList(capability);
			capabilityValue = new SupportedPackages(supportedPackageNames);
		} else if ("m".equals(key)) {
			String[] modes = capability.split(";");
			ConnectionMode[] supportedConnectionModes = new ConnectionMode[modes.length];
			for (int i = 0; i < modes.length; i++) {
				ConnectionMode c = decodeConnectionMode(modes[i]);
				supportedConnectionModes[i] = c;
			}
			capabilityValue = new SupportedModes(supportedConnectionModes);
		}

		return capabilityValue;
	}

	public RequestedAction[] decodeRequestedActions(String value) throws ParseException {
		// requestedActions =requestedAction 0*(","0*(WSP)requestedAction)
		String tokens[] = value.split(",");
		RequestedAction[] actions = new RequestedAction[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			actions[i] = decodeRequestedAction(tokens[i]);
		}
		return actions;
	}

	public RequestedAction decodeRequestedAction(String value) throws ParseException {
		// requestedAction ="N"/"A"/"D"/"S"/"I"/"K"
		// /"E""("EmbeddedRequest ")"
		// /ExtensionAction
		value = value.trim();
		if (value.equalsIgnoreCase("N")) {
			return RequestedAction.NotifyImmediately;
		} else if (value.equalsIgnoreCase("A")) {
			return RequestedAction.Accumulate;
		} else if (value.equalsIgnoreCase("S")) {
			return RequestedAction.Swap;
		} else if (value.equalsIgnoreCase("I")) {
			return RequestedAction.Ignore;
		} else if (value.equalsIgnoreCase("K")) {
			return RequestedAction.KeepSignalsActive;
		} else if (value.equalsIgnoreCase("D")) {
			return RequestedAction.TreatAccordingToDigitMap;
		} else if (value.equalsIgnoreCase("E")) {
			return new RequestedAction(decodeEmbeddedRequest(value));
		} else if (value.equalsIgnoreCase("X")) {
			// X is RequestIdentifier and we have already taken care of this.
			// Ignore here
			return RequestedAction.Ignore;
		} else {
			throw new ParseException("Extension action not suported", 0);
		}
	}

	private String getEvent(String s) {
		char[] c = s.toCharArray();

		String command = "";
		int start = 0;
		int end = 0;

		int paraenthisis = 0;

		boolean first = true;
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '(') {
				paraenthisis++;
				if (first) {
					first = false;
					start = i;
					command = String.valueOf(c[i - 1]);
				}
			} else if (c[i] == ')') {
				paraenthisis--;
			}

			if (!first && paraenthisis == 0) {
				end = i + 1;
				break;
				// command ends
			}
		}

		// System.out.println("Command = " + command + " Start = " + start + "
		// end = " + end);
		return s.substring(0, end);
	}

	public String encodeEmbeddedRequest(EmbeddedRequest embeddedRequest) {
		String s = "";
		boolean first = true;

		RequestedEvent[] requestedEventList = embeddedRequest.getEmbeddedRequestList();
		if (requestedEventList != null) {
			if (first) {
				first = false;
			} else {
				s += ",";
			}

			s += "R(" + encodeRequestedEvents(requestedEventList) + ")";
		}

		EventName[] eventNameList = embeddedRequest.getEmbeddedSignalRequest();
		if (eventNameList != null) {
			if (first) {
				first = false;
			} else {
				s += ",";
			}
			s += "S(" + encodeEventNames(eventNameList) + ")";
		}
		DigitMap digitMap = embeddedRequest.getEmbeddedDigitMap();
		if (digitMap != null) {
			if (first) {
				first = false;
			} else {
				s += ",";
			}
			s += "D(" + digitMap.toString() + ")";
		}
		return s;
	}

	public EmbeddedRequest decodeEmbeddedRequest(String value) throws ParseException {
		// EmbeddedRequest =("R""("EmbeddedRequestList ")"
		// [","0*(WSP)"S""("EmbeddedSignalRequest ")"]
		// [","0*(WSP)"D""("EmbeddedDigitMap ")"])
		// /("S""("EmbeddedSignalRequest ")"
		// [","0*(WSP)"D""("EmbeddedDigitMap ")"])
		// /("D""("EmbeddedDigitMap ")")

		RequestedEvent[] requestedEvents = null;
		EventName[] signalEvents = null;
		DigitMap digitMap = null;

		while (value.length() > 0) {
			String temp = this.getEvent(value);
			value = value.substring(temp.length(), value.length());

			if (temp.startsWith(",")) {
				temp = temp.substring(1, temp.length());
			}

			if (temp.startsWith("R")) {
				temp = temp.substring(2, temp.length() - 1);
				requestedEvents = decodeRequestedEventList(temp);
			} else if (temp.startsWith("S")) {
				temp = temp.substring(2, temp.length() - 1);
				signalEvents = decodeEventNames(temp);
			} else if (temp.startsWith("D")) {
				temp = temp.substring(2, temp.length() - 1);
				digitMap = new DigitMap(temp);
			}
		}

		return new EmbeddedRequest(requestedEvents, signalEvents, digitMap);
	}

	public EventName[] decodeEventNames(String value) throws ParseException {
		String tokens[] = value.split(",");
		EventName[] events = new EventName[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			String name = null;
			String parm = null;

			int pos = tokens[i].indexOf('(');
			if (pos > 0) {
				name = tokens[i].substring(0, pos);
				parm = tokens[i].substring(pos + 1, tokens[i].length() - 1);
			} else {
				name = tokens[i];
			}
			events[i] = decodeEventName(name, parm);
		}
		return events;
	}

	public String encodeEventNames(EventName[] events) {
		String s = "";
		boolean first = true;
		for (EventName e : events) {
			if (first) {
				first = false;
			} else {
				s += ',';
			}
			s += encodeEventName(e);
		}
		return s;
	}

	public String encodeEventName(EventName e) {
		String s = "";
		s += e.getPackageName().toString() + '/' + e.getEventIdentifier().getName();
		if (e.getConnectionIdentifier() != null) {
			s += '@' + e.getConnectionIdentifier().toString();
		}
		if (e.getEventIdentifier().getParms() != null) {
			s += '(' + e.getEventIdentifier().getParms() + ')';
		}
		return s;
	}

	/**
	 * Creates EndpointIdentifier object from givent endpont's name.
	 * 
	 * @param name
	 *            the name of the given endpoint.
	 * @return EdnpointIdentifier object.
	 */
	public EndpointIdentifier decodeEndpointIdentifier(String name) {
		String[] s = name.split("@");
		return new EndpointIdentifier(s[0], s[1]);
	}

	public String encodeEndpointIdentifier(EndpointIdentifier e) {
		String s = e.getLocalEndpointName();
		if (e.getDomainName() != null) {
			s += "@" + e.getDomainName();
		}
		return s;
	}

	public EndpointIdentifier[] decodeEndpointIdentifiers(String name) {
		String[] s = name.split(",");
		EndpointIdentifier[] endpointIdentifiers = new EndpointIdentifier[s.length];
		for (int i = 0; i < s.length; i++) {
			endpointIdentifiers[i] = decodeEndpointIdentifier(s[i]);
		}

		return endpointIdentifiers;
	}

	public String encodeEndpointIdentifiers(EndpointIdentifier[] endpointIdentifierList) {
		String msg = "";
		boolean first = true;
		for (int i = 0; i < endpointIdentifierList.length; i++) {
			if (first) {
				first = false;
			} else {
				msg += ",";
			}
			msg += encodeEndpointIdentifier(endpointIdentifierList[i]);
		}
		return msg;

	}

	public ReturnCode decodeReturnCode(int code) throws ParseException {
		switch (code) {
		case ReturnCode.CAS_SIGNALING_PROTOCOL_ERROR:
			return ReturnCode.CAS_Signaling_Protocol_Error;
		case ReturnCode.CONNECTION_WAS_DELETED:
			return ReturnCode.Connection_Was_Deleted;
		case ReturnCode.ENDPOINT_HAS_NO_DIGIT_MAP:
			return ReturnCode.Endpoint_Has_No_Digit_Map;
		case ReturnCode.ENDPOINT_INSUFFICIENT_RESOURCES:
			return ReturnCode.Endpoint_Insufficient_Resources;
		case ReturnCode.ENDPOINT_IS_RESTARTING:
			return ReturnCode.Endpoint_Is_Restarting;
		case ReturnCode.ENDPOINT_NOT_READY:
			return ReturnCode.Endpoint_Not_Ready;
		case ReturnCode.ENDPOINT_REDIRECTED:
			return ReturnCode.Endpoint_Redirected;
		case ReturnCode.ENDPOINT_UNKNOWN:
			return ReturnCode.Endpoint_Unknown;
		case ReturnCode.GATEWAY_CANNOT_DETECT_REQUESTED_EVENT:
			return ReturnCode.Gateway_Cannot_Detect_Requested_Event;
		case ReturnCode.GATEWAY_CANNOT_GENERATE_REQUESTED_SIGNAL:
			return ReturnCode.Gateway_Cannot_Generate_Requested_Signal;
		case ReturnCode.GATEWAY_CANNOT_SEND_SPECIFIED_ANNOUNCEMENT:
			return ReturnCode.Gateway_Cannot_Send_Specified_Announcement;
		case ReturnCode.INCOMPATIBLE_PROTOCOL_VERSION:
			return ReturnCode.Incompatible_Protocol_Version;
		case ReturnCode.INCORRECT_CONNECTION_ID:
			return ReturnCode.Incorrect_Connection_ID;
		case ReturnCode.INSUFFICIENT_BANDWIDTH:
			return ReturnCode.Insufficient_Bandwidth;
		case ReturnCode.INSUFFICIENT_BANDWIDTH_NOW:
			return ReturnCode.Insufficient_Bandwidth_Now;
		case ReturnCode.INSUFFICIENT_RESOURCES_NOW:
			return ReturnCode.Insufficient_Resources_Now;
		case ReturnCode.INTERNAL_HARDWARE_FAILURE:
			return ReturnCode.Internal_Hardware_Failure;
		case ReturnCode.INTERNAL_INCONSISTENCY_IN_LOCALCONNECTIONOPTIONS:
			return ReturnCode.Internal_Inconsistency_In_LocalConnectionOptions;
		case ReturnCode.MISSING_REMOTECONNECTIONDESCRIPTOR:
			return ReturnCode.Missing_RemoteConnectionDescriptor;
		case ReturnCode.NO_SUCH_EVENT_OR_SIGNAL:
			return ReturnCode.No_Such_Event_Or_Signal;
		case ReturnCode.PHONE_OFF_HOOK:
			return ReturnCode.Phone_Off_Hook;
		case ReturnCode.PHONE_ON_HOOK:
			return ReturnCode.Phone_On_Hook;
		case ReturnCode.PROTOCOL_ERROR:
			return ReturnCode.Protocol_Error;
		case ReturnCode.TRANSACTION_BEING_EXECUTED:
			return ReturnCode.Transaction_Being_Executed;
		case ReturnCode.TRANSACTION_EXECUTED_NORMALLY:
			return ReturnCode.Transaction_Executed_Normally;
		case ReturnCode.TRANSIENT_ERROR:
			return ReturnCode.Transient_Error;
		case ReturnCode.TRUNK_GROUP_FAILURE:
			return ReturnCode.Trunk_Group_Failure;
		case ReturnCode.UNKNOWN_CALL_ID:
			return ReturnCode.Unknown_Call_ID;
		case ReturnCode.UNKNOWN_EXTENSION_IN_LOCALCONNECTIONOPTIONS:
			return ReturnCode.Unknown_Extension_In_LocalConnectionOptions;
		case ReturnCode.UNKNOWN_OR_ILLEGAL_COMBINATION_OF_ACTIONS:
			return ReturnCode.Unknown_Or_Illegal_Combination_Of_Actions;
		case ReturnCode.UNRECOGNIZED_EXTENSION:
			return ReturnCode.Unrecognized_Extension;
		case ReturnCode.UNSUPPORTED_OR_INVALID_MODE:
			return ReturnCode.Unsupported_Or_Invalid_Mode;
		case ReturnCode.UNSUPPORTED_OR_UNKNOWN_PACKAGE:
			return ReturnCode.Unsupported_Or_Unknown_Package;
		default:
			// TODO: 0xx should be treated as response acknowledgement.
			if ((code > 99) && (code < 200))
				return (ReturnCode.Transaction_Being_Executed);
			else if ((code > 199) && code < 300)
				return (ReturnCode.Transaction_Executed_Normally);
			else if ((code > 299) && code < 400)
				return (ReturnCode.Endpoint_Redirected);
			else if ((code > 399) && (code > 500))
				return (ReturnCode.Transient_Error);
			else if ((code > 499) && (code < 1000))
				return (ReturnCode.Protocol_Error);
			else
				throw new ParseException("unknown response code: " + code, 0);
		}
	}

	public String encodeLocalOptionValueList(LocalOptionValue[] options) {
		String msg = "";
		boolean first = true;
		for (int i = 0; i < options.length; i++) {
			if (first) {
				first = false;
			} else {
				msg += ",";
			}
			String s = options[i].toString();
			if (s.indexOf("\n") != -1) {
				s = s.substring(0, s.indexOf("\n"));
			}
			msg += s;
		}
		return msg;
	}

	public String encodeNotificationRequestParms(NotificationRequestParms parms) {
		String msg = "X:" + parms.getRequestIdentifier() + "\n";
		if (parms.getSignalRequests() != null) {
			msg += "S:" + encodeEventNames(parms.getSignalRequests()) + "\n";
		}
		if (parms.getRequestedEvents() != null) {
			msg += "R:" + encodeRequestedEvents(parms.getRequestedEvents()) + "\n";
		}
		if (parms.getDetectEvents() != null) {
			msg += "T:" + encodeEventNames(parms.getDetectEvents()) + "\n";
		}
		if (parms.getDigitMap() != null) {
			msg += "D:" + parms.getDigitMap() + "\n";
		}
		return msg;
	}

	public String encodeRequestedEvent(RequestedEvent evt) {
		String s = evt.getEventName().getPackageName() + "/" + evt.getEventName().getEventIdentifier().getName();

		if (evt.getEventName().getConnectionIdentifier() != null) {
			s += '@' + evt.getEventName().getConnectionIdentifier().toString();
		}
		String parms = evt.getEventName().getEventIdentifier().getParms();
		RequestedAction[] actions = evt.getRequestedActions();

		if (actions != null) {
			String ac = encodeRequestedActions(actions);
			s += " (" + ac + ")";
		}

		if (parms != null) {
			s += " (" + parms + ")";
		}

		return s;
	}

	public String encodeRequestedEvents(RequestedEvent[] evts) {
		String s = "";
		for (int i = 0; i < evts.length; i++) {
			s += encodeRequestedEvent(evts[i]);
			if (i != evts.length - 1) {
				s += ',';
			}
		}
		return s;
	}

	public String encodeRequestedActions(RequestedAction[] actions) {
		String s = "";
		String d = "";
		for (int i = 0; i < actions.length; i++) {
			d = i == 0 ? "" : ",";
			s += d + encodeRequestedAction(actions[i]);
		}
		return s;
	}

	public String encodeRequestedAction(RequestedAction action) {
		String s = "";
		if (RequestedAction.EMBEDDED_NOTIFICATION_REQUEST != action.getRequestedAction()) {
			return action.toString();
		} else {
			EmbeddedRequest embeddedRequest = action.getEmbeddedRequest();
			s += "E(" + encodeEmbeddedRequest(embeddedRequest) + ")";
		}
		return s;
	}

	public String encodeConnectionParm(ConnectionParm parm) {
		int type = parm.getConnectionParmType();
		if (type == RegularConnectionParm.JITTER) {
			return "JI=" + parm.getConnectionParmValue();
		} else if (type == RegularConnectionParm.LATENCY) {
			return "LA=" + parm.getConnectionParmValue();
		} else if (type == RegularConnectionParm.OCTETS_RECEIVED) {
			return "OR=" + parm.getConnectionParmValue();
		} else if (type == RegularConnectionParm.OCTETS_SENT) {
			return "OS=" + parm.getConnectionParmValue();
		} else if (type == RegularConnectionParm.PACKETS_LOST) {
			return "PL=" + parm.getConnectionParmValue();
		} else if (type == RegularConnectionParm.PACKETS_RECEIVED) {
			return "PR=" + parm.getConnectionParmValue();
		} else if (type == RegularConnectionParm.PACKETS_SENT) {
			return "PS=" + parm.getConnectionParmValue();
		} else {
			return ((ExtendedConnectionParm) parm).getConnectionParmExtensionName() + "="
					+ parm.getConnectionParmValue();
		}

	}

	public ConnectionParm decodeConnectionParm(String parm) {
		String[] tokens = parm.split("=");

		String name = tokens[0].trim();
		String value = tokens[1].trim();

		if (name.equalsIgnoreCase("JI")) {
			return new RegularConnectionParm(RegularConnectionParm.JITTER, Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("LA")) {
			return new RegularConnectionParm(RegularConnectionParm.LATENCY, Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("OR")) {
			return new RegularConnectionParm(RegularConnectionParm.OCTETS_RECEIVED, Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("OS")) {
			return new RegularConnectionParm(RegularConnectionParm.OCTETS_SENT, Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("PL")) {
			return new RegularConnectionParm(RegularConnectionParm.PACKETS_LOST, Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("PR")) {
			return new RegularConnectionParm(RegularConnectionParm.PACKETS_RECEIVED, Integer.parseInt(value));
		} else if (name.equalsIgnoreCase("PS")) {
			return new RegularConnectionParm(RegularConnectionParm.PACKETS_SENT, Integer.parseInt(value));
		} else {
			return new ExtendedConnectionParm(name, Integer.parseInt(value));
		}
	}

	public String encodeConnectionParms(ConnectionParm[] parms) {
		String s = "";
		for (int i = 0; i < parms.length - 1; i++) {
			s += encodeConnectionParm(parms[i]) + ",";
		}

		s += encodeConnectionParm(parms[parms.length - 1]);
		return s;
	}

	public ConnectionParm[] decodeConnectionParms(String value) {
		String tokens[] = value.split(",");
		ConnectionParm[] parms = new ConnectionParm[tokens.length];

		for (int i = 0; i < tokens.length; i++) {
			parms[i] = decodeConnectionParm(tokens[i].trim());
		}

		return parms;
	}

	public ReasonCode decodeReasonCode(String value) {
		String[] tokens = value.split("\\s");

		int code = Integer.parseInt(tokens[0]);
		ReasonCode reasonCode = null;

		switch (code) {
		case ReasonCode.ENDPOINT_MALFUNCTIONING:
			reasonCode = ReasonCode.Endpoint_Malfunctioning;
			break;
		case ReasonCode.ENDPOINT_OUT_OF_SERVICE:
			reasonCode = ReasonCode.Endpoint_Out_Of_Service;
			break;
		case ReasonCode.ENDPOINT_STATE_IS_NOMINAL:
			reasonCode = ReasonCode.Endpoint_State_Is_Nominal;
			break;
		case ReasonCode.LOSS_OF_LOWER_LAYER_CONNECTIVITY:
			reasonCode = ReasonCode.Loss_Of_Lower_Layer_Connectivity;
			break;
		}

		return reasonCode;
	}

	public NotifiedEntity decodeNotifiedEntity(String value, boolean toMGW) throws ParseException {
		NotifiedEntity notifiedEntity = null;

		String localName = null;
		String domainName = null;
		int port = 0;

		String domainAndPort[] = null;

		try {
			String tokens[] = value.split("@");

			if (tokens.length == 2) {
				localName = tokens[0];
				domainAndPort = tokens[1].split(":");
				domainName = domainAndPort[0];
			} else {
				domainAndPort = tokens[0].split(":");
				domainName = domainAndPort[0];
				return new NotifiedEntity(domainName);
			}

			if (domainAndPort.length == 2) {
				port = Integer.parseInt(domainAndPort[1]);
			} else if (toMGW) {
				// See 3.5 of RFC3435. "by the Call Agents, to the default
				// MGCP port for gateways, 2427."
				port = 2427;
			} else {
				port = 2727;
			}

			notifiedEntity = new NotifiedEntity(localName, domainName, port);
		} catch (Exception ex) {
			throw new ParseException("unable to parse the " + value + " Message = " + ex.getMessage(), 0);
		}

		return notifiedEntity;
	}

	public String encodeNotifiedEntity(NotifiedEntity n) {
		String s = "";

		if (n.getLocalName() != null) {
			s += n.getLocalName() + "@";
		}

		if (n.getDomainName() != null) {
			s += n.getDomainName();
		}

		if (n.getPortNumber() > 0) {
			s += ":" + n.getPortNumber();
		}
		return s;
	}
}

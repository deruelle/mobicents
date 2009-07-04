package org.openxdm.xcap.server.slee.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.httpclient.util.EncodingUtil;
import org.openxdm.xcap.common.error.InternalServerErrorException;

public class RFC2617ChallengeParamGenerator {

	public String getNonce(String seed) throws InternalServerErrorException {
		if (nonceDigestSecret == null) {
			nonceDigestSecret = generateOpaque();
		}
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("failed to get instance of MD5 digest, used in "+RFC2617AuthQopDigest.class.getName());
		}
		return AsciiHexStringEncoder.encode(messageDigest.digest(EncodingUtil.getAsciiBytes(seed+":"+nonceDigestSecret)));
	}

	private String nonceDigestSecret;
	
	private final SecureRandom opaqueGenerator = new SecureRandom();

	public String generateOpaque() {
		synchronized (opaqueGenerator) {
			return Integer.toHexString(opaqueGenerator.nextInt());
		}
	}
}

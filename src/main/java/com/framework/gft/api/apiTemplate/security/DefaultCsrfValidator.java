package com.framework.gft.api.apiTemplate.security;

import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 
 * @author a73s
 *
 */
public class DefaultCsrfValidator implements CsrfValidator {

	final static Logger logger = Logger.getLogger(DefaultCsrfValidator.class);

	private static final String HASHING_ALGORITHM = "SHA-256";
	private static final String MAIN_CIPHER_ALGORITHM = "AES";
	private static final String SUB_CIPHER_ALGORITHM = "CBC/PKCS5Padding";
	private static final String CIPHER_ALGORITHM = MAIN_CIPHER_ALGORITHM + "/" + SUB_CIPHER_ALGORITHM;
	private static final Long DEFAULT_REQUEST_TIMEOUT_IN_MS = 10000L;

	private final String cipherKey;
	private final long requestTimeoutMs;

	public DefaultCsrfValidator(String cipherKey) {
		this(cipherKey, DEFAULT_REQUEST_TIMEOUT_IN_MS);
	}

	public DefaultCsrfValidator(String cipherKey, Long requestTimeoutMs) {
		this.cipherKey = cipherKey;
		this.requestTimeoutMs = requestTimeoutMs;
	}

	public static String generateCsrfIv() {
		SecureRandom sr;
		try {
			sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new RuntimeException("error in SecureRandom.getInstance()");
		}
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	@Override
	public boolean check(HttpServletRequest request) {
		Long requestTime = null;
		try {
			requestTime = getRequestTimeMs(request);
		} catch (Exception e) {
			logger.warn("DefaultCsrfValidator failed check. Bad key?", e);
		}
		if (requestTime == null) {
			return false;
		}
		return System.currentTimeMillis() < requestTime + requestTimeoutMs;
	}

	public String generateCsrfToken(String cipherIv) {
		try {
			Cipher aes = getCipher(Cipher.ENCRYPT_MODE, cipherIv);
			return Base64.getEncoder().encodeToString(aes.doFinal(String.valueOf(System.currentTimeMillis()).getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Long getRequestTimeMs(HttpServletRequest request) {
		String csrfToken = getParameterOrHeader(request, SecurityParameters.CSRF_TOKEN);
		String cipherIv = getParameterOrHeader(request, SecurityParameters.CSRF_IV);
		try {
			Cipher aes = getCipher(Cipher.DECRYPT_MODE, cipherIv);
			return Long.parseLong(new String(aes.doFinal(Base64.getDecoder().decode(csrfToken))));
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getParameterOrHeader(HttpServletRequest request, String key) {
		String value = request.getParameter(key);
		return value != null ? value : request.getHeader(key);
	}

	private SecretKeySpec computeKey(String cipherKey) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(HASHING_ALGORITHM);
		digest.update(cipherKey.getBytes());
		return new SecretKeySpec(digest.digest(), 0, 16, MAIN_CIPHER_ALGORITHM);
	}

	private Cipher getCipher(int mode, String cipherIv) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher aes = Cipher.getInstance(CIPHER_ALGORITHM);
		aes.init(mode, computeKey(cipherKey), new IvParameterSpec(cipherIv.getBytes(), 0, 16));
		return aes;
	}

}

package com.framework.gft.api.apiTemplate.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.framework.gft.api.apiTemplate.router.CachingHttpServletRequest;

/**
 * 
 * @author a73s
 *
 */
public class DefaultSignatureValidator implements SignatureValidator {

	final static Logger logger = Logger.getLogger(DefaultSignatureValidator.class);

	private static final String HASHING_ALGORITHM = "SHA-256";
	private String salt;

	public DefaultSignatureValidator(String salt) {
		this.salt = salt;
	}

	public boolean checkHexSignature(Map<String, String> params, HttpEntity entity, String candidateSignature) {
		if (getHexSignatureWithoutSettingParameterOrder(params, entity).equals(candidateSignature)) {
			if (!params.isEmpty()) {
				logger.warn("Successfully checked signature without checking parameter order");
			}
			return true;
		}
		return getHexSignature(params, entity).equals(candidateSignature);
	}

	private boolean checkHexSignatureMulti(HttpServletRequest request, HttpEntity entity) {
		String parameter = getParameterOrHeader(request, SecurityParameters.SIGNATURE);
		Map<String, String> params = multiToSingle(request.getParameterMap());
		return checkHexSignature(params, entity, parameter);
	}

	@Override
	public SecurityValidationResult validate(HttpServletRequest request) {
		if (isFormPost(request) || "GET".equalsIgnoreCase(request.getMethod())) {
			boolean result = checkHexSignatureMulti(request, null);
			return new SecurityValidationResult(request, result, null);
		}

		HttpEntity entity;
		try {
			Optional<CachingHttpServletRequest> cachingRequestOptional = CachingHttpServletRequest.get(request);
			if (!cachingRequestOptional.isPresent()) {
				cachingRequestOptional = Optional.of(CachingHttpServletRequest.getOrCreate(request));
				request = cachingRequestOptional.get();
			}
			entity = new ByteArrayEntity(cachingRequestOptional.get().getContent());
		} catch (IOException e) {
			throw new RuntimeException();
		}

		boolean result = checkHexSignatureMulti(request, entity);
		return new SecurityValidationResult(request, result, null);
	}

	private static String getParameterOrHeader(HttpServletRequest request, String key) {
		String value = request.getParameter(key);
		return value != null ? value : request.getHeader(key);
	}

	private boolean isFormPost(HttpServletRequest request) {
		String contentType = request.getContentType();
		boolean isFormContent = contentType != null && contentType.contains(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
		return isFormContent && "POST".equalsIgnoreCase(request.getMethod());
	}

	public byte[] sign(Map<String, String> map) {
		return signWithoutSettingParameterOrder(new TreeMap<>(map), null);
	}

	public byte[] sign(Map<String, String> map, HttpEntity entity) {
		return signWithoutSettingParameterOrder(new TreeMap<>(map), entity);
	}

	private byte[] signWithoutSettingParameterOrder(Map<String, String> map, HttpEntity entity) {
		// TODO signature length should be constant. currently signature length
		// is proportional to number of parameters.
		ByteArrayOutputStream signature = new ByteArrayOutputStream();
		try {
			MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);
			for (Entry<String, String> entry : map.entrySet()) {
				String parameterName = entry.getKey();
				if (parameterName.equals(SecurityParameters.SIGNATURE) || "submitAction".equals(parameterName)) {
					continue;
				}
				String value = entry.getValue();
				String keyValue = parameterName.concat(value == null ? "" : value);
				String keyValueSalt = keyValue.concat(salt);
				md.update(keyValueSalt.getBytes(StandardCharsets.UTF_8));
				signature.write(md.digest());
			}

			if (entity != null) {
				byte[] bytes = EntityUtils.toByteArray(entity);
				md.update(bytes);
				md.update(salt.getBytes(StandardCharsets.UTF_8));
				signature.write(md.digest());
			}
		} catch (IOException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return signature.toByteArray();
	}

	public String getHexSignature(Map<String, String> params, HttpEntity entity) {
		byte[] signature = sign(params, entity);
		return Hex.encodeHexString(signature);
	}

	public String getHexSignature(Map<String, String> params) {
		byte[] signature = sign(params);
		return Hex.encodeHexString(signature);
	}

	private String getHexSignatureWithoutSettingParameterOrder(Map<String, String> params, HttpEntity entity) {
		byte[] signature = signWithoutSettingParameterOrder(params, entity);
		return Hex.encodeHexString(signature);
	}

	private Map<String, String> multiToSingle(Map<String, String[]> data) {
		Map<String, String> map = new HashMap<>();
		for (Entry<String, String[]> entry : data.entrySet()) {
			map.put(entry.getKey(), entry.getValue()[0]);
		}
		return map;
	}

}

package com.framework.gft.api.apiTemplate.router;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author a73s
 *
 */
public class CachingHttpServletRequest extends HttpServletRequestWrapper {

	private byte[] content;

	public static Optional<CachingHttpServletRequest> get(HttpServletRequest request) {
		return getWrappedRequest(request, CachingHttpServletRequest.class);
	}

	public static CachingHttpServletRequest getOrCreate(HttpServletRequest request) {
		Optional<CachingHttpServletRequest> cachedRequestOptional = getWrappedRequest(request, CachingHttpServletRequest.class);
		if (cachedRequestOptional.isPresent()) {
			return cachedRequestOptional.get();
		}

		return new CachingHttpServletRequest(request);
	}

	private CachingHttpServletRequest(HttpServletRequest request) {
		super(request);
	}

	public byte[] getContent() throws IOException {
		if (content != null) {
			return content;
		}
		content = EntityUtils.toByteArray(new InputStreamEntity(super.getInputStream()));
		return content;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		byte[] content = getContent();
		return new CachingServletInputStream(new ByteArrayInputStream(content));
	}

	@Override
	public BufferedReader getReader() throws IOException {
		String encoding = super.getCharacterEncoding();
		Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.ISO_8859_1;
		return new BufferedReader(new InputStreamReader(getInputStream(), charset));
	}

	public static <T> Optional<T> getWrappedRequest(ServletRequest request, Class<T> requiredType) {
		if (requiredType.isInstance(request)) {
			return Optional.of(requiredType.cast(request));
		}
		if (request instanceof ServletRequestWrapper) {
			return getWrappedRequest(((ServletRequestWrapper) request).getRequest(), requiredType);
		}
		return Optional.empty();
	}
}

package com.framework.gft.api.apiTemplate;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * 
 * @author a73s
 *
 */
public abstract class BaseApiRquest {

	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

	static HttpResponse response;
	static String payload;

	public static void setHeaders(HttpRequestBase request, Map<String, String> headers) {
		if (headers != null && !headers.isEmpty())
			for (Entry<String, String> element : headers.entrySet())
				request.setHeader(element.getKey(), element.getValue());

		request.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
	}

	public static void setHeaders(HttpEntityEnclosingRequestBase request, Map<String, String> headers) {
		if (headers != null && !headers.isEmpty())
			for (Entry<String, String> element : headers.entrySet())
				request.setHeader(element.getKey(), element.getValue());

		request.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
	}

}

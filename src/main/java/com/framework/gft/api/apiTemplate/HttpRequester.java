package com.framework.gft.api.apiTemplate;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.framework.gft.api.apiTemplate.utils.HttpMethod;
/**
 * 
 * @author a73s
 *
 */
public class HttpRequester extends BaseApiRquest {

	final static Logger logger = Logger.getLogger(HttpRequester.class);

	@HttpMethod(value = "GET")
	public static HttpResponse get(String url, Map<String, String> headers) {

		try {
			HttpClient httpClient = HttpClientBuilder.create().build();

			HttpGet request = new HttpGet(url);
			setHeaders(request, headers);
			response = httpClient.execute(request);
			return response;
		} catch (IOException e) {
			logger.error(e);
			return null;
		}

	}

	@HttpMethod(value = "POST")
	public static HttpResponse post(String url, String json, Map<String, String> headers) {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();

			HttpPost request = new HttpPost(url);
			setHeaders(request, headers);
			request.setEntity(new StringEntity(json));
			response = httpClient.execute(request);
			return response;

		} catch (IOException e) {
			logger.error(e);
			return null;
		}

	}

	@HttpMethod(value = "PUT")
	public static HttpResponse put(String url, String json, Map<String, String> headers) {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();

			HttpPut request = new HttpPut(url);
			setHeaders(request, headers);
			request.setEntity(new StringEntity(json));
			response = httpClient.execute(request);
			return response;

		} catch (IOException e) {
			logger.error(e);
			return null;
		}

	}

}

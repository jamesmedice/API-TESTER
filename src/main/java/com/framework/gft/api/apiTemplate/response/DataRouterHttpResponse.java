package com.framework.gft.api.apiTemplate.response;

import java.util.List;
import java.util.function.Consumer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * 
 * @author a73s
 *
 */
public class DataRouterHttpResponse {

	final static Logger logger = Logger.getLogger(DataRouterHttpResponse.class);

	private final HttpResponse response;
	private final List<Cookie> cookies;
	private int statusCode;
	private String entity;
	private JSONObject jsonObject;

	public DataRouterHttpResponse(HttpResponse response, HttpClientContext context, Consumer<HttpEntity> httpEntityConsumer) {
		this.response = response;
		this.cookies = context.getCookieStore().getCookies();
		if (response != null) {
			this.statusCode = response.getStatusLine().getStatusCode();
			this.entity = "";

			HttpEntity httpEntity = response.getEntity();
			if (httpEntity == null) {
				return;
			}
			if (httpEntityConsumer != null) {
				httpEntityConsumer.accept(httpEntity);
				return;
			}
			try {
				this.entity = EntityUtils.toString(httpEntity);

				this.jsonObject = new JSONObject(entity);

			} catch (Exception e) {
				logger.error("Exception occurred while reading HTTP response entity", e);
			} finally {
				EntityUtils.consumeQuietly(httpEntity);
			}
		}
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getEntity() {
		return entity;
	}

	public Header getFirstHeader(String name) {
		return response.getFirstHeader(name);
	}

	public Header[] getHeaders(String name) {
		return response.getHeaders(name);
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

}

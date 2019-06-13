package com.framework.gft.api.apiTemplate;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.framework.gft.api.apiTemplate.provider.BundleProvider;
import com.framework.gft.api.apiTemplate.provider.JsonPayloadProvider;
import com.framework.gft.api.apiTemplate.response.DataRouterHttpResponse;
import com.framework.gft.api.apiTemplate.router.DataRouterHttpClient;
import com.framework.gft.api.apiTemplate.router.DataRouterHttpClientBuilder;
import com.framework.gft.api.apiTemplate.router.DataRouterHttpClientConfig;
import com.framework.gft.api.apiTemplate.router.DataRouterHttpRequest;
import com.framework.gft.api.apiTemplate.router.DataRouterHttpRequest.HttpRequestMethod;

public class HttpActions {

	final static Logger logger = Logger.getLogger(HttpActions.class);

	private static final String HOST = "host";

	private HttpResponse httpResponse;

	private JSONObject jsonObject;

	private String url;

	private DataRouterHttpClient client;
	private DataRouterHttpClientConfig config;
	private DataRouterHttpRequest request;
	private DataRouterHttpResponse dataRouterHttpResponse;

	public HttpActions(String urlPath) {
		this(urlPath, false);
	}

	public HttpActions(String urlPath, boolean router) {

		if (router) {
			setRouterClient();
		}

		this.url = BundleProvider.getPropertiesFromBundle(HOST) + urlPath;

		logger.info(url);
	}

	public void get(Map<String, String> headers) {
		try {

			httpResponse = HttpRequester.get(url, headers);
			String result = EntityUtils.toString(httpResponse.getEntity());
			jsonObject = new JSONObject(result);

			logger.info(jsonObject);
			logger.info(result);

		} catch (Exception e) {
			jsonObject = null;
			logger.error(e);
		}
	}

	public void post(String jsonFileName, Map<String, String> headers) {
		try {

			if (headers == null)
				headers = new HashMap<String, String>();

			String json = getJson(jsonFileName);

			logger.info(json);

			httpResponse = HttpRequester.post(url, json, headers);
			String result = EntityUtils.toString(httpResponse.getEntity());
			jsonObject = new JSONObject(result);

			logger.info(jsonObject);
			logger.info(result);

		} catch (Exception e) {
			jsonObject = null;

			logger.error(e);
		}
	}

	public void routerGet()   {
		request = new DataRouterHttpRequest(HttpRequestMethod.GET, url, true);
		dataRouterHttpResponse = client.execute(request);

		logger.info(dataRouterHttpResponse.getJsonObject());
	}

	public void routerPost(String jsonFileName) {
		String json = getJson(jsonFileName);

		request = new DataRouterHttpRequest(HttpRequestMethod.POST, url, true);
		request.setEntity(json, ContentType.APPLICATION_JSON);
		dataRouterHttpResponse = client.execute(request);

		logger.info(dataRouterHttpResponse.getJsonObject());
	}

	public int getRouterStatusCode() {
		return dataRouterHttpResponse.getStatusCode();
	}

	public String getRouterEntity() {
		return dataRouterHttpResponse.getEntity();
	}

	public JSONObject getRouterJson() {
		logger.info(dataRouterHttpResponse.getJsonObject());
		return dataRouterHttpResponse.getJsonObject();
	}

	public int getStatusCode() {
		logger.info(httpResponse.getStatusLine());
		return httpResponse.getStatusLine().getStatusCode();
	}

	public String getContentType() {
		logger.info(ContentType.getOrDefault(httpResponse.getEntity()).getMimeType());
		return ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
	}

	private String getJson(String jsonFileName) {
		JsonPayloadProvider provider = new JsonPayloadProvider();
		String json = provider.getJsonFromFile(jsonFileName);
		return json;
	}

	private void setRouterClient() {
		config = new DataRouterHttpClientConfig() {

			@Override
			public String getDtoTypeParameterName() {
				return "";
			}

			@Override
			public String getDtoParameterName() {
				return "";
			}
		};
		client = new DataRouterHttpClientBuilder().setConfig(config).build();
	}
}

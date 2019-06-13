package com.framework.gft.api.apiTemplate.router;

import java.net.URI;

import com.framework.gft.api.apiTemplate.router.DataRouterHttpRequest.HttpRequestMethod;

/**
 * 
 * @author a73s
 *
 */
public class DataRouterHttpRequestBuilder {

	private final DataRouterHttpClientSettings settings;

	public DataRouterHttpRequestBuilder(DataRouterHttpClientSettings settings) {
		this.settings = settings;
	}

	public DataRouterHttpRequest createGet(String path) {
		return new DataRouterHttpRequest(HttpRequestMethod.GET, buildUrl(path), true);
	}

	public DataRouterHttpRequest createPost(String path) {
		return new DataRouterHttpRequest(HttpRequestMethod.POST, buildUrl(path), false);
	}

	public DataRouterHttpRequest createPut(String path) {
		return new DataRouterHttpRequest(HttpRequestMethod.PUT, buildUrl(path), true);
	}

	public DataRouterHttpRequest createDelete(String path) {
		return new DataRouterHttpRequest(HttpRequestMethod.DELETE, buildUrl(path), true);
	}

	public String buildUrl(String path) {
		URI endpointUrl = settings.getEndpointUrl();
		URI finalUrl = URI.create(endpointUrl + "/" + path);
		return finalUrl.normalize().toString();
	}

}

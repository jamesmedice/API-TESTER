package com.framework.gft.api.apiTemplate.router;

import java.net.URI;

/**
 * 
 * @author a73s
 *
 */
public interface DataRouterHttpClientSettings {

	URI getEndpointUrl();

	String getApiKey();

	String getPrivateKey();

}
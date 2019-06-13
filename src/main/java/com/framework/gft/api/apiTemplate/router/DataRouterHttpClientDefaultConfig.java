package com.framework.gft.api.apiTemplate.router;

/**
 * 
 * @author a73s
 *
 */
public class DataRouterHttpClientDefaultConfig implements DataRouterHttpClientConfig {

	@Override
	public String getDtoParameterName() {
		return "dataTransferObject";
	}

	@Override
	public String getDtoTypeParameterName() {
		return "dataTransferObjectType";
	}

}

package com.framework.gft.api.apiTemplate.context;

import com.framework.gft.api.apiTemplate.response.DataRouterHttpResponse;
import com.framework.gft.api.apiTemplate.router.DataRouterHttpRequest;
import com.framework.gft.api.apiTemplate.router.exception.DataRouterHttpException;
import com.framework.gft.api.apiTemplate.router.exception.DataRouterHttpResponseException;
/**
 * 
 * @author a73s
 *
 */
public class DataRouterHttpContext {

	public final DataRouterHttpRequest request;
	public final DataRouterHttpResponse response;
	public final DataRouterHttpException exception;

	public DataRouterHttpContext(DataRouterHttpRequest request, DataRouterHttpResponse response, DataRouterHttpException exception) {
		this.request = request;
		this.exception = exception;

		if (exception instanceof DataRouterHttpResponseException)
			this.response = ((DataRouterHttpResponseException) exception).getResponse();
		else
			this.response = response;

	}

}

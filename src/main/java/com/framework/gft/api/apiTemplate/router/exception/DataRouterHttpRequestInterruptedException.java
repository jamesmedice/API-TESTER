package com.framework.gft.api.apiTemplate.router.exception;

/**
 * 
 * @author a73s
 *
 */
@SuppressWarnings("serial")
public class DataRouterHttpRequestInterruptedException extends DataRouterHttpException {

	public DataRouterHttpRequestInterruptedException(Exception ex, long requestStartTimeMs) {
		super("HTTP request interrupted after " + (System.currentTimeMillis() - requestStartTimeMs) + "ms", ex);
	}

}

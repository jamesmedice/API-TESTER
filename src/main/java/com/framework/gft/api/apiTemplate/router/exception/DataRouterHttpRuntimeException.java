package com.framework.gft.api.apiTemplate.router.exception;

/**
 * 
 * @author a73s
 *
 */
@SuppressWarnings("serial")
public class DataRouterHttpRuntimeException extends RuntimeException {

	public DataRouterHttpRuntimeException(Exception exception) {
		super(exception);
	}

	public DataRouterHttpRuntimeException(DataRouterHttpException exception) {
		super(exception.getMessage(), exception.getCause());
	}
}
